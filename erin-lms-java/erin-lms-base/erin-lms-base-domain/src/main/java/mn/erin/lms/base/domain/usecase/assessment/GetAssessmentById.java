package mn.erin.lms.base.domain.usecase.assessment;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.base.domain.model.assessment.Assessment;
import mn.erin.lms.base.domain.model.assessment.AssessmentId;
import mn.erin.lms.base.domain.model.assessment.Quiz;
import mn.erin.lms.base.aim.user.Instructor;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.repository.QuizRepository;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.assessment.dto.AssessmentDto;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Authorized(users = { Instructor.class })
public class GetAssessmentById extends AssessmentUseCase<String, AssessmentDto>
{
  private final QuizRepository quizRepository;

  public GetAssessmentById(LmsRepositoryRegistry lmsRepositoryRegistry,
      LmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
    this.quizRepository = lmsRepositoryRegistry.getQuizRepository();
  }

  @Override
  protected AssessmentDto executeImpl(String assessmentId) throws UseCaseException
  {
    try
    {
      Assessment assessment = assessmentRepository.findById(AssessmentId.valueOf(assessmentId));
      Quiz quiz = quizRepository.fetchById(assessment.getQuizId());
      return convert(assessment, quiz);
    }
    catch (LmsRepositoryException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }
  }
}

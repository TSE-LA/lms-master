package mn.erin.lms.base.domain.usecase.assessment;

import org.apache.commons.lang3.Validate;

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
import mn.erin.lms.base.domain.usecase.assessment.dto.UpdateAssessmentInput;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Authorized(users = { Instructor.class })
public class UpdateAssessment extends AssessmentUseCase<UpdateAssessmentInput, AssessmentDto>
{
  private final QuizRepository quizRepository;

  public UpdateAssessment(LmsRepositoryRegistry lmsRepositoryRegistry,
      LmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
    this.quizRepository = lmsRepositoryRegistry.getQuizRepository();
  }

  @Override
  protected AssessmentDto executeImpl(UpdateAssessmentInput input) throws UseCaseException
  {
    Validate.notNull(input);

    try
    {
      Assessment assessment = assessmentRepository.update(AssessmentId.valueOf(input.getId()), input.getName(), input.getDescription());
      Quiz quiz = quizRepository.fetchById(assessment.getQuizId());
      return convert(assessment, quiz);
    }
    catch (LmsRepositoryException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }
  }
}

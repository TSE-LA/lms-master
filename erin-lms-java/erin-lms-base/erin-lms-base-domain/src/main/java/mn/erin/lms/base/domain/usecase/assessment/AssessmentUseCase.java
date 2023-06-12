package mn.erin.lms.base.domain.usecase.assessment;

import java.time.ZoneId;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.domain.model.assessment.Assessment;
import mn.erin.lms.base.domain.model.assessment.AssessmentId;
import mn.erin.lms.base.domain.model.assessment.Quiz;
import mn.erin.lms.base.domain.repository.AssessmentRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.LmsUseCase;
import mn.erin.lms.base.domain.usecase.assessment.dto.AssessmentDto;

import static java.util.Date.from;

/**
 * @author Bat-Erdene Tsogoo.
 */
public abstract class AssessmentUseCase<I, O> extends LmsUseCase<I, O>
{

  protected AssessmentRepository assessmentRepository;

  public AssessmentUseCase(LmsRepositoryRegistry lmsRepositoryRegistry,
      LmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
    this.assessmentRepository = lmsRepositoryRegistry.getAssessmentRepository();
  }

  protected AssessmentDto convert(Assessment assessment, Quiz quiz)
  {
    AssessmentDto dto = new AssessmentDto();

    dto.setName(assessment.getName());
    dto.setId(assessment.getAssessmentId().getId());
    dto.setQuizId(assessment.getQuizId().getId());
    dto.setAuthorId(assessment.getAuthorId().getId());
    dto.setCreatedDate(from(assessment.getCreatedDate().atZone(ZoneId.systemDefault()).toInstant()));
    dto.setModifiedDate(from(assessment.getModifiedDate().atZone(ZoneId.systemDefault()).toInstant()));
    dto.setStatus(assessment.getStatus().name());
    dto.setQuestionCount(quiz.getQuestions().size());
    dto.setDescription(assessment.getDescription());

    return dto;
  }

  protected Assessment getAssessment(AssessmentId assessmentId) throws UseCaseException
  {
    try
    {
      return assessmentRepository.findById(assessmentId);
    }
    catch (LmsRepositoryException e)
    {
      throw new UseCaseException("The assessment with the ID: [" + assessmentId.getId() + "] does not exist!");

    }
  }
}

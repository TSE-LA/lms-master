package mn.erin.lms.base.domain.usecase.assessment;

import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.base.domain.model.assessment.AssessmentId;
import mn.erin.lms.base.aim.user.Instructor;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Authorized(users = { Instructor.class })
public class DeleteAssessment extends AssessmentUseCase<String, Boolean>
{

  public DeleteAssessment(LmsRepositoryRegistry lmsRepositoryRegistry,
      LmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
  }

  @Override
  protected Boolean executeImpl(String assessmentId)
  {
    return assessmentRepository.delete(AssessmentId.valueOf(assessmentId));
  }
}

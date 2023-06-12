package mn.erin.lms.base.domain.usecase.enrollment.dto;

import org.apache.commons.lang3.Validate;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class DeleteLearnerEnrollmentsInput
{
  private final String learnerId;
  private String enrollmentState;

  public DeleteLearnerEnrollmentsInput(String learnerId)
  {
    this.learnerId = Validate.notBlank(learnerId);
  }

  public void setEnrollmentState(String enrollmentState)
  {
    this.enrollmentState = enrollmentState;
  }

  public String getLearnerId()
  {
    return learnerId;
  }

  public String getEnrollmentState()
  {
    return enrollmentState;
  }
}

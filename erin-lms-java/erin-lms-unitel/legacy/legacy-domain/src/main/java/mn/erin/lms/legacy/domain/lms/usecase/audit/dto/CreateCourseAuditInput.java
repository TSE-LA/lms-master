package mn.erin.lms.legacy.domain.lms.usecase.audit.dto;

import org.apache.commons.lang3.Validate;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class CreateCourseAuditInput
{
  private final String courseId;
  private final String learnerId;

  public CreateCourseAuditInput(String courseId, String learnerId)
  {
    this.courseId = Validate.notBlank(courseId, "Course ID cannot be null or blank!");
    this.learnerId = Validate.notBlank(learnerId, "Learner ID cannot be null or blank!");
  }

  public String getCourseId()
  {
    return courseId;
  }

  public String getLearnerId()
  {
    return learnerId;
  }
}

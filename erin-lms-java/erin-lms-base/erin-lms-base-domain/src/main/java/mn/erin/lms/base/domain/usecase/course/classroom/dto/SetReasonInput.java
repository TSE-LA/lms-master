package mn.erin.lms.base.domain.usecase.course.classroom.dto;

/**
 * @author Munkh
 */
public class SetReasonInput
{
  private final String courseId;
  private final String reason;

  public SetReasonInput(String courseId, String reason)
  {
    this.courseId = courseId;
    this.reason = reason;
  }

  public String getCourseId()
  {
    return courseId;
  }

  public String getReason()
  {
    return reason;
  }
}

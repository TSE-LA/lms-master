package mn.erin.lms.base.domain.usecase.course.dto;

/**
 * @author Munkh
 */
public class UpdateStateInput
{
  private final String courseId;
  private final String state;
  private boolean rollback;
  private String reason;

  public UpdateStateInput(String courseId, String state, boolean rollback, String reason)
  {
    this.courseId = courseId;
    this.state = state;
    this.rollback = rollback;
    this.reason = reason;
  }

  public String getCourseId()
  {
    return courseId;
  }

  public String getState()
  {
    return state;
  }

  public boolean isRollback()
  {
    return rollback;
  }

  public void setRollback(boolean rollback)
  {
    this.rollback = rollback;
  }

  public String getReason()
  {
    return reason;
  }

  public void setReason(String reason)
  {
    this.reason = reason;
  }
}

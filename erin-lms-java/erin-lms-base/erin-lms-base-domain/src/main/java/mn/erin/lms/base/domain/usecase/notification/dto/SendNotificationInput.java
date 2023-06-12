package mn.erin.lms.base.domain.usecase.notification.dto;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class SendNotificationInput
{
  private final String courseId;
  private String message;

  public SendNotificationInput(String courseId)
  {
    this.courseId = courseId;
  }

  public SendNotificationInput(String courseId, String message)
  {
    this.courseId = courseId;
    this.message = message;
  }

  public String getCourseId()
  {
    return courseId;
  }

  public String getMessage()
  {
    return message;
  }
}

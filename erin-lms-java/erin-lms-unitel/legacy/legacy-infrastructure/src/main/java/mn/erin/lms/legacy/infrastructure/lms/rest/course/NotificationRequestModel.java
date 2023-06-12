package mn.erin.lms.legacy.infrastructure.lms.rest.course;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class NotificationRequestModel
{
  private boolean sendEmail;
  private boolean sendSms;
  private String memo;

  public boolean isSendEmail()
  {
    return sendEmail;
  }

  public boolean isSendSms()
  {
    return sendSms;
  }

  public String getMemo()
  {
    return memo;
  }
}

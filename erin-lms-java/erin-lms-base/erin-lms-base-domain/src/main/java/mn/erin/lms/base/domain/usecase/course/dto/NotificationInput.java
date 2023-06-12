package mn.erin.lms.base.domain.usecase.course.dto;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class NotificationInput
{
  private boolean sendEmail;
  private boolean sendSms;
  private String note;

  public NotificationInput(boolean sendEmail, boolean sendSms, String note)
  {
    this.sendEmail = sendEmail;
    this.sendSms = sendSms;
    this.note = note;
  }

  public boolean isSendEmail()
  {
    return sendEmail;
  }

  public void setSendEmail(boolean sendEmail)
  {
    this.sendEmail = sendEmail;
  }

  public boolean isSendSms()
  {
    return sendSms;
  }

  public void setSendSms(boolean sendSms)
  {
    this.sendSms = sendSms;
  }

  public String getNote()
  {
    return note;
  }

  public void setNote(String note)
  {
    this.note = note;
  }
}

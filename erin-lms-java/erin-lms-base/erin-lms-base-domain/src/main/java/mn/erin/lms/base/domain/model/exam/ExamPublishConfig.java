package mn.erin.lms.base.domain.model.exam;

import java.util.Date;
import java.util.Objects;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.model.ValueObject;

/**
 * @author Temuulen Naranbold
 */
public class ExamPublishConfig implements ValueObject<ExamPublishConfig>
{
  private Date publishDate;
  private String publishTime;
  private boolean sendEmail;
  private boolean sendSMS;
  private String mailText;
  private String smsText;

  public ExamPublishConfig(Date publishDate, String publishTime, boolean sendEmail, boolean sendSMS)
  {
    this.publishDate = Objects.requireNonNull(publishDate);
    this.publishTime = Validate.notBlank(publishTime);
    this.sendEmail = sendEmail;
    this.sendSMS = sendSMS;
  }

  public Date getPublishDate()
  {
    return publishDate;
  }

  public void setPublishDate(Date publishDate)
  {
    this.publishDate = Objects.requireNonNull(publishDate);
  }

  public String getPublishTime()
  {
    return publishTime;
  }

  public void setPublishTime(String publishTime)
  {
    this.publishTime = Validate.notBlank(publishTime);
  }

  public boolean isSendEmail()
  {
    return sendEmail;
  }

  public void setSendEmail(boolean sendEmail)
  {
    this.sendEmail = sendEmail;
  }

  public boolean isSendSMS()
  {
    return sendSMS;
  }

  public void setSendSMS(boolean sendSMS)
  {
    this.sendSMS = sendSMS;
  }

  public String getMailText()
  {
    return mailText;
  }

  public void setMailText(String mailText)
  {
    this.mailText = Validate.notBlank(mailText);
  }

  public String getSmsText()
  {
    return smsText;
  }

  public void setSmsText(String smsText)
  {
    this.smsText = Validate.notBlank(smsText);
  }

  @Override
  public boolean sameValueAs(ExamPublishConfig other)
  {
    return false;
  }
}

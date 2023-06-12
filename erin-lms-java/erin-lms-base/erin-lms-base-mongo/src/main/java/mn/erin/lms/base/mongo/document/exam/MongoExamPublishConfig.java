package mn.erin.lms.base.mongo.document.exam;

import java.util.Date;

/**
 * @author Temuulen Naranbold
 */
public class MongoExamPublishConfig
{
  private Date publishDate;
  private String publishTime;
  private boolean sendEmail;
  private boolean sendSMS;
  private String mailText;
  private String smsText;

  public MongoExamPublishConfig()
  {
    /*Mongo need an empty constructor*/
  }

  public Date getPublishDate()
  {
    return publishDate;
  }

  public void setPublishDate(Date publishDate)
  {
    this.publishDate = publishDate;
  }

  public String getPublishTime()
  {
    return publishTime;
  }

  public void setPublishTime(String publishTime)
  {
    this.publishTime = publishTime;
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
    this.mailText = mailText;
  }

  public String getSmsText()
  {
    return smsText;
  }

  public void setSmsText(String smsText)
  {
    this.smsText = smsText;
  }
}

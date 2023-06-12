package mn.erin.lms.base.domain.usecase.exam.dto;

import java.util.Date;

import org.apache.commons.lang3.Validate;

import mn.erin.lms.base.domain.model.exam.ExamId;

/**
 * @author Galsan Bayart.
 */
public class PublishExamInput
{
  private ExamId examId;
  private Date publishDate;
  private String time;
  private boolean sendSms;
  private boolean sendMail;

  public PublishExamInput(ExamId examId, Date publishDate, String time, boolean sendSms, boolean sendMail)
  {
    this.examId = examId;
    this.publishDate = publishDate;
    this.time = time;
    this.sendSms = sendSms;
    this.sendMail = sendMail;
  }

  public ExamId getExamId()
  {
    return examId;
  }

  public void setExamId(ExamId examId)
  {
    this.examId = examId;
  }

  public Date getPublishDate()
  {
    return publishDate;
  }

  public void setPublishDate(Date publishDate)
  {
    this.publishDate = publishDate;
  }

  public String getTime()
  {
    return time;
  }

  public void setTime(String time)
  {
    this.time = time;
  }

  public boolean isSendSms()
  {
    return sendSms;
  }

  public void setSendSms(boolean sendSms)
  {
    this.sendSms = sendSms;
  }

  public boolean isSendMail()
  {
    return sendMail;
  }

  public void setSendMail(boolean sendMail)
  {
    this.sendMail = sendMail;
  }
}

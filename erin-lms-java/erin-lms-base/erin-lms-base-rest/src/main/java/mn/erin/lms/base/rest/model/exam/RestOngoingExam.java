package mn.erin.lms.base.rest.model.exam;

import java.util.Date;

/**
 * @author mLkhagvasuren
 */
public class RestOngoingExam
{
  private String examId;
  private String examName;
  private Date examStartDate;
  private Date examEndDate;

  public void setExamId(String examId)
  {
    this.examId = examId;
  }

  public String getExamId()
  {
    return examId;
  }

  public void setExamName(String examName)
  {
    this.examName = examName;
  }

  public String getExamName()
  {
    return examName;
  }

  public void setExamStartDate(Date examStartDate)
  {
    this.examStartDate = examStartDate;
  }

  public Date getExamStartDate()
  {
    return examStartDate;
  }

  public void setExamEndDate(Date examEndDate)
  {
    this.examEndDate = examEndDate;
  }

  public Date getExamEndDate()
  {
    return examEndDate;
  }
}

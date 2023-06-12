package mn.erin.lms.base.domain.usecase.exam.dto;

import java.util.Date;

/**
 * @author Byambajav
 */
public class LeanerExamListDto
{
  private final String id;
  private String name;
  private String author;
  private String status;
  private String categoryId;
  private String startTime;
  private String endTime;
  private Date startDate;
  private Date endDate;
  private int thresholdScore;
  private boolean hasCertificate;
  private boolean upcoming;

  public LeanerExamListDto(String examId, String examName, int thresholdScore, String author, String status)
  {
    this.id = examId;
    this.name = examName;
    this.thresholdScore = thresholdScore;
    this.author = author;
    this.status = status;
  }

  public String getStartTime()
  {
    return startTime;
  }

  public void setStartTime(String startTime)
  {
    this.startTime = startTime;
  }

  public String getEndTime()
  {
    return endTime;
  }

  public void setEndTime(String endTime)
  {
    this.endTime = endTime;
  }

  public Date getEndDate()
  {
    return endDate;
  }

  public void setEndDate(Date endDate)
  {
    this.endDate = endDate;
  }

  public String getCategoryId()
  {
    return categoryId;
  }

  public void setCategoryId(String categoryId)
  {
    this.categoryId = categoryId;
  }

  public boolean isUpcoming()
  {
    return upcoming;
  }

  public void setUpcoming(boolean upcoming)
  {
    this.upcoming = upcoming;
  }

  public String getId()
  {
    return id;
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public void setThresholdScore(int thresholdScore)
  {
    this.thresholdScore = thresholdScore;
  }

  public int getThresholdScore()
  {
    return thresholdScore;
  }

  public String getAuthor()
  {
    return author;
  }

  public void setAuthor(String author)
  {
    this.author = author;
  }

  public String getStatus()
  {
    return status;
  }

  public void setStatus(String status)
  {
    this.status = status;
  }

  public boolean isHasCertificate()
  {
    return hasCertificate;
  }

  public void setHasCertificate(boolean hasCertificate)
  {
    this.hasCertificate = hasCertificate;
  }

  public Date getStartDate()
  {
    return startDate;
  }

  public void setStartDate(Date startDate)
  {
    this.startDate = startDate;
  }
}

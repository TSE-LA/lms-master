package mn.erin.lms.unitel.domain.model.analytics;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.apache.commons.lang3.Validate;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class CourseAnalyticData implements Serializable
{
  private Float status;
  private Integer score;
  private LocalDateTime initialLaunchDate;
  private LocalDateTime lastLaunchDate;
  private LocalDateTime receivedCertificateDate;
  private String totalTime;
  private String feedback;
  private Integer maxScore;
  private Integer interactionsCount;
  private boolean isLate;
  private String departmentName;

  private CourseAnalyticData()
  {

  }

  public Float getStatus()
  {
    return status;
  }

  public Integer getScore()
  {
    return score;
  }

  public LocalDateTime getInitialLaunchDate()
  {
    return initialLaunchDate;
  }

  public LocalDateTime getLastLaunchDate()
  {
    return lastLaunchDate;
  }

  public String getTotalTime()
  {
    return totalTime;
  }

  public String getFeedback()
  {
    return feedback;
  }

  public Integer getMaxScore()
  {
    return maxScore;
  }

  public Integer getInteractionsCount()
  {
    return interactionsCount;
  }

  public boolean isLate()
  {
    return isLate;
  }

  public String getDepartmentName()
  {
    return departmentName;
  }

  public void setDepartmentName(String departmentName)
  {
    this.departmentName = departmentName;
  }

  public LocalDateTime getReceivedCertificateDate()
  {
    return receivedCertificateDate;
  }

  public void setReceivedCertificateDate(LocalDateTime receivedCertificateDate)
  {
    this.receivedCertificateDate = receivedCertificateDate;
  }

  public static class Builder
  {
    private Float status;
    private Integer score;
    private Integer maxScore;
    private Integer interactionsCount;
    private LocalDateTime initialLaunchDate;
    private LocalDateTime lastLaunchDate;
    private LocalDateTime receivedCertificateDate;
    private String totalTime;
    private String feedback;
    private boolean isLate;
    private String departmentName;

    public Builder withStatus(Float status)
    {
      this.status = status;
      return this;
    }

    public Builder withScore(Integer score)
    {
      this.score = score;
      return this;
    }

    public Builder withDepartmentName(String departmentName)
    {
      this.departmentName = departmentName;
      return this;
    }

    public Builder startedAt(LocalDateTime initialLaunchDate)
    {
      this.initialLaunchDate = initialLaunchDate;
      return this;
    }

    public Builder withCertificateDateAt(LocalDateTime receivedCertificateDate)
    {
      this.receivedCertificateDate = receivedCertificateDate;
      return this;
    }

    public Builder lastLaunchedAt(LocalDateTime lastLaunchDate)
    {
      this.lastLaunchDate = lastLaunchDate;
      return this;
    }

    public Builder withTotalTime(String totalTime)
    {
      this.totalTime = Validate.notBlank(totalTime, "Total time cannot be null or blank!");
      return this;
    }

    public Builder withFeedback(String feedback)
    {
      this.feedback = feedback;
      return this;
    }

    public Builder havingMaxScore(Integer maxScore)
    {
      this.maxScore = maxScore;
      return this;
    }

    public Builder havingInteractionsCount(Integer interactionsCount)
    {
      this.interactionsCount = interactionsCount;
      return this;
    }

    public Builder setIsLate(boolean isLate)
    {
      this.isLate = isLate;
      return this;
    }

    public CourseAnalyticData build()
    {
      CourseAnalyticData courseAnalyticData = new CourseAnalyticData();
      courseAnalyticData.score = this.score;
      courseAnalyticData.status = this.status;
      courseAnalyticData.initialLaunchDate = this.initialLaunchDate;
      courseAnalyticData.lastLaunchDate = this.lastLaunchDate;
      courseAnalyticData.totalTime = this.totalTime;
      courseAnalyticData.feedback = this.feedback;
      courseAnalyticData.maxScore = this.maxScore;
      courseAnalyticData.interactionsCount = this.interactionsCount;
      courseAnalyticData.isLate = this.isLate;
      courseAnalyticData.departmentName = this.departmentName;
      courseAnalyticData.receivedCertificateDate = this.receivedCertificateDate;

      return courseAnalyticData;
    }

    public CourseAnalyticData buildDepartmentName(String departmentName)
    {
      CourseAnalyticData courseAnalyticData = new CourseAnalyticData();
      courseAnalyticData.score = this.score;
      courseAnalyticData.status = this.status;
      courseAnalyticData.initialLaunchDate = this.initialLaunchDate;
      courseAnalyticData.lastLaunchDate = this.lastLaunchDate;
      courseAnalyticData.totalTime = this.totalTime;
      courseAnalyticData.feedback = this.feedback;
      courseAnalyticData.maxScore = this.maxScore;
      courseAnalyticData.interactionsCount = this.interactionsCount;
      courseAnalyticData.isLate = this.isLate;
      courseAnalyticData.receivedCertificateDate = this.receivedCertificateDate;
      courseAnalyticData.departmentName = departmentName;

      return courseAnalyticData;
    }
  }
}

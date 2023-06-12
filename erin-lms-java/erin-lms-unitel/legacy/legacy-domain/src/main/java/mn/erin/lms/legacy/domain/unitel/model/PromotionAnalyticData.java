package mn.erin.lms.legacy.domain.unitel.model;

import java.util.Date;

import org.apache.commons.lang3.Validate;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class PromotionAnalyticData
{
  private String contentId;
  private Float status;
  private Integer score;
  private Date initialLaunchDate;
  private Date lastLaunchDate;
  private String totalTime;
  private String feedback;
  private Integer maxScore;
  private Integer interactionsCount;
  private Integer totalEnrollment;

  private PromotionAnalyticData()
  {

  }

  public String getContentId()
  {
    return contentId;
  }


  public Float getStatus()
  {
    return status;
  }

  public Integer getScore()
  {
    return score;
  }

  public Integer getTotalEnrollment()
  {
    return totalEnrollment;
  }

  public Date getInitialLaunchDate()
  {
    return initialLaunchDate;
  }

  public Date getLastLaunchDate()
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

  public static class Builder
  {
    private String contentId;
    private Float status;
    private Integer score;
    private Integer totalEnrollment;
    private Integer maxScore;
    private Integer interactionsCount;
    private Date initialLaunchDate;
    private Date lastLaunchDate;
    private String totalTime;
    private String feedback;

    public Builder ofContentId(String contentId)
    {
      this.contentId = Validate.notBlank(contentId, "User name cannot be null or blank!");
      return this;
    }

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
    public Builder withTotalEnrollment(Integer totalEnrollment)
    {
      this.totalEnrollment = totalEnrollment;
      return this;
    }

    public Builder startedAt(Date initialLaunchDate)
    {
      this.initialLaunchDate = initialLaunchDate;
      return this;
    }

    public Builder lastLaunchedAt(Date lastLaunchDate)
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

    public PromotionAnalyticData build()
    {
      PromotionAnalyticData promotionAnalyticData = new PromotionAnalyticData();
      promotionAnalyticData.contentId = this.contentId;
      promotionAnalyticData.score = this.score;
      promotionAnalyticData.totalEnrollment = this.totalEnrollment;
      promotionAnalyticData.status = this.status;
      promotionAnalyticData.initialLaunchDate = this.initialLaunchDate;
      promotionAnalyticData.lastLaunchDate = this.lastLaunchDate;
      promotionAnalyticData.totalTime = this.totalTime;
      promotionAnalyticData.feedback = this.feedback;
      promotionAnalyticData.maxScore = this.maxScore;
      promotionAnalyticData.interactionsCount = this.interactionsCount;

      return promotionAnalyticData;
    }
  }
}

package mn.erin.lms.legacy.domain.unitel.usecase.analytics.get_promotion_analytics;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class PromotionAnalyticsOutput
{
  private String userName;
  private String group;
  private String role;
  private Float status;
  private Integer score;
  private String initialLaunchDate;
  private String lastLaunchDate;
  private String totalTime;
  private String feedback;
  private boolean isLate = false;
  private Integer totalEnrollment;

  public String getUserName()
  {
    return userName;
  }

  public void setUserName(String userName)
  {
    this.userName = userName;
  }

  public String getGroup()
  {
    return group;
  }

  public void setGroup(String group)
  {
    this.group = group;
  }

  public String getRole()
  {
    return role;
  }

  public void setRole(String role)
  {
    this.role = role;
  }

  public Float getStatus()
  {
    return status;
  }

  public void setStatus(Float status)
  {
    this.status = status;
  }

  public Integer getScore()
  {
    return score;
  }

  public void setScore(Integer score)
  {
    this.score = score == null ? 0 : score;
  }

  public Integer getTotalEnrollment()
  {
    return totalEnrollment;
  }

  public void setTotalEnrollment(Integer totalEnrollment)
  {
    this.totalEnrollment = totalEnrollment == null ? 0 : totalEnrollment;
  }

  public String getInitialLaunchDate()
  {
    return initialLaunchDate;
  }

  public void setInitialLaunchDate(String initialLaunchDate)
  {
    this.initialLaunchDate = initialLaunchDate;
  }

  public String getLastLaunchDate()
  {
    return lastLaunchDate;
  }

  public void setLastLaunchDate(String lastLaunchDate)
  {
    this.lastLaunchDate = lastLaunchDate;
  }

  public String getTotalTime()
  {
    return totalTime;
  }

  public void setTotalTime(String totalTime)
  {
    this.totalTime = totalTime;
  }

  public String getFeedback()
  {
    return feedback;
  }

  public void setFeedback(String feedback)
  {
    this.feedback = feedback;
  }

  public boolean isLate()
  {
    return isLate;
  }

  public boolean getIsLate()
  {
    return isLate;
  }

  public void setLate(boolean late)
  {
    isLate = late;
  }
}

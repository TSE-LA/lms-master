package mn.erin.lms.unitel.domain.usecase.dto;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class PromotionAnalyticsDto
{
  private String userName;
  private String department;
  private String role;
  private Float status;
  private Integer score;
  private String initialLaunchDate;
  private String lastLaunchDate;
  private String totalTime;
  private String feedback;
  private Integer views;
  private boolean isLate = false;

  public String getUserName()
  {
    return userName;
  }

  public void setUserName(String userName)
  {
    this.userName = userName;
  }

  public String getDepartment()
  {
    return department;
  }

  public void setDepartment(String department)
  {
    this.department = department;
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
    this.score = score;
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

  public Integer getViews()
  {
    return views;
  }

  public void setViews(Integer views)
  {
    this.views = views;
  }

  public boolean isLate()
  {
    return isLate;
  }

  public void setLate(boolean late)
  {
    isLate = late;
  }
}

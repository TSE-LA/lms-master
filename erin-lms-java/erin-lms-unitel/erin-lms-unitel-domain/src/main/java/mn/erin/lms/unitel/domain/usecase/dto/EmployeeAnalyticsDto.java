package mn.erin.lms.unitel.domain.usecase.dto;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class EmployeeAnalyticsDto
{
  private float launchPercentage;
  private float avgTestScoreByPercentage;
  private int month;

  public float getLaunchPercentage()
  {
    return launchPercentage;
  }

  public void setLaunchPercentage(float launchPercentage)
  {
    this.launchPercentage = launchPercentage;
  }

  public float getAvgTestScoreByPercentage()
  {
    return avgTestScoreByPercentage;
  }

  public void setAvgTestScoreByPercentage(float avgTestScoreByPercentage)
  {
    this.avgTestScoreByPercentage = avgTestScoreByPercentage;
  }

  public int getMonth()
  {
    return month;
  }

  public void setMonth(int month)
  {
    this.month = month;
  }
}

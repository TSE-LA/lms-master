package mn.erin.lms.unitel.domain.model.analytics;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class EmployeeAnalyticData
{
  private float launchPercentage = 0.0f;
  private float avgTestScoreByPercentage = 0.0f;
  private int month = 1;

  public EmployeeAnalyticData withLaunchPercentage(float launchPercentage)
  {
    this.launchPercentage = launchPercentage;
    return this;
  }

  public EmployeeAnalyticData withAvgTestScorePercentage(float avgTestScoreByPercentage)
  {
    this.avgTestScoreByPercentage = avgTestScoreByPercentage;
    return this;
  }

  public EmployeeAnalyticData on(int month)
  {
    this.month = month;
    return this;
  }

  public float getLaunchPercentage()
  {
    return launchPercentage;
  }

  public float getAvgTestScoreByPercentage()
  {
    return avgTestScoreByPercentage;
  }

  public int getMonth()
  {
    return month;
  }
}

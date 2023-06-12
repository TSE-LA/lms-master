package mn.erin.lms.base.analytics.usecase.dto;

import java.util.List;

/**
 * @author Munkh
 */
public class PromotionActivityDto
{
  private int learners;
  private String averageTime;
  private int perfectEmployeeCount;
  private int perfectSupersCount;
  private double averageProgress;
  private List<LearnerActivity> activities;

  public PromotionActivityDto(int learners, String averageTime, int perfectEmployeeCount, int perfectSupersCount,
      double averageProgress, List<LearnerActivity> activities)
  {
    this.learners = learners;
    this.averageTime = averageTime;
    this.perfectEmployeeCount = perfectEmployeeCount;
    this.perfectSupersCount = perfectSupersCount;
    this.averageProgress = averageProgress;
    this.activities = activities;
  }

  public int getLearners()
  {
    return learners;
  }

  public void setLearners(int learners)
  {
    this.learners = learners;
  }

  public String getAverageTime()
  {
    return averageTime;
  }

  public void setAverageTime(String averageTime)
  {
    this.averageTime = averageTime;
  }

  public int getPerfectEmployeeCount()
  {
    return perfectEmployeeCount;
  }

  public void setPerfectEmployeeCount(int perfectEmployeeCount)
  {
    this.perfectEmployeeCount = perfectEmployeeCount;
  }

  public int getPerfectSupersCount()
  {
    return perfectSupersCount;
  }

  public void setPerfectSupersCount(int perfectSupersCount)
  {
    this.perfectSupersCount = perfectSupersCount;
  }

  public double getAverageProgress()
  {
    return averageProgress;
  }

  public void setAverageProgress(double averageProgress)
  {
    this.averageProgress = averageProgress;
  }

  public List<LearnerActivity> getActivities()
  {
    return activities;
  }

  public void setActivities(List<LearnerActivity> activities)
  {
    this.activities = activities;
  }
}

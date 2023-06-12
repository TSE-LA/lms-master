package mn.erin.lms.base.analytics.repository.mongo.document;

import java.util.List;

/**
 * @author Munkh
 */
public class MongoAggregatedRuntime
{
  private double totalProgress;
  private List<String> spentTimes;
  private int count;

  public MongoAggregatedRuntime(double totalProgress, List<String> spentTimes)
  {
    this.totalProgress = totalProgress;
    this.spentTimes = spentTimes;
    this.count = 1;
  }

  public double getTotalProgress()
  {
    return totalProgress;
  }

  public void setTotalProgress(double totalProgress)
  {
    this.totalProgress = totalProgress;
  }

  public void addTotalProgress(double progress)
  {
    this.totalProgress += progress;
    count++;
  }

  public List<String> getSpentTimes()
  {
    return spentTimes;
  }

  public void setSpentTimes(List<String> spentTimes)
  {
    this.spentTimes = spentTimes;
  }

  public void addSpentTimes(String spentTime)
  {
    if (spentTime != null)
    {
      this.spentTimes.add(spentTime);
    }
  }

  public int getCount()
  {
    return count;
  }
}

/*
 * (C)opyright, 2021, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.base.analytics.model.learner;

import mn.erin.lms.base.analytics.model.Analytic;

/**
 * @author Munkh
 */
public class LearnerActivity implements Analytic
{
  private String averageTime;
  private int learnerCount;
  private int perfectViewersCount;
  private double averageProgress;

  public LearnerActivity(String averageTime, int learnerCount, int perfectViewersCount, double averageProgress)
  {
    this.averageTime = averageTime;
    this.learnerCount = learnerCount;
    this.perfectViewersCount = perfectViewersCount;
    this.averageProgress = averageProgress;
  }

  public String getAverageTime()
  {
    return averageTime;
  }

  public void setAverageTime(String averageTime)
  {
    this.averageTime = averageTime;
  }

  public int getLearnerCount()
  {
    return learnerCount;
  }

  public void setLearnerCount(int learnerCount)
  {
    this.learnerCount = learnerCount;
  }

  public int getPerfectViewersCount()
  {
    return perfectViewersCount;
  }

  public void setPerfectViewersCount(int perfectViewsCount)
  {
    this.perfectViewersCount = perfectViewsCount;
  }

  public double getAverageProgress()
  {
    return averageProgress;
  }

  public void setAverageProgress(double averageProgress)
  {
    this.averageProgress = averageProgress;
  }
}

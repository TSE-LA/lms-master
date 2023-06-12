package mn.erin.lms.jarvis.domain.report.model;

import mn.erin.domain.base.model.Entity;

/**
 * @author Galsan Bayart
 */
public class LearnerSuccess implements Entity<LearnerSuccess>
{
  private final String learnerId;

  private String courseType;

  private int score;
  private int maxScore;

  private double performance;

  private int year;
  private int month;

  public LearnerSuccess(String learnerId, String courseType, int score, int maxScore, double performance, int year, int month)
  {
    this.learnerId = learnerId;
    this.courseType = courseType;
    this.score = score;
    this.maxScore = maxScore;
    this.performance = performance;
    this.year = year;
    this.month = month;
  }

  public LearnerSuccess(String learnerId)
  {
    this.learnerId = learnerId;
  }

  public String getLearnerId()
  {
    return learnerId;
  }

  public int getScore()
  {
    return score;
  }

  public void setScore(int score)
  {
    this.score = score;
  }

  public int getMaxScore()
  {
    return maxScore;
  }

  public void setMaxScore(int maxScore)
  {
    this.maxScore = maxScore;
  }

  public double getPerformance()
  {
    return performance;
  }

  public void setPerformance(double performance)
  {
    this.performance = performance;
  }

  public int getYear()
  {
    return year;
  }

  public void setYear(int year)
  {
    this.year = year;
  }

  public int getMonth()
  {
    return month;
  }

  public void setMonth(int month)
  {
    this.month = month;
  }

  @Override
  public boolean sameIdentityAs(LearnerSuccess other)
  {
    return false;
  }

  public String getCourseType()
  {
    return courseType;
  }

  public void setCourseType(String courseType)
  {
    this.courseType = courseType;
  }
}

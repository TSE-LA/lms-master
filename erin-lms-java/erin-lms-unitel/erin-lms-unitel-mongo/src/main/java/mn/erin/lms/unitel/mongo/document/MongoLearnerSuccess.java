package mn.erin.lms.unitel.mongo.document;

import org.springframework.data.annotation.Id;

/**
 * @author Byambajav
 */
public class MongoLearnerSuccess
{
  @Id
  private String id;

  private String courseType;

  private String learnerId;

  private int score;
  private int maxScore;

  private double performance;

  private int year;
  private int month;

  public MongoLearnerSuccess(String id, String courseType, String learnerId, int score, int maxScore, double performance, int year, int month)
  {
    this.id = id;
    this.courseType = courseType;
    this.learnerId = learnerId;
    this.score = score;
    this.maxScore = maxScore;
    this.performance = performance;
    this.year = year;
    this.month = month;
  }

  public MongoLearnerSuccess()
  {
  }

  public String getId()
  {
    return id;
  }

  public void setId(String id)
  {
    this.id = id;
  }

  public String getLearnerId()
  {
    return learnerId;
  }

  public void setLearnerId(String learnerId)
  {
    this.learnerId = learnerId;
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

  public String getCourseType()
  {
    return courseType;
  }

  public void setCourseType(String courseType)
  {
    this.courseType = courseType;
  }
}

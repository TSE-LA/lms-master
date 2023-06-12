package mn.erin.lms.unitel.domain.model.analytics;

/**
 * @author Byambajav
 */
public class LearnerSuccess
{
  private final String learnerId;

  private int score;
  private int maxScore;
  private String courseType;

  private double performance;

  private int year;
  private int month;

  public LearnerSuccess(String learnerId, int score, int maxScore, String courseType, double performance, int year, int month)
  {
    this.learnerId = learnerId;
    this.score = score;
    this.maxScore = maxScore;
    this.courseType = courseType;
    this.performance = performance;
    this.year = year;
    this.month = month;
  }

  public LearnerSuccess(String learnerId){
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

  public String getCourseType()
  {
    return courseType;
  }

  public void setCourseType(String courseType)
  {
    this.courseType = courseType;
  }
}

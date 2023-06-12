package mn.erin.lms.jarvis.domain.report.usecase.dto;

/**
 * @author Galsan Bayart
 */
public class LearnerSuccessMonthData
{
  double score;
  double groupAvg;

  double myPerformance;
  double groupPerformance;

  int month;

  public LearnerSuccessMonthData(double score, double groupAvg, double myPerformance, double groupPerformance, int month)
  {
    this.score = score;
    this.groupAvg = groupAvg;
    this.myPerformance = myPerformance;
    this.groupPerformance = groupPerformance;
    this.month = month;
  }

  public LearnerSuccessMonthData()
  {
  }

  public double getScore()
  {
    return score;
  }

  public void setScore(double score)
  {
    this.score = score;
  }

  public double getGroupAvg()
  {
    return groupAvg;
  }

  public void setGroupAvg(double groupAvg)
  {
    this.groupAvg = groupAvg;
  }

  public double getMyPerformance()
  {
    return myPerformance;
  }

  public void setMyPerformance(double myPerformance)
  {
    this.myPerformance = myPerformance;
  }

  public double getGroupPerformance()
  {
    return groupPerformance;
  }

  public void setGroupPerformance(double groupPerformance)
  {
    this.groupPerformance = groupPerformance;
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

package mn.erin.lms.unitel.domain.usecase.dto;

import java.util.List;

/**
 * @author Byambajav
 */
public class LearnerSuccessDto
{
  private List<LearnerSuccessMonthData> learnerSuccesses;
  private int overallScore;
  private int overallMaxScore;
  private double difference;

  public LearnerSuccessDto(List<LearnerSuccessMonthData> learnerSuccesses, int overallScore, int overallMaxScore, double difference)
  {
    this.learnerSuccesses = learnerSuccesses;
    this.overallScore = overallScore;
    this.overallMaxScore = overallMaxScore;
    this.difference = difference;
  }

  public LearnerSuccessDto()
  {
  }

  public List<LearnerSuccessMonthData> getLearnerSuccesses()
  {
    return learnerSuccesses;
  }

  public void setLearnerSuccesses(List<LearnerSuccessMonthData> learnerSuccesses)
  {
    this.learnerSuccesses = learnerSuccesses;
  }

  public int getOverallScore()
  {
    return overallScore;
  }

  public void setOverallScore(int overallScore)
  {
    this.overallScore = overallScore;
  }

  public int getOverallMaxScore()
  {
    return overallMaxScore;
  }

  public void setOverallMaxScore(int overallMaxScore)
  {
    this.overallMaxScore = overallMaxScore;
  }

  public double getDifference()
  {
    return difference;
  }

  public void setDifference(double difference)
  {
    this.difference = difference;
  }
}

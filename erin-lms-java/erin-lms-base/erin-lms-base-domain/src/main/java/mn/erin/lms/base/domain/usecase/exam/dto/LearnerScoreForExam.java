package mn.erin.lms.base.domain.usecase.exam.dto;

import java.io.Serializable;

import mn.erin.lms.base.domain.model.exam.ExamConfig;

/**
 * Represents a learner exam final score
 *
 * @author EBazarragchaa
 */
public class LearnerScoreForExam implements Serializable
{
  private static final long serialVersionUID = -6058600928877924825L;

  private final String learnerName;
  private final int examMaxScore;
  private final int examThresholdScore;
  private final int examAttemptCount;
  private final int learnerFinalScore;
  private final int learnerSpentTimeInSeconds;
  private final int learnerAttemptCount;

  private LearnerScoreForExam(String learnerName, double examMaxScore, double examThresholdScore, double examMaxAttempt, double learnerFinalScore,
      double learnerSpentTimeInSeconds, int learnerAttemptCount)
  {
    this.learnerName = learnerName;
    this.examMaxScore = ((int) examMaxScore);
    this.examThresholdScore = ((int) examThresholdScore);
    this.examAttemptCount = ((int) examMaxAttempt);
    this.learnerFinalScore = ((int) learnerFinalScore);
    this.learnerSpentTimeInSeconds = ((int) learnerSpentTimeInSeconds);
    this.learnerAttemptCount = learnerAttemptCount;
  }

  public static LearnerScoreForExam empty(String learnerName, ExamConfig examConfig)
  {
    return new LearnerScoreForExam(learnerName, examConfig.getMaxScore(), examConfig.getThresholdScore(), examConfig.getAttempt(), -1, 0, 0);
  }

  public static LearnerScoreForExam from(String learnerName, ExamConfig examConfig, ExamInteractionDto interaction)
  {
    return new LearnerScoreForExam(learnerName, examConfig.getMaxScore(), examConfig.getThresholdScore(), examConfig.getAttempt(),
        interaction.getScore(), interaction.getSpentTime(), interaction.getAttempt());
  }

  public String getLearnerName()
  {
    return learnerName;
  }

  public double getExamMaxScore()
  {
    return examMaxScore;
  }

  public double getExamThresholdScore()
  {
    return examThresholdScore;
  }

  public int getExamAttemptCount()
  {
    return examAttemptCount;
  }

  public double getLearnerFinalScore()
  {
    return learnerFinalScore;
  }

  public String getLearnerSpentTime()
  {
    int hours = learnerSpentTimeInSeconds / 3600;
    int minutes = (learnerSpentTimeInSeconds % 3600) / 60;
    int seconds = learnerSpentTimeInSeconds % 60;
    return String.format("%02d:%02d:%02d", hours, minutes, seconds);
  }

  public int getLearnerAttemptCount()
  {
    return learnerAttemptCount;
  }

  public Boolean getLearnerPassStatus()
  {
    if (learnerFinalScore == -1)
    {
      return null;//OK
    }
    return learnerFinalScore >= examThresholdScore;
  }

  public int getLearnerGradeInPercentage()
  {
    return Math.toIntExact(Math.round((learnerFinalScore * 100) / (double) examMaxScore));
  }
}

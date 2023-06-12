package mn.erin.lms.base.domain.usecase.exam.dto;

import mn.erin.lms.base.domain.model.exam.ExamStatus;

/**
 * @author Byambajav
 */
public class LearnerExamLaunchDto
{
  private String title;
  private String description;
  private ExamStatus status;
  private int maxAttempt;
  private double thresholdScore;
  private int remainingAttempt;
  private double maxScore;
  private double score;
  private String scorePercentage;
  private String duration;
  private String author;
  private String spentTime;
  private String certificateId;
  private boolean ongoing;

  public LearnerExamLaunchDto(String author, String duration,
      double thresholdScore, String title, String description, ExamStatus status)
  {
    this.author = author;
    this.duration = duration;
    this.thresholdScore = thresholdScore;
    this.title = title;
    this.description = description;
    this.status = status;
  }

  public ExamStatus getStatus()
  {
    return status;
  }

  public void setStatus(ExamStatus status)
  {
    this.status = status;
  }

  public String getTitle()
  {
    return title;
  }

  public void setTitle(String title)
  {
    this.title = title;
  }

  public String getDescription()
  {
    return description;
  }

  public void setDescription(String description)
  {
    this.description = description;
  }

  public void setThresholdScore(double thresholdScore)
  {
    this.thresholdScore = thresholdScore;
  }

  public boolean isOngoing()
  {
    return ongoing;
  }

  public void setOngoing(boolean ongoing)
  {
    this.ongoing = ongoing;
  }

  public String getCertificateId()
  {
    return certificateId;
  }

  public void setCertificateId(String certificateId)
  {
    this.certificateId = certificateId;
  }

  public String getAuthor()
  {
    return author;
  }

  public void setAuthor(String author)
  {
    this.author = author;
  }

  public double getMaxScore()
  {
    return maxScore;
  }

  public void setMaxScore(double maxScore)
  {
    this.maxScore = maxScore;
  }

  public int getMaxAttempt()
  {
    return maxAttempt;
  }

  public void setMaxAttempt(int maxAttempt)
  {
    this.maxAttempt = maxAttempt;
  }

  public String getDuration()
  {
    return duration;
  }

  public void setDuration(String duration)
  {
    this.duration = duration;
  }

  public double getThresholdScore()
  {
    return thresholdScore;
  }

  public void setThresholdScore(int thresholdScore)
  {
    this.thresholdScore = thresholdScore;
  }

  public double getScore()
  {
    return score;
  }

  public void setScore(double score)
  {
    this.score = score;
  }

  public String getScorePercentage()
  {
    return scorePercentage;
  }

  public void setScorePercentage(String scorePercentage)
  {
    this.scorePercentage = scorePercentage;
  }

  public int getRemainingAttempt()
  {
    return remainingAttempt;
  }

  public void setRemainingAttempt(int remainingAttempt)
  {
    this.remainingAttempt = remainingAttempt;
  }

  public String getSpentTime()
  {
    return spentTime;
  }

  public void setSpentTime(String spentTime)
  {
    this.spentTime = spentTime;
  }
}

package mn.erin.lms.base.mongo.document.exam;

import java.util.Date;
import java.util.Set;

/**
 * @author Galsan Bayart
 */

public class MongoExamConfig
{
  private Set<String> questionIds;
  private Set<MongoRandomQuestionConfig> mongoRandomQuestionConfigs;
  private int totalQuestion;

  private String answerResult;
  private boolean shuffleQuestion;
  private boolean shuffleAnswer;
  private boolean questionsPerPage;
  private boolean autoStart;

  private int maxScore;
  private int thresholdScore;
  private int attempt;

  private String certificateId;

  private Date startDate;
  private Date endDate;
  private String startTime;
  private String endTime;
  private int duration;

  public MongoExamConfig()
  {
    /*Mongo need an empty constructor*/
  }

  public Set<String> getQuestionIds()
  {
    return questionIds;
  }

  public void setQuestionIds(Set<String> questionIds)
  {
    this.questionIds = questionIds;
  }

  public Set<MongoRandomQuestionConfig> getMongoRandomQuestionConfigs()
  {
    return mongoRandomQuestionConfigs;
  }

  public void setRandomQuestionConfigs(Set<MongoRandomQuestionConfig> mongoRandomQuestionConfigs)
  {
    this.mongoRandomQuestionConfigs = mongoRandomQuestionConfigs;
  }

  public int getTotalQuestion()
  {
    return totalQuestion;
  }

  public void setTotalQuestion(int totalQuestion)
  {
    this.totalQuestion = totalQuestion;
  }

  public String getAnswerResult()
  {
    return answerResult;
  }

  public void setAnswerResult(String answerResult)
  {
    this.answerResult = answerResult;
  }

  public boolean isShuffleQuestion()
  {
    return shuffleQuestion;
  }

  public void setShuffleQuestion(boolean shuffleQuestion)
  {
    this.shuffleQuestion = shuffleQuestion;
  }

  public boolean isShuffleAnswer()
  {
    return shuffleAnswer;
  }

  public void setShuffleAnswer(boolean shuffleAnswer)
  {
    this.shuffleAnswer = shuffleAnswer;
  }

  public boolean getQuestionsPerPage()
  {
    return questionsPerPage;
  }

  public void setQuestionsPerPage(boolean questionsPerPage)
  {
    this.questionsPerPage = questionsPerPage;
  }

  public int getThresholdScore()
  {
    return thresholdScore;
  }

  public void setThresholdScore(int thresholdScore)
  {
    this.thresholdScore = thresholdScore;
  }

  public int getAttempt()
  {
    return attempt;
  }

  public void setAttempt(int attempt)
  {
    this.attempt = attempt;
  }

  public String getCertificateId()
  {
    return certificateId;
  }

  public void setCertificateId(String certificateId)
  {
    this.certificateId = certificateId;
  }

  public Date getStartDate()
  {
    return startDate;
  }

  public void setStartDate(Date startDate)
  {
    this.startDate = startDate;
  }

  public Date getEndDate()
  {
    return endDate;
  }

  public void setEndDate(Date endDate)
  {
    this.endDate = endDate;
  }

  public String getStartTime()
  {
    return startTime;
  }

  public void setStartTime(String startTime)
  {
    this.startTime = startTime;
  }

  public int getDuration()
  {
    return duration;
  }

  public void setDuration(int duration)
  {
    this.duration = duration;
  }

  public int getMaxScore()
  {
    return maxScore;
  }

  public void setMaxScore(int maxScore)
  {
    this.maxScore = maxScore;
  }

  public String getEndTime()
  {
    return endTime;
  }

  public void setEndTime(String endTime)
  {
    this.endTime = endTime;
  }

  public boolean isAutoStart()
  {
    return autoStart;
  }

  public void setAutoStart(boolean autoStart)
  {
    this.autoStart = autoStart;
  }
}

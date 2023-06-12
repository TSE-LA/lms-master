package mn.erin.lms.base.rest.model.exam;

import java.util.Date;
import java.util.Set;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;

/**
 * @author Galsan Bayart.
 */
public class RestExamConfigure
{
  private Set<String> questionIds;
  private Set<RestRandomQuestion> randomQuestions;
  private String answerResult;
  private boolean shuffleQuestion;
  private boolean shuffleAnswer;
  private boolean questionsPerPage;
  private boolean autoStart;
  private int thresholdScore;
  private int attempt;
  @Nullable
  private String certificateId;
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private Date startDate;
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private Date endDate;
  private String startTime;
  private String endTime;
  private int duration;
  private int maxScore;

  public RestExamConfigure()
  {
    //doNothing
  }

  public Set<String> getQuestionIds()
  {
    return questionIds;
  }

  public void setQuestionIds(Set<String> questionIds)
  {
    this.questionIds = questionIds;
  }

  public Set<RestRandomQuestion> getRandomQuestions()
  {
    return randomQuestions;
  }

  public void setRandomQuestions(Set<RestRandomQuestion> randomQuestions)
  {
    this.randomQuestions = randomQuestions;
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

  public boolean isQuestionsPerPage()
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

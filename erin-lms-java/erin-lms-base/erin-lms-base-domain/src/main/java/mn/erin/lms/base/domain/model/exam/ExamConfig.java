package mn.erin.lms.base.domain.model.exam;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import mn.erin.domain.base.model.ValueObject;
import mn.erin.lms.base.domain.model.certificate.CertificateId;

/**
 * @author Galsan Bayart
 */
public class ExamConfig implements ValueObject<ExamConfig>
{
  private final Set<RandomQuestionConfig> randomQuestionConfigs;
  private final int totalQuestions;
  private final ShowAnswerResult answerResult;
  private final boolean shuffleQuestion;
  private final boolean shuffleAnswer;
  private final boolean questionsPerPage;
  private final boolean autoStart;
  private final int thresholdScore;
  private int maxScore;
  private int attempt;
  private Set<String> questionIds = new HashSet<>();
  private CertificateId certificateId;
  private Date startDate;
  private Date endDate;
  private String startTime;
  private String endTime;
  private int duration;

  public ExamConfig(Set<RandomQuestionConfig> randomQuestionConfigs,
      int totalQuestions, ShowAnswerResult answerResult,
      boolean shuffleQuestion,
      boolean shuffleAnswer,
      boolean questionsPerPage,
      boolean autoStart,
      int thresholdScore)
  {
    this.randomQuestionConfigs = randomQuestionConfigs;
    this.totalQuestions = totalQuestions;
    this.answerResult = answerResult;
    this.shuffleQuestion = shuffleQuestion;
    this.shuffleAnswer = shuffleAnswer;
    this.questionsPerPage = questionsPerPage;
    this.thresholdScore = thresholdScore;
    this.autoStart = autoStart;
  }

  public Set<String> getQuestionIds()
  {
    return questionIds;
  }

  public void setQuestionIds(Set<String> questionIds)
  {
    this.questionIds = questionIds;
  }

  public Set<RandomQuestionConfig> getRandomQuestionConfigs()
  {
    return randomQuestionConfigs;
  }

  public int getTotalQuestions()
  {
    return totalQuestions;
  }

  public ShowAnswerResult getAnswerResult()
  {
    return answerResult;
  }

  public boolean isShuffleQuestion()
  {
    return shuffleQuestion;
  }

  public boolean isShuffleAnswer()
  {
    return shuffleAnswer;
  }

  public boolean getQuestionsPerPage()
  {
    return questionsPerPage;
  }

  public int getThresholdScore()
  {
    return thresholdScore;
  }

  public int getAttempt()
  {
    return attempt;
  }

  public void setAttempt(int attempt)
  {
    this.attempt = attempt;
  }

  public CertificateId getCertificateId()
  {
    return certificateId;
  }

  public void setCertificateId(CertificateId certificateId)
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

  public String getEndTime()
  {
    return this.endTime;
  }

  public void setEndTime(String endTime)
  {
    this.endTime = endTime;
  }

  @Override
  public boolean sameValueAs(ExamConfig other)
  {
    return false;
  }

  public void setMaxScore(int maxScore)
  {
    this.maxScore = maxScore;
  }

  public int getMaxScore()
  {
    return maxScore;
  }

  public boolean isAutoStart()
  {
    return autoStart;
  }
}

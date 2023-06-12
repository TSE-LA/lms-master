package mn.erin.lms.base.domain.usecase.exam.dto;

import java.util.Date;
import java.util.Set;

import mn.erin.lms.base.domain.model.exam.RandomQuestionConfig;

/**
 * @author Temuulen Naranbold
 */
public class ExamInput
{
  private String id;
  private String name;
  private String description;
  private String examCategoryId;
  private String groupId;
  private String examType;
  private Date publishDate;
  private String publishTime;
  private boolean sendEmail;
  private boolean sendSMS;
  private String mailText;
  private String smsText;
  private Set<String> questionIds;
  private Set<RandomQuestionConfig> randomQuestionConfigs;
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
  private Set<String> enrolledLearners;
  private Set<String> enrolledGroups;

  public String getId()
  {
    return id;
  }

  public void setId(String id)
  {
    this.id = id;
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public String getDescription()
  {
    return description;
  }

  public void setDescription(String description)
  {
    this.description = description;
  }

  public String getExamCategoryId()
  {
    return examCategoryId;
  }

  public void setExamCategoryId(String examCategoryId)
  {
    this.examCategoryId = examCategoryId;
  }

  public String getGroupId()
  {
    return groupId;
  }

  public void setGroupId(String groupId)
  {
    this.groupId = groupId;
  }

  public String getExamType()
  {
    return examType;
  }

  public void setExamType(String examType)
  {
    this.examType = examType;
  }

  public Date getPublishDate()
  {
    return publishDate;
  }

  public void setPublishDate(Date publishDate)
  {
    this.publishDate = publishDate;
  }

  public String getPublishTime()
  {
    return publishTime;
  }

  public void setPublishTime(String publishTime)
  {
    this.publishTime = publishTime;
  }

  public boolean isSendEmail()
  {
    return sendEmail;
  }

  public void setSendEmail(boolean sendEmail)
  {
    this.sendEmail = sendEmail;
  }

  public boolean isSendSMS()
  {
    return sendSMS;
  }

  public void setSendSMS(boolean sendSMS)
  {
    this.sendSMS = sendSMS;
  }

  public String getMailText()
  {
    return mailText;
  }

  public void setMailText(String mailText)
  {
    this.mailText = mailText;
  }

  public String getSmsText()
  {
    return smsText;
  }

  public void setSmsText(String smsText)
  {
    this.smsText = smsText;
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

  public void setRandomQuestionConfigs(Set<RandomQuestionConfig> randomQuestionConfigs)
  {
    this.randomQuestionConfigs = randomQuestionConfigs;
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

  public Set<String> getEnrolledLearners()
  {
    return enrolledLearners;
  }

  public void setEnrolledLearners(Set<String> enrolledLearners)
  {
    this.enrolledLearners = enrolledLearners;
  }

  public Set<String> getEnrolledGroups()
  {
    return enrolledGroups;
  }

  public void setEnrolledGroups(Set<String> enrolledGroups)
  {
    this.enrolledGroups = enrolledGroups;
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

  public static class Builder
  {
    private String id;
    private String name;
    private String description;
    private String examCategoryId;
    private String groupId;
    private String examType;
    private Date publishDate;
    private String publishTime;
    private boolean sendEmail;
    private boolean sendSMS;
    private String mailText;
    private String smsText;
    private Set<String> questionIds;
    private Set<RandomQuestionConfig> randomQuestionConfigs;
    private String answerResult;
    private boolean shuffleQuestion;
    private boolean shuffleAnswer;
    private boolean questionsPerPage;
    private boolean autoStart;
    private int thresholdScore;
    private int maxScore;
    private int attempt;
    private String certificateId;
    private Date startDate;
    private Date endDate;
    private String startTime;
    private int duration;
    private Set<String> enrolledLearners;
    private Set<String> enrolledGroups;
    private String endTime;

    public Builder withId(String id)
    {
      this.id = id;
      return this;
    }

    public Builder withName(String name)
    {
      this.name = name;
      return this;
    }

    public Builder withDescription(String description)
    {
      this.description = description;
      return this;
    }

    public Builder withExamCategoryId(String examCategoryId)
    {
      this.examCategoryId = examCategoryId;
      return this;
    }

    public Builder withExamGroupId(String groupId)
    {
      this.groupId = groupId;
      return this;
    }

    public Builder withExamType(String examType)
    {
      this.examType = examType;
      return this;
    }

    public Builder withPublishDate(Date publishDate)
    {
      this.publishDate = publishDate;
      return this;
    }

    public Builder withPublishTime(String publishTime)
    {
      this.publishTime = publishTime;
      return this;
    }

    public Builder withSendEmail(boolean sendEmail)
    {
      this.sendEmail = sendEmail;
      return this;
    }

    public Builder withSendSMS(boolean sendSMS)
    {
      this.sendSMS = sendSMS;
      return this;
    }

    public Builder withMailText(String mailText)
    {
      this.mailText = mailText;
      return this;
    }

    public Builder withSMSText(String smsText)
    {
      this.smsText = smsText;
      return this;
    }

    public Builder withQuestionIds(Set<String> questionIds)
    {
      this.questionIds = questionIds;
      return this;
    }

    public Builder withRandomQuestionConfigs(Set<RandomQuestionConfig> randomQuestionConfigs)
    {
      this.randomQuestionConfigs = randomQuestionConfigs;
      return this;
    }

    public Builder withShowAnswerResult(String answerResult)
    {
      this.answerResult = answerResult;
      return this;
    }

    public Builder withShuffleQuestions(boolean shuffleQuestion)
    {
      this.shuffleQuestion = shuffleQuestion;
      return this;
    }

    public Builder withShuffleAnswers(boolean shuffleAnswer)
    {
      this.shuffleAnswer = shuffleAnswer;
      return this;
    }

    public Builder withQuestionsPerPage(boolean questionsPerPage)
    {
      this.questionsPerPage = questionsPerPage;
      return this;
    }

    public Builder withThresholdScore(int thresholdScore)
    {
      this.thresholdScore = thresholdScore;
      return this;
    }

    public Builder withMaxScore(int maxScore)
    {
      this.maxScore = maxScore;
      return this;
    }

    public Builder withAttempt(int attempt)
    {
      this.attempt = attempt;
      return this;
    }

    public Builder withCertificateId(String certificateId)
    {
      this.certificateId = certificateId;
      return this;
    }

    public Builder withStartDate(Date startDate)
    {
      this.startDate = startDate;
      return this;
    }

    public Builder withEndDate(Date endDate)
    {
      this.endDate = endDate;
      return this;
    }

    public Builder withStartTime(String startTime)
    {
      this.startTime = startTime;
      return this;
    }

    public Builder withEndTime(String endTime)
    {
      this.endTime = endTime;
      return this;
    }

    public Builder withDuration(int duration)
    {
      this.duration = duration;
      return this;
    }

    public Builder withEnrolledLearners(Set<String> learners)
    {
      this.enrolledLearners = learners;
      return this;
    }

    public Builder withEnrolledGroups(Set<String> groups)
    {
      this.enrolledGroups = groups;
      return this;
    }

    public Builder withAutoStart(boolean autoStart)
    {
      this.autoStart = autoStart;
      return this;
    }

    public ExamInput build()
    {
      ExamInput input = new ExamInput();
      input.id = this.id;
      input.name = this.name;
      input.description = this.description;
      input.examCategoryId = this.examCategoryId;
      input.groupId = this.groupId;
      input.examType = this.examType;
      input.publishDate = this.publishDate;
      input.publishTime = this.publishTime;
      input.sendEmail = this.sendEmail;
      input.sendSMS = this.sendSMS;
      input.mailText = this.mailText;
      input.smsText = this.smsText;
      input.questionIds = this.questionIds;
      input.randomQuestionConfigs = this.randomQuestionConfigs;
      input.answerResult = this.answerResult;
      input.shuffleQuestion = this.shuffleQuestion;
      input.shuffleAnswer = this.shuffleAnswer;
      input.questionsPerPage = this.questionsPerPage;
      input.thresholdScore = this.thresholdScore;
      input.attempt = this.attempt;
      input.certificateId = this.certificateId;
      input.startDate = this.startDate;
      input.endDate = this.endDate;
      input.startTime = this.startTime;
      input.endTime = this.endTime;
      input.duration = this.duration;
      input.enrolledLearners = this.enrolledLearners;
      input.enrolledGroups = this.enrolledGroups;
      input.maxScore = this.maxScore;
      input.autoStart = this.autoStart;

      return input;
    }
  }
}

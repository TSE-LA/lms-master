package mn.erin.lms.base.domain.usecase.exam.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import mn.erin.lms.base.domain.model.exam.question.LearnerQuestionDto;
import mn.erin.lms.base.domain.usecase.exam.dto.question.QuestionAnswerDto;

public class ExamInteractionDto
{
  private final Date initialLaunch;
  private Date lastLaunch;
  private double spentTime;
  private int attempt;
  private double score;
  private String successStatus;
  private String completionStatus;
  private String bookmark;
  private boolean ongoing;
  private List<QuestionAnswerDto> questionAnswers = new ArrayList<>();
  private List<LearnerQuestionDto> givenQuestions = new ArrayList<>();

  public ExamInteractionDto(Date initialLaunch)
  {
    this.initialLaunch = initialLaunch;
  }

  public static ExamInteractionDto newOngoingInteraction(Date initialLaunch)
  {
    ExamInteractionDto interaction = new ExamInteractionDto(initialLaunch);
    interaction.ongoing = true;
    return interaction;
  }
  public void setQuestionAnswers(List<QuestionAnswerDto> questionAnswers)
  {
    this.questionAnswers = questionAnswers;
  }

  public List<LearnerQuestionDto> getGivenQuestions()
  {
    return givenQuestions;
  }

  public void setGivenQuestions(List<LearnerQuestionDto> givenQuestions)
  {
    this.givenQuestions = givenQuestions;
  }

  public boolean isOngoing()
  {
    return ongoing;
  }

  public void setOngoing(boolean ongoing)
  {
    this.ongoing = ongoing;
  }

  public Date getInitialLaunch()
  {
    return initialLaunch;
  }

  public Date getLastLaunch()
  {
    return lastLaunch;
  }

  public void setLastLaunch(Date lastLaunch)
  {
    this.lastLaunch = lastLaunch;
  }

  public double getSpentTime()
  {
    return spentTime;
  }

  public void setSpentTime(double spentTime)
  {
    this.spentTime = spentTime;
  }

  public int getAttempt()
  {
    return attempt;
  }

  public void setAttempt(int attempt)
  {
    this.attempt = attempt;
  }

  public double getScore()
  {
    return score;
  }

  public void setScore(double score)
  {
    this.score = score;
  }

  public String getSuccessStatus()
  {
    return successStatus;
  }

  public void setSuccessStatus(String successStatus)
  {
    this.successStatus = successStatus;
  }

  public String getCompletionStatus()
  {
    return completionStatus;
  }

  public void setCompletionStatus(String completionStatus)
  {
    this.completionStatus = completionStatus;
  }

  public String getBookmark()
  {
    return bookmark;
  }

  public void setBookmark(String bookmark)
  {
    this.bookmark = bookmark;
  }

  public List<QuestionAnswerDto> getQuestionAnswers()
  {
    return Collections.unmodifiableList(questionAnswers);
  }
}

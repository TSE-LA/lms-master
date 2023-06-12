package mn.erin.lms.base.domain.usecase.exam.dto;

import java.util.List;

import mn.erin.lms.base.domain.model.exam.question.LearnerQuestionDto;

/**
 * @author Byambajav
 */
public class StartExamDto
{
  private String title;
  private long remainingTime;
  private long duration;
  private List<LearnerQuestionDto> learnerQuestion;

  public StartExamDto(String title, long duration, long remainingTime, List<LearnerQuestionDto> learnerQuestion)
  {
    this.title = title;
    this.duration = duration;
    this.remainingTime = remainingTime;
    this.learnerQuestion = learnerQuestion;
  }

  public String getTitle()
  {
    return title;
  }

  public void setTitle(String title)
  {
    this.title = title;
  }

  public long getDuration()
  {
    return duration;
  }

  public void setDuration(long duration)
  {
    this.duration = duration;
  }

  public long getRemainingTime()
  {
    return remainingTime;
  }

  public void setRemainingTime(long remainingTime)
  {
    this.remainingTime = remainingTime;
  }

  public List<LearnerQuestionDto> getLearnerQuestion()
  {
    return learnerQuestion;
  }

  public void setLearnerQuestion(List<LearnerQuestionDto> learnerQuestion)
  {
    this.learnerQuestion = learnerQuestion;
  }
}

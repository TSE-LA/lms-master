package mn.erin.lms.base.domain.usecase.assessment.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.Validate;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class CreateQuizInput
{
  private List<QuestionInfo> questions = new ArrayList<>();

  private final String name;
  private final boolean graded;
  private final Long timeLimit;
  private final LocalDateTime dueDate;

  private Integer maxAttempts;
  private Double thresholdScore;

  public CreateQuizInput(String name, boolean graded, Long timeLimit, LocalDateTime dueDate)
  {
    this.name = Validate.notBlank(name);
    this.graded = graded;
    this.timeLimit = timeLimit;
    this.dueDate = dueDate;
  }

  public void addQuestion(QuestionInfo question)
  {
    this.questions.add(question);
  }

  public void setMaxAttempts(Integer maxAttempts)
  {
    this.maxAttempts = maxAttempts;
  }

  public void setThresholdScore(Double thresholdScore)
  {
    this.thresholdScore = thresholdScore;
  }

  public List<QuestionInfo> getQuestions()
  {
    return questions;
  }

  public String getName()
  {
    return name;
  }

  public boolean isGraded()
  {
    return graded;
  }

  public Long getTimeLimit()
  {
    return timeLimit;
  }

  public LocalDateTime getDueDate()
  {
    return dueDate;
  }

  public Integer getMaxAttempts()
  {
    return maxAttempts;
  }

  public Double getThresholdScore()
  {
    return thresholdScore;
  }
}

package mn.erin.lms.base.domain.usecase.assessment.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class QuizDto
{
  private String id;
  private String name;
  private boolean graded;
  private Long timeLimit;
  private LocalDateTime dueDate;
  private Integer maxAttempts;
  private Double thresholdScore;
  private List<QuestionInfo> questions = new ArrayList<>();

  public QuizDto(String id, String name, boolean graded, Long timeLimit, LocalDateTime dueDate)
  {
    this.id = id;
    this.name = name;
    this.graded = graded;
    this.timeLimit = timeLimit;
    this.dueDate = dueDate;
  }

  public List<QuestionInfo> getQuestions()
  {
    return questions;
  }

  public void addQuestions(QuestionInfo question)
  {
    this.questions.add(question);
  }

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

  public boolean isGraded()
  {
    return graded;
  }

  public void setGraded(boolean graded)
  {
    this.graded = graded;
  }

  public Long getTimeLimit()
  {
    return timeLimit;
  }

  public void setTimeLimit(Long timeLimit)
  {
    this.timeLimit = timeLimit;
  }

  public LocalDateTime getDueDate()
  {
    return dueDate;
  }

  public void setDueDate(LocalDateTime dueDate)
  {
    this.dueDate = dueDate;
  }

  public Integer getMaxAttempts()
  {
    return maxAttempts;
  }

  public void setMaxAttempts(Integer maxAttempts)
  {
    this.maxAttempts = maxAttempts;
  }

  public Double getThresholdScore()
  {
    return thresholdScore;
  }

  public void setThresholdScore(Double thresholdScore)
  {
    this.thresholdScore = thresholdScore;
  }
}

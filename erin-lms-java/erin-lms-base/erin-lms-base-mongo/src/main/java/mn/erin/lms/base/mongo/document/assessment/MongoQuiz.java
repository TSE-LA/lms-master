package mn.erin.lms.base.mongo.document.assessment;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class MongoQuiz
{
  @Id
  private String id;

  @Indexed
  private String name;

  private boolean graded;
  private Long timeLimit;
  private LocalDateTime dueDate;
  private Integer maxAttempts;
  private Double thresholdScore;

  private List<MongoQuestion> questions;

  public MongoQuiz()
  {
  }

  public MongoQuiz(String id, String name)
  {
    this.id = id;
    this.name = name;
  }

  public MongoQuiz(String id, String name, boolean graded, Long timeLimit, LocalDateTime dueDate, Integer maxAttempts, Double thresholdScore,
      List<MongoQuestion> questions)
  {
    this.id = id;
    this.name = name;
    this.graded = graded;
    this.timeLimit = timeLimit;
    this.dueDate = dueDate;
    this.maxAttempts = maxAttempts;
    this.thresholdScore = thresholdScore;
    this.questions = questions;
  }

  public MongoQuiz(String id, String name, boolean graded, Long timeLimit, LocalDateTime dueDate,
      List<MongoQuestion> questions)
  {
    this.id = id;
    this.name = name;
    this.graded = graded;
    this.timeLimit = timeLimit;
    this.dueDate = dueDate;
    this.questions = questions;
  }

  public void setMaxAttempts(Integer maxAttempts)
  {
    this.maxAttempts = maxAttempts;
  }

  public void setThresholdScore(Double thresholdScore)
  {
    this.thresholdScore = thresholdScore;
  }

  public void setQuestions(List<MongoQuestion> questions)
  {
    this.questions = questions;
  }

  public String getId()
  {
    return id;
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

  public List<MongoQuestion> getQuestions()
  {
    return questions;
  }
}

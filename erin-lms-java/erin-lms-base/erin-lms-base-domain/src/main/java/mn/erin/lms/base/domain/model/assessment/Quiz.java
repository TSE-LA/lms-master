package mn.erin.lms.base.domain.model.assessment;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.model.Entity;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class Quiz implements Entity<Quiz>
{
  private final QuizId quizId;
  private final String name;
  private boolean graded;
  private List<Question> questions = new ArrayList<>();

  private Long timeLimit;
  private LocalDateTime dueDate;
  private Integer maxAttempts;
  private Double thresholdScore;

  public Quiz(QuizId quizId, String name)
  {
    this(quizId, name, false, 0L, null);
  }

  public Quiz(QuizId quizId, String name, boolean graded, Long timeLimit, LocalDateTime dueDate)
  {
    this.quizId = Objects.requireNonNull(quizId, "Quiz ID cannot be null!!");
    this.name = Validate.notBlank(name, "Quiz name cannot be blank!");
    this.graded = graded;
    this.timeLimit = timeLimit;
    this.dueDate = dueDate;
  }

  public void addQuestion(Question question)
  {
    this.questions.add(question);
  }

  public void setMaxAttempts(Integer maxAttempts)
  {
    this.maxAttempts = maxAttempts;
  }

  public void setGraded(boolean graded)
  {
    this.graded = graded;
  }

  public void setThresholdScore(Double thresholdScore)
  {
    if (thresholdScore != null)
    {
      double overallScore = 0;
      for (Question question : questions)
      {
        List<Answer> answers = question.getAnswers();

        for (Answer answer : answers)
        {
          if (answer.isCorrect())
          {
            overallScore += answer.getScore();
          }
        }
      }

      if (thresholdScore > overallScore)
      {
        throw new IllegalArgumentException("Threshold score cannot be greater than the overall score of the test");
      }

      this.thresholdScore = thresholdScore;
    }
  }

  public QuizId getQuizId()
  {
    return quizId;
  }

  public String getName()
  {
    return name;
  }

  public boolean isGraded()
  {
    return graded;
  }

  public List<Question> getQuestions()
  {
    return questions;
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

  @Override
  public boolean sameIdentityAs(Quiz other)
  {
    return this.quizId.equals(other.quizId);
  }
}

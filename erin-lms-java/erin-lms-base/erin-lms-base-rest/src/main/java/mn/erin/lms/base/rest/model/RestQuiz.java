package mn.erin.lms.base.rest.model;

import java.util.List;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class RestQuiz
{
  private List<RestQuestion> questions;
  private Integer maxAttempts;
  private Double thresholdScore;
  private String quizId;
  private boolean isRequired;

  public List<RestQuestion> getQuestions()
  {
    return questions;
  }

  public void setQuestions(List<RestQuestion> questions)
  {
    this.questions = questions;
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

  public String getQuizId() {
    return quizId;
  }

  public void setQuizId(String quizId) {
    this.quizId = quizId;
  }

  public boolean isRequired() {
    return isRequired;
  }

  public void setRequired(boolean required) {
    isRequired = required;
  }
}

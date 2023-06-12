package mn.erin.lms.base.rest.model.exam;

/**
 * @author Galsan Bayart
 */
public class RestMainAnswerEntity
{
  private String value;
  private double score;
  private boolean correct;

  public RestMainAnswerEntity(String value, double score, boolean correct)
  {
    this.value = value;
    this.score = score;
    this.correct = correct;
  }

  public RestMainAnswerEntity()
  {
  }

  public String getValue()
  {
    return value;
  }

  public void setValue(String value)
  {
    this.value = value;
  }

  public double getScore()
  {
    return score;
  }

  public void setScore(double score)
  {
    this.score = score;
  }

  public boolean isCorrect()
  {
    return correct;
  }

  public void setCorrect(boolean correct)
  {
    this.correct = correct;
  }
}

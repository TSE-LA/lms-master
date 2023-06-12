package mn.erin.lms.base.domain.model.exam.question;

/**
 * @author Galsan Bayart.
 */
public class MainAnswerEntity
{
  private String value;
  private double score;
  private boolean correct;

  public MainAnswerEntity(String value, double score, boolean correct)
  {
    this.value = value;
    this.score = score;
    this.correct = correct;
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

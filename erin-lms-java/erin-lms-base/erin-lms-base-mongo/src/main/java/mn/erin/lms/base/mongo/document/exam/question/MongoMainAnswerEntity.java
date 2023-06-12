package mn.erin.lms.base.mongo.document.exam.question;

/**
 * @author Galsan Bayart.
 */
public class MongoMainAnswerEntity
{
  private String value;
  private double score;
  private boolean isCorrect;

  public MongoMainAnswerEntity(String value, double score, boolean isCorrect)
  {
    this.value = value;
    this.score = score;
    this.isCorrect = isCorrect;
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
    return isCorrect;
  }

  public void setCorrect(boolean correct)
  {
    isCorrect = correct;
  }
}

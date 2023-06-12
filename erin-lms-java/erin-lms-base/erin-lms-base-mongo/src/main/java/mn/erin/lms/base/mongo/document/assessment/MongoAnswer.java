package mn.erin.lms.base.mongo.document.assessment;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class MongoAnswer
{
  private String value;
  private boolean correct;
  private Double score;

  public MongoAnswer()
  {
  }

  public MongoAnswer(String value, boolean correct, Double score)
  {
    this.value = value;
    this.correct = correct;
    this.score = score;
  }

  public String getValue()
  {
    return value;
  }

  public boolean isCorrect()
  {
    return correct;
  }

  public Double getScore()
  {
    return score;
  }
}

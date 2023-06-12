package mn.erin.lms.base.mongo.document.exam.question;

/**
 * @author Galsan Bayart.
 */
public class MongoMatchAnswerEntity
{
  String value;
  int score;
  int key;

  public MongoMatchAnswerEntity(String value, int score, int key)
  {
    this.value = value;
    this.score = score;
    this.key = key;
  }

  public String getValue()
  {
    return value;
  }

  public void setValue(String value)
  {
    this.value = value;
  }

  public int getScore()
  {
    return score;
  }

  public void setScore(int score)
  {
    this.score = score;
  }

  public int getKey()
  {
    return key;
  }

  public void setKey(int key)
  {
    this.key = key;
  }
}

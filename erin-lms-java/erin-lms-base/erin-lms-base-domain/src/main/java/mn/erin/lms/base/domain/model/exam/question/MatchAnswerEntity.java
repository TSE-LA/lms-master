package mn.erin.lms.base.domain.model.exam.question;

/**
 * @author Galsan Bayart.
 */
public class MatchAnswerEntity
{
  String value;
  int score;
  int key;

  public MatchAnswerEntity(String value, int score, int key)
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

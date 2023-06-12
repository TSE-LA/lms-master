package mn.erin.lms.base.rest.model.exam;

/**
 * @author Galsan Bayart.
 */
public class RestRandomQuestion
{
  private String groupId;
  private String categoryId;
  private int score;
  private int amount;

  public RestRandomQuestion()
  {
    //doNothing
  }

  public String getGroupId()
  {
    return groupId;
  }

  public void setGroupId(String groupId)
  {
    this.groupId = groupId;
  }

  public String getCategoryId()
  {
    return categoryId;
  }

  public void setCategoryId(String categoryId)
  {
    this.categoryId = categoryId;
  }

  public int getScore()
  {
    return score;
  }

  public void setScore(int score)
  {
    this.score = score;
  }

  public int getAmount()
  {
    return amount;
  }

  public void setAmount(int amount)
  {
    this.amount = amount;
  }
}

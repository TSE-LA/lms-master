package mn.erin.lms.base.mongo.document.exam;

import org.apache.commons.lang3.Validate;

/**
 * @author Galsan Bayart.
 */
public class MongoRandomQuestionConfig
{
  private String groupId;
  private String categoryId;
  private int score;
  private int amount;

  public MongoRandomQuestionConfig(String groupId, String categoryId, int score, int amount)
  {
    this.groupId = Validate.notBlank(groupId);
    this.categoryId = Validate.notBlank(categoryId);
    this.score = score;
    this.amount = amount;
  }

  public MongoRandomQuestionConfig()
  {
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

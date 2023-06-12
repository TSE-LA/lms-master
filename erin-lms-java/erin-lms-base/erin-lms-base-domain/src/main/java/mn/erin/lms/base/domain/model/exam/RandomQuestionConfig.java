package mn.erin.lms.base.domain.model.exam;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

/**
 * @author Galsan Bayart.
 */
public class RandomQuestionConfig implements Serializable
{
  private String groupId;
  private String categoryId;
  private int score;
  private int amount;

  public RandomQuestionConfig(String groupId, String categoryId, int score, int amount)
  {
    if (StringUtils.isBlank(groupId) && StringUtils.isBlank(categoryId))
    {
      throw new NullPointerException("Group ID and Category ID both cannot be null or blank.");
    }
    this.groupId = groupId;
    this.categoryId = categoryId;
    this.score = score;
    this.amount = amount;
  }

  public String getGroupId()
  {
    return groupId;
  }

  public void setGroupId(String groupId)
  {
    this.groupId = Validate.notBlank(groupId);
  }

  public String getCategoryId()
  {
    return categoryId;
  }

  public void setCategoryId(String categoryId)
  {
    this.categoryId = Validate.notBlank(categoryId);
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

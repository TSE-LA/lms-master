package mn.erin.lms.legacy.domain.unitel.usecase.notification;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Oyungerel Chuluunsukh.
 */
public class GetPromotionNotificationOutput
{
  private String categoryId;
  private int total = 0;
  private int newTotal = 0;
  private List<GetPromotionNotificationOutput> subCategory = new ArrayList<>();

  public String getCategoryId()
  {
    return categoryId;
  }

  public void setCategoryId(String categoryId)
  {
    this.categoryId = categoryId;
  }

  public int getTotal()
  {
    return total;
  }

  public void setTotal(int total)
  {
    this.total = total;
  }

  public void addTotal(int number)
  {
    this.total += number;
  }

  public void addNewTotal(int number)
  {
    this.newTotal += number;
  }

  public int getNewTotal()
  {
    return newTotal;
  }

  public void setNewTotal(Integer newTotal)
  {
    this.newTotal = newTotal;
  }

  public List<GetPromotionNotificationOutput> getSubCategory()
  {
    return subCategory;
  }

  public void addToSubCategory(GetPromotionNotificationOutput subCategory)
  {
    this.subCategory.add(subCategory);
    this.addTotal(subCategory.getTotal());
    this.addNewTotal(subCategory.getNewTotal());
  }
}

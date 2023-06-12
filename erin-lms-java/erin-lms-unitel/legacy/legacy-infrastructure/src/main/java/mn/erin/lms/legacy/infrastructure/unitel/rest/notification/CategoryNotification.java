package mn.erin.lms.legacy.infrastructure.unitel.rest.notification;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Oyungerel Chuluunsukh.
 */
public class CategoryNotification
{
  private String id;
  private Integer total;
  private Integer newTotal;
  private List<CategoryNotification> subCategory = new ArrayList<>();

  public CategoryNotification(String id, Integer total, Integer newTotal){
    this.id = id;
    this.total = total;
    this.newTotal = newTotal;
  }

  public String getId()
  {
    return id;
  }

  public void setId(String id)
  {
    this.id = id;
  }

  public Integer getTotal()
  {
    return total;
  }

  public void setTotal(Integer total)
  {
    this.total = total;
  }

  public Integer getNewTotal()
  {
    return newTotal;
  }

  public void setNewTotal(Integer newTotal)
  {
    this.newTotal = newTotal;
  }

  public List<CategoryNotification> getSubCategory()
  {
    return subCategory;
  }

  public void addToSubCategory(CategoryNotification subCategory)
  {
    this.subCategory.add(subCategory);
  }
}

package mn.erin.lms.legacy.domain.unitel.usecase.category.get_category;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class GetPromotionCategoryOutput
{
  private final String categoryId;
  private final String parentCategoryId;
  private final String categoryName;
  private String description;

  public GetPromotionCategoryOutput(String categoryId, String parentCategoryId, String categoryName)
  {
    this.categoryId = categoryId;
    this.parentCategoryId = parentCategoryId;
    this.categoryName = categoryName;
  }

  public String getCategoryId()
  {
    return categoryId;
  }

  public String getParentCategoryId()
  {
    return parentCategoryId;
  }

  public String getCategoryName()
  {
    return categoryName;
  }

  public String getDescription()
  {
    return description;
  }

  public void setDescription(String description)
  {
    this.description = description;
  }
}

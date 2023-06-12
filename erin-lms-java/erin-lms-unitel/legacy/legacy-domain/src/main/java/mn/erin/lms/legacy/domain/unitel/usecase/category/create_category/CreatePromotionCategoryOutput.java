package mn.erin.lms.legacy.domain.unitel.usecase.category.create_category;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class CreatePromotionCategoryOutput
{
  private final String categoryId;

  public CreatePromotionCategoryOutput(String categoryId)
  {
    this.categoryId = categoryId;
  }

  public String getCategoryId()
  {
    return categoryId;
  }
}

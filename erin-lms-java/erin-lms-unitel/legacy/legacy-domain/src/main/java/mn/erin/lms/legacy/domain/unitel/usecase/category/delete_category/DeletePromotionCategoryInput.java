package mn.erin.lms.legacy.domain.unitel.usecase.category.delete_category;

import org.apache.commons.lang3.Validate;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class DeletePromotionCategoryInput
{
  private final String categoryName;

  public DeletePromotionCategoryInput(String categoryName)
  {
    this.categoryName = Validate.notBlank(categoryName, "Promotion category name cannot be null or blank!");
  }

  public String getCategoryName()
  {
    return categoryName;
  }
}

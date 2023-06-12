package mn.erin.lms.legacy.domain.unitel.usecase.category.create_category;

import org.apache.commons.lang3.Validate;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class CreatePromotionCategoryInput
{
  private final String categoryName;
  private String description;

  public CreatePromotionCategoryInput(String categoryName)
  {
    this.categoryName = Validate.notBlank(categoryName, "Promotion category name cannot be null or blank!");
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

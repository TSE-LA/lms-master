package mn.erin.lms.base.domain.usecase.category.dto;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class CourseCategoryDto
{
  private final String categoryId;
  private final String parentCategoryId;
  private final String categoryName;

  private String description;
  private boolean autoEnroll;

  public CourseCategoryDto(String categoryId, String parentCategoryId, String categoryName)
  {
    this.categoryId = categoryId;
    this.parentCategoryId = parentCategoryId;
    this.categoryName = categoryName;
  }

  public void setDescription(String description)
  {
    this.description = description;
  }

  public void setAutoEnroll(boolean autoEnroll)
  {
    this.autoEnroll = autoEnroll;
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

  public boolean isAutoEnroll()
  {
    return autoEnroll;
  }
}

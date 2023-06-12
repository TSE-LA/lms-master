package mn.erin.lms.base.domain.usecase.category.dto;

import org.apache.commons.lang3.Validate;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class CreateCourseCategoryInput
{
  private final String parentId;
  private final String categoryName;

  private String description;
  private boolean autoEnroll;

  public CreateCourseCategoryInput(String parentId, String categoryName)
  {
    this.parentId = parentId;
    this.categoryName = Validate.notBlank(categoryName);
  }

  public void setDescription(String description)
  {
    this.description = description;
  }

  public String getDescription()
  {
    return description;
  }

  public boolean isAutoEnroll()
  {
    return autoEnroll;
  }

  public void setAutoEnroll(boolean autoEnroll)
  {
    this.autoEnroll = autoEnroll;
  }

  public String getParentId()
  {
    return parentId;
  }

  public String getCategoryName()
  {
    return categoryName;
  }
}

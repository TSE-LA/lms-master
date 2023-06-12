package mn.erin.lms.base.rest.model;

/**
 * @author Munkh
 */
public class RestCourseCategory
{
  private String parentCategoryId;
  private String categoryId;
  private String categoryName;
  private String description;
  private boolean autoEnroll;

  public RestCourseCategory()
  {
    autoEnroll = false;
  }

  public RestCourseCategory(String parentCategoryId, String categoryName, String description, boolean autoEnroll)
  {
    this.parentCategoryId = parentCategoryId;
    this.categoryName = categoryName;
    this.description = description;
    this.autoEnroll = autoEnroll;
  }

  public String getParentCategoryId()
  {
    return parentCategoryId;
  }

  public void setParentCategoryId(String parentCategoryId)
  {
    this.parentCategoryId = parentCategoryId;
  }

  public String getCategoryId()
  {
    return categoryId;
  }

  public void setCategoryId(String categoryId)
  {
    this.categoryId = categoryId;
  }

  public String getCategoryName()
  {
    return categoryName;
  }

  public void setCategoryName(String categoryName)
  {
    this.categoryName = categoryName;
  }

  public String getDescription()
  {
    return description;
  }

  public void setDescription(String description)
  {
    this.description = description;
  }

  public boolean isAutoEnroll()
  {
    return autoEnroll;
  }

  public void setAutoEnroll(boolean autoEnroll)
  {
    this.autoEnroll = autoEnroll;
  }
}

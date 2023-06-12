package mn.erin.lms.base.domain.usecase.category.dto;

import org.apache.commons.lang3.Validate;

/**
 * @author Temuulen Naranbold
 */
public class ExamCategoryInput
{
  private String id;
  private int index;
  private String parentCategoryId;
  private String categoryName;
  private String description;

  public ExamCategoryInput(String id, int index, String parentCategoryId, String categoryName, String description)
  {
    this.id = id;
    this.index = index;
    this.parentCategoryId = Validate.notBlank(parentCategoryId);
    this.categoryName = Validate.notBlank(categoryName);
    this.description = description;
  }

  public ExamCategoryInput(String id, int index, String categoryName, String description)
  {
    this.id = Validate.notBlank(id);
    this.index = index;
    this.categoryName = Validate.notBlank(categoryName);
    this.description = description;
  }

  public String getId()
  {
    return id;
  }

  public void setId(String id)
  {
    this.id = id;
  }

  public int getIndex()
  {
    return index;
  }

  public void setIndex(int index)
  {
    this.index = index;
  }

  public String getParentCategoryId()
  {
    return parentCategoryId;
  }

  public void setParentCategoryId(String parentCategoryId)
  {
    this.parentCategoryId = parentCategoryId;
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
}

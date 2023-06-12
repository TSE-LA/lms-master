package mn.erin.lms.base.domain.usecase.category.dto;

import org.apache.commons.lang3.Validate;

/**
 * @author Temuulen Naranbold
 */
public class ExamCategoryDto
{
  private final String categoryId;
  private final String parentCategoryId;
  private final int index;
  private final String name;
  private String description;

  public ExamCategoryDto(String categoryId, String parentCategoryId, int index, String name, String description)
  {
    this.categoryId = Validate.notBlank(categoryId);
    this.parentCategoryId = parentCategoryId;
    this.index = index;
    this.name = Validate.notBlank(name);
    this.description = description;
  }

  public String getCategoryId()
  {
    return categoryId;
  }

  public String getParentCategoryId()
  {
    return parentCategoryId;
  }

  public int getIndex()
  {
    return index;
  }

  public String getName()
  {
    return name;
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

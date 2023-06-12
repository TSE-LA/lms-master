package mn.erin.lms.base.domain.usecase.category.dto;

import org.apache.commons.lang3.Validate;

/**
 * @author Temuulen Naranbold
 */
public class QuestionCategoryDto
{
  private final String id;
  private final String parentId;
  private final int index;
  private final String name;
  private String description;

  public QuestionCategoryDto(String id, String parentId, int index, String name, String description)
  {
    this.id = Validate.notBlank(id);
    this.parentId = parentId;
    this.index = index;
    this.name = Validate.notBlank(name);
    this.description = description;
  }

  public String getId()
  {
    return id;
  }

  public String getParentId()
  {
    return parentId;
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

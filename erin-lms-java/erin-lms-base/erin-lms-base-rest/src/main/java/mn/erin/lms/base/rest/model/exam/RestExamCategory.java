package mn.erin.lms.base.rest.model.exam;

/**
 * @author Galsan Bayart.
 */
public class RestExamCategory
{
  private String id;
  private int index;
  private String parentCategoryId;
  private String name;
  private String description;

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

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
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

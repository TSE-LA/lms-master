package mn.erin.lms.base.domain.usecase.enrollment.dto;

/**
 * @author Munkh
 */
public class AssignCoursesToGroupInput
{
  private final String id;
  private final String parentId;
  private String categoryName;

  public AssignCoursesToGroupInput(String id, String parentId)
  {
    this.id = id;
    this.parentId = parentId;
  }

  public String getId()
  {
    return id;
  }

  public String getParentId()
  {
    return parentId;
  }

  public String getCategoryName()
  {
    return categoryName;
  }

  public void setCategoryName(String categoryName)
  {
    this.categoryName = categoryName;
  }
}

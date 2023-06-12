package mn.erin.lms.base.domain.usecase.enrollment.dto;

/**
 *
 * @author Munkh
 */
public class GetCategoryIdInput
{
  private final String parentId;
  private final String categoryName;

  public GetCategoryIdInput(String parentId, String categoryName)
  {
    this.parentId = parentId;
    this.categoryName = categoryName;
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

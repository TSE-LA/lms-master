package mn.erin.lms.base.domain.usecase.exam.dto;

/**
 * @author Galsan Bayart.
 */
public class GetExamInput
{
  private String categoryId;
  private String groupId;

  public GetExamInput(String categoryId, String groupId)
  {
    this.categoryId = categoryId;
    this.groupId = groupId;
  }

  public String getCategoryId()
  {
    return categoryId;
  }

  public void setCategoryId(String categoryId)
  {
    this.categoryId = categoryId;
  }

  public String getGroupId()
  {
    return groupId;
  }

  public void setGroupId(String groupId)
  {
    this.groupId = groupId;
  }
}

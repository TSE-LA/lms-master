package mn.erin.lms.legacy.domain.lms.usecase.readership.create_readership;

/**
 * @author Munkh
 */
public class UpdateCourseEnrollmentsInput
{
  private String groupId;
  private String newGroupId;

  public UpdateCourseEnrollmentsInput(String groupId, String newGroupId)
  {
    this.groupId = groupId;
    this.newGroupId = newGroupId;
  }

  public String getGroupId()
  {
    return groupId;
  }

  public void setGroupId(String groupId)
  {
    this.groupId = groupId;
  }

  public String getNewGroupId()
  {
    return newGroupId;
  }

  public void setNewGroupId(String newGroupId)
  {
    this.newGroupId = newGroupId;
  }
}

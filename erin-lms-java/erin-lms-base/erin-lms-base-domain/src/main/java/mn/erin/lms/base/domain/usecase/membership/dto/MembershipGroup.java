package mn.erin.lms.base.domain.usecase.membership.dto;

public class MembershipGroup
{
  private final String groupId;
  private final String groupName;

  public MembershipGroup(String groupId, String groupName)
  {
    this.groupId = groupId;
    this.groupName = groupName;
  }

  public String getGroupId()
  {
    return groupId;
  }

  public String getGroupName()
  {
    return groupName;
  }
}

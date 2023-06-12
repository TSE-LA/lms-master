package mn.erin.lms.base.rest.api.aim.model;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class MembershipResponse
{
  private String membershipId;
  private String groupId;
  private String roleId;

  public MembershipResponse(String membershipId, String groupId, String roleId)
  {
    this.membershipId = membershipId;
    this.groupId = groupId;
    this.roleId = roleId;
  }

  public String getMembershipId()
  {
    return membershipId;
  }

  public String getGroupId()
  {
    return groupId;
  }

  public String getRoleId()
  {
    return roleId;
  }
}

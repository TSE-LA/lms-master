package mn.erin.lms.base.rest.api.aim.model;

/**
 * @author Zorig
 */
public class RestMembership
{
  private String groupId;
  private String roleId;
  private String userId;
  private String membershipId;

  public String getGroupId()
  {
    return groupId;
  }

  public void setGroupId(String groupId)
  {
    this.groupId = groupId;
  }

  public String getRoleId()
  {
    return roleId;
  }

  public void setRoleId(String roleId)
  {
    this.roleId = roleId;
  }

  public String getUserId()
  {
    return userId;
  }

  public void setUserId(String userId)
  {
    this.userId = userId;
  }

  public void setMembershipId(String membershipId)
  {
    this.membershipId = membershipId;
  }

  public String getMembershipId()
  {
    return membershipId;
  }
}

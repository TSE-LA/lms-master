package mn.erin.lms.base.rest.api.aim.model;

import java.util.List;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class RestMemberships
{
  private String groupId;
  private String roleId;
  private List<String> users;

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

  public List<String> getUsers()
  {
    return users;
  }

  public void setUsers(List<String> users)
  {
    this.users = users;
  }
}

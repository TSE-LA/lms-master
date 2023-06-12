package mn.erin.domain.aim.usecase.group;

/**
 * @author Zorig
 */
public class GetUsersByRoleInput
{
  private final String roleId;

  public GetUsersByRoleInput(String roleId)
  {
    this.roleId = roleId;
  }

  public String getRoleId()
  {
    return roleId;
  }
}

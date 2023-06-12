package mn.erin.domain.aim.model.role;

/**
 * @author Munkh
 */
public enum AimRole
{
  ADMIN, MANAGER, USER;

  public RoleId getRoleId()
  {
    return RoleId.valueOf(name());
  }

  public String getMessageKey()
  {
    return "erin.aim.role." + name().toLowerCase();
  }
}

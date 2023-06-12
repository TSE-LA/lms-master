package mn.erin.lms.base.aim.role;

import mn.erin.domain.aim.model.role.RoleId;

/**
 * @author Munkh
 */
public enum LmsRole
{
  LMS_USER,
  LMS_SUPERVISOR,
  LMS_MANAGER,
  LMS_ADMIN;

  public RoleId getRoleId()
  {
    return RoleId.valueOf(name());
  }

  public String getMessageKey()
  {
    return "erin.aim.role." + name().toLowerCase().replace('_', '-');
  }
}

package mn.erin.lms.base.aim.permission;

import java.util.Set;

import mn.erin.domain.aim.model.permission.Permission;

/**
 * @author Bat-Erdene Tsogoo.
 */
public interface PermissionProvider
{
  Set<Permission> getPermissionsByRole(String role);
}

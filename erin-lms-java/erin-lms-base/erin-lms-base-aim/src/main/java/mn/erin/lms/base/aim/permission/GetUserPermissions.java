package mn.erin.lms.base.aim.permission;

import java.util.HashSet;
import java.util.Set;

import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.base.usecase.UseCase;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class GetUserPermissions implements UseCase<String, Set<PermissionDto>>
{
  private final PermissionProvider permissionProvider;

  public GetUserPermissions(PermissionProvider permissionProvider)
  {
    this.permissionProvider = permissionProvider;
  }

  @Override
  public Set<PermissionDto> execute(String role)
  {
    Set<Permission> permissions = permissionProvider.getPermissionsByRole(role);

    Set<PermissionDto> result = new HashSet<>();

    for (Permission permission : permissions)
    {
      result.add(new PermissionDto(permission.getPermissionString(), permission.getProperties()));
    }

    return result;
  }
}

package mn.erin.lms.base.aim.role;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.model.role.Role;
import mn.erin.domain.aim.model.role.RoleId;
import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.aim.repository.RoleRepository;
import mn.erin.domain.base.model.EntityId;
import mn.erin.lms.base.aim.AccessIdentityManagement;
import mn.erin.lms.base.aim.permission.PermissionProvider;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class LmsRoleRepository implements RoleRepository
{
  private final Map<LmsRole, Role> lmsAllRoles = new EnumMap<>(LmsRole.class);

  private final AccessIdentityManagement accessIdentityManagement;
  private final TenantId tenantId;

  public LmsRoleRepository(AccessIdentityManagement accessIdentityManagement, TenantId tenantId, ResourceBundle messageSet,
      PermissionProvider permissionProvider)
  {
    this.accessIdentityManagement = accessIdentityManagement;
    this.tenantId = tenantId;
    for (LmsRole lmsRole : LmsRole.values())
    {
      Role role = new Role(lmsRole.getRoleId(), tenantId, messageSet.getString(lmsRole.getMessageKey()));
      Set<Permission> permissions = permissionProvider.getPermissionsByRole(lmsRole.getRoleId().getId());
      role.setPermissions(permissions);
      lmsAllRoles.put(lmsRole, role);
    }
  }

  @Override
  public List<Role> listAll(TenantId tenantId)
  {
    if (this.tenantId.equals(tenantId))
    {
      return listRolesByAccess();
    }
    return Collections.emptyList();
  }

  @Override
  public boolean doesExistById(RoleId roleId)
  {
    return findById(roleId) != null;
  }

  @Override
  public Role create(TenantId tenantId, String s, String s1, Collection<Permission> collection) throws AimRepositoryException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Role create(TenantId tenantId, String s, String s1) throws AimRepositoryException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Role findById(EntityId entityId)
  {
    for (Role role : lmsAllRoles.values())
    {
      if (role.getRoleId().equals(entityId))
      {
        return role;
      }
    }
    return null;
  }

  @Override
  public Collection<Role> findAll()
  {
    return Collections.unmodifiableCollection(lmsAllRoles.values());
  }

  private List<Role> listRolesByAccess()
  {
    String username = accessIdentityManagement.getCurrentUsername();
    String role = accessIdentityManagement.getRole(username);

    LmsRole lmsRole = LmsRole.valueOf(role);

    if (lmsRole == LmsRole.LMS_ADMIN)
    {
      return new ArrayList<>(lmsAllRoles.values());
    }
    else if (lmsRole == LmsRole.LMS_MANAGER)
    {
      List<Role> roles = new ArrayList<>();
      roles.add(lmsAllRoles.get(LmsRole.LMS_USER));
      roles.add(lmsAllRoles.get(LmsRole.LMS_SUPERVISOR));
      return roles;
    }
    else if (lmsRole == LmsRole.LMS_SUPERVISOR)
    {
      return Collections.singletonList(lmsAllRoles.get(LmsRole.LMS_USER));
    }
    return Collections.emptyList();
  }
}

package mn.erin.aim.repository.memory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.permission.AimModulePermission;
import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.model.role.Role;
import mn.erin.domain.aim.model.role.RoleId;
import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.aim.repository.RoleRepository;
import mn.erin.domain.base.model.EntityId;
import org.springframework.stereotype.Repository;

import static mn.erin.domain.aim.constant.AimConstants.APPLICATION_ID;
import static mn.erin.domain.aim.constant.AimConstants.MODULE_ID;

/**
 * author Naranbaatar Avir.
 */
@Repository
public class MemoryRoleRepository implements RoleRepository
{
  private static final String GET_PROCESS_REQUESTS = "bpms.bpm.GetGroupProcessRequests";
  private static final String GET_PROCESS_REQUEST = "bpms.bpm.GetProcessRequest";
  private static final String GET_PROCESS_REQUEST_BY_CREATED_DATE = "bpms.bpm.GetRequestsByCreatedDate";


  private static final String CREATE_PROCESS_REQUEST = "bpms.bpm.CreateProcessRequest";

  private static final String GET_PROCESS_TYPES = "bpms.bpm.GetProcessTypes";
  private static final String GET_PROCESS_TYPE = "bpms.bpm.GetProcessType";

  private static final String START_PROCESS = "bpms.bpm.StartProcess";
  private static final String MANUAL_ACTIVATE = "bpms.bpm.ManualActivate";
  private static final String GET_PROCESS_REQUEST_BY_PROCESS_INSTANCE_ID = "bpms.bpm.GetProcessRequestByProcessInstanceId";
  private static final String UPDATE_PARAMETERS = "bpms.bpm.UpdateParameters";
  private static final String GET_ALL_PROCESS_REQUESTS = "bpms.bpm.GetAllProcessRequests";
  private static final String GET_PROCESS_REQUESTS_BY_ASSIGNED_USER_ID = "bpms.bpm.GetProcessRequestsByAssignedUserId";
  private static final String UPDATE_ASSIGNED_USER = "bpms.bpm.UpdateAssignedUser";

  private static final String USER_CREATE = APPLICATION_ID + "." + MODULE_ID + "." + "CreateUser";
  private static final String USER_GET = APPLICATION_ID + "." + MODULE_ID + "." + "GetUser";
  private static final String USER_GET_ALL = APPLICATION_ID + "." + MODULE_ID + "." + "GetAllUsers";
  private static final String USER_UPDATE = APPLICATION_ID + "." + MODULE_ID + "." + "UpdateUser";
  private static final String USER_DELETE = APPLICATION_ID + "." + MODULE_ID + "." + "DeleteUser";
  private static final String USER_ARCHIVE = APPLICATION_ID + "." + MODULE_ID + "." + "ArchiveUser";

  private static final String MEMBERSHIP_GET_ALL = APPLICATION_ID + "." + MODULE_ID + "." + "GetUserMemberships";

  private static List<Role> roles = new ArrayList<>();

  static
  {
    TenantId tenantId = TenantId.valueOf("xac");

    Role admin = new Role(RoleId.valueOf("-admin-"), tenantId, "Admin");
    Role manager = new Role(RoleId.valueOf("-role-"), tenantId, "Manager");

    Role developer = new Role(RoleId.valueOf("-developer-"), tenantId, "Developer");
    Role tester = new Role(RoleId.valueOf("-tester-"), tenantId, "Tester");

    Role loanSpecialist = new Role(RoleId.valueOf("-branchSpecialist-"), tenantId, "Branch Specialist");
    Role ebank = new Role(RoleId.valueOf("-ebank-"), tenantId, "Ebank role");

    Role userManager = new Role(RoleId.valueOf("-userManager-"), tenantId, "User Manager Role");

    admin.addPermission(Permission.valueOf("*", "*", "*"));

    developer.addPermission(new AimModulePermission("*"));
    developer.addPermission(Permission.valueOf(GET_PROCESS_REQUESTS));

    loanSpecialist.addPermission(Permission.valueOf(GET_PROCESS_REQUESTS));
    loanSpecialist.addPermission(Permission.valueOf(CREATE_PROCESS_REQUEST));
    loanSpecialist.addPermission(Permission.valueOf(GET_PROCESS_TYPES));
    loanSpecialist.addPermission(Permission.valueOf(START_PROCESS));
    loanSpecialist.addPermission(Permission.valueOf(GET_PROCESS_TYPE));
    loanSpecialist.addPermission(Permission.valueOf(MANUAL_ACTIVATE));
    loanSpecialist.addPermission(Permission.valueOf(GET_ALL_PROCESS_REQUESTS));
    loanSpecialist.addPermission(Permission.valueOf(GET_PROCESS_REQUESTS_BY_ASSIGNED_USER_ID));
    loanSpecialist.addPermission(Permission.valueOf(UPDATE_ASSIGNED_USER));
    loanSpecialist.addPermission(Permission.valueOf(GET_PROCESS_REQUEST_BY_PROCESS_INSTANCE_ID));
    loanSpecialist.addPermission(Permission.valueOf(UPDATE_PARAMETERS));

    ebank.addPermission(Permission.valueOf(CREATE_PROCESS_REQUEST));
    ebank.addPermission(Permission.valueOf(GET_PROCESS_REQUESTS));
    ebank.addPermission(Permission.valueOf(GET_PROCESS_REQUEST));
    ebank.addPermission(Permission.valueOf(GET_PROCESS_REQUEST_BY_CREATED_DATE));
    ebank.addPermission(Permission.valueOf(GET_ALL_PROCESS_REQUESTS));
    ebank.addPermission(Permission.valueOf(GET_PROCESS_REQUESTS_BY_ASSIGNED_USER_ID));
    ebank.addPermission(Permission.valueOf(UPDATE_ASSIGNED_USER));
    ebank.addPermission(Permission.valueOf(GET_PROCESS_REQUEST_BY_PROCESS_INSTANCE_ID));
    ebank.addPermission(Permission.valueOf(UPDATE_PARAMETERS));

    userManager.addPermission(Permission.valueOf(USER_CREATE));
    userManager.addPermission(Permission.valueOf(USER_GET));
    userManager.addPermission(Permission.valueOf(USER_GET_ALL));
    userManager.addPermission(Permission.valueOf(USER_UPDATE));
    userManager.addPermission(Permission.valueOf(USER_DELETE));
    userManager.addPermission(Permission.valueOf(USER_ARCHIVE));
    userManager.addPermission(Permission.valueOf(MEMBERSHIP_GET_ALL));

    roles.add(admin);
    roles.add(manager);
    roles.add(developer);
    roles.add(tester);
    roles.add(loanSpecialist);
    roles.add(ebank);
    roles.add(userManager);
  }

  @Override
  public Role create(TenantId tenantId, String id, String name, Collection<Permission> permissions)
  {
    Role role = new Role(RoleId.valueOf(id), tenantId, name);
    for (Permission permission : permissions)
    {
      role.addPermission(permission);
    }

    roles.add(role);

    return role;
  }

  @Override
  public Role create(TenantId tenantId, String id, String name) throws AimRepositoryException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<Role> listAll(TenantId tenantId)
  {
    List<Role> roleList = new ArrayList<>();
    for (Role role : roles)
    {
      if (role.getTenantId().sameValueAs(tenantId))
      {
        roleList.add(role);
      }
    }
    return roleList;
  }

  @Override
  public boolean doesExistById(RoleId roleId)
  {
    for (Role role : roles)
    {
      if (role.getRoleId().equals(roleId))
      {
        return true;
      }
    }
    return false;
  }

  @Override
  public Role findById(EntityId entityId)
  {
    for (Role role : findAll())
    {
      if (role.getRoleId().sameValueAs(entityId))
      {
        return role;
      }
    }
    return null;
  }

  @Override
  public Collection<Role> findAll()
  {
    return Collections.unmodifiableList(roles);
  }
}

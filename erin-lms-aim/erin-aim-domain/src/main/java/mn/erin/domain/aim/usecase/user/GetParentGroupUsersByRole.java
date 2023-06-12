package mn.erin.domain.aim.usecase.user;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.group.Group;
import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.domain.aim.model.membership.Membership;
import mn.erin.domain.aim.model.permission.AimModulePermission;
import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.aim.model.user.User;
import mn.erin.domain.aim.model.user.UserId;
import mn.erin.domain.aim.model.user.UserIdentity;
import mn.erin.domain.aim.repository.AimRepositoryRegistry;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.aim.service.TenantIdProvider;
import mn.erin.domain.aim.usecase.AuthorizedUseCase;
import mn.erin.domain.aim.usecase.group.GetUsersByRole;
import mn.erin.domain.aim.usecase.group.GetUsersByRoleInput;
import mn.erin.domain.aim.usecase.group.GetUsersByRoleOutput;
import mn.erin.domain.base.usecase.UseCaseException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Zorig
 */
public class GetParentGroupUsersByRole extends AuthorizedUseCase<GetParentGroupUsersByRoleInput, GetParentGroupUsersByRoleOutput>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(GetParentGroupUsersByRole.class);
  private static final Permission permission = new AimModulePermission("GetParentGroupUsersByRole");

  private final AimRepositoryRegistry aimRepositoryRegistry;
  private final TenantIdProvider tenantIdProvider;

  public GetParentGroupUsersByRole(AuthenticationService authenticationService, AuthorizationService authorizationService,
      AimRepositoryRegistry aimRepositoryRegistry, TenantIdProvider tenantIdProvider)
  {
    super(authenticationService, authorizationService);
    this.aimRepositoryRegistry = aimRepositoryRegistry;
    this.tenantIdProvider = tenantIdProvider;
  }

  @Override
  public Permission getPermission()
  {
    return permission;
  }

  @Override
  protected GetParentGroupUsersByRoleOutput executeImpl(GetParentGroupUsersByRoleInput input) throws UseCaseException
  {
    //validations
    //null checks/handling
    //testing, edge cases

    if (input == null || StringUtils.isBlank(input.getRole()))
    {
      String errorMessage = "BPMS021";
      throw new UseCaseException(errorMessage, "Invalid Role Input!");
    }

    GetUsersByRole getUsersByRole = new GetUsersByRole(authenticationService, authorizationService, aimRepositoryRegistry, tenantIdProvider);
    GetUsersByRoleInput getUsersByRoleInput = new GetUsersByRoleInput(input.getRole());
    GetUsersByRoleOutput getUsersByRoleOutput = getUsersByRole.execute(getUsersByRoleInput);

    Collection<User> allUsers = getUsersByRoleOutput.getAllUsers();

    Collection<User> allUsersByRole = getUsersByRoleOutput.getUsersByRole();
    List<User> allParentGroupUsers = getParentGroupUsers(allUsers);

    List<User> allParentGroupUsersByRole = new ArrayList<>();

    for (User userByRole : allUsersByRole)
    {
      for (User parentGroupUser : allParentGroupUsers)
      {
        if (userByRole.getUserId().getId().equals(parentGroupUser.getUserId().getId()))
        {
          allParentGroupUsersByRole.add(userByRole);
        }
      }
    }
    return new GetParentGroupUsersByRoleOutput(allParentGroupUsersByRole);
  }

  private List<User> getParentGroupUsers(Collection<User> allUsers) throws UseCaseException
  {
    try
    {
      String tenantId = tenantIdProvider.getCurrentUserTenantId();
      String username = authenticationService.getCurrentUsername();

      List<Membership> memberships = aimRepositoryRegistry.getMembershipRepository().listAllByUsername(TenantId.valueOf(tenantId), username);

      if (memberships.isEmpty())
      {
        return Collections.emptyList();
      }

      GroupId groupId = memberships.get(0).getGroupId();

      List<String> parentGroupIds = collectParentGroupIds(groupId);
      List<Membership> allMembershipsByGroup = new ArrayList<>();
      for (String parentGroupId : parentGroupIds)
      {
        List<Membership> returnedMembershipsByGroup = aimRepositoryRegistry.getMembershipRepository()
            .listAllByGroupId(TenantId.valueOf(tenantId), GroupId.valueOf(parentGroupId));
        allMembershipsByGroup.addAll(returnedMembershipsByGroup);
      }

      List<User> allSubGroupUsers = new ArrayList<>();

      for (Membership membership : allMembershipsByGroup)
      {
        UserIdentity identity = aimRepositoryRegistry.getUserIdentityRepository().getUserIdentityByUsername(membership.getUsername());
        if(identity != null){
          User subGroupUser = findUserById(allUsers, identity.getUserId());
          if (subGroupUser != null)
          {
            allSubGroupUsers.add(subGroupUser);
          }
        }
        else{
          LOGGER.debug("User identity not found: username [" + membership.getUsername() + "]");
        }
      }

      return allSubGroupUsers;
    }
    catch (AimRepositoryException e)
    {
      throw new UseCaseException(e.getMessage());
    }
  }

  private User findUserById(Collection<User> allUsers, UserId userId)
  {
    for (User user : allUsers)
    {
      if (user.getUserId().equals(userId))
      {
        return user;
      }
    }

    return null;
  }

  private List<String> collectParentGroupIds(GroupId groupId)
  {
    List<String> groupIds = new ArrayList<>();

    Queue<GroupId> groupsToTraverse = new LinkedList<>();
    groupsToTraverse.add(groupId);

    while (!groupsToTraverse.isEmpty())
    {
      GroupId currentGroupId = groupsToTraverse.peek();
      Group currentGroup = aimRepositoryRegistry.getGroupRepository().findById(currentGroupId);

      if (currentGroup.getParent() != null)
      {
        groupsToTraverse.add(currentGroup.getParent());
      }

      groupIds.add(currentGroupId.getId());
      groupsToTraverse.remove(currentGroupId);
    }
    return groupIds;
  }
}

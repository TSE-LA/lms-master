package mn.erin.domain.aim.usecase.user;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.domain.aim.model.group.GroupTree;
import mn.erin.domain.aim.model.membership.Membership;
import mn.erin.domain.aim.model.permission.AimModulePermission;
import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.aim.model.user.User;
import mn.erin.domain.aim.model.user.UserIdentity;
import mn.erin.domain.aim.repository.AimRepositoryRegistry;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.aim.service.TenantIdProvider;
import mn.erin.domain.aim.usecase.AuthorizedUseCase;
import mn.erin.domain.aim.usecase.group.GetGroupSubTree;
import mn.erin.domain.aim.usecase.group.GetGroupSubTreeInput;
import mn.erin.domain.aim.usecase.group.GetGroupSubTreeOutput;
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
public class GetSubGroupUsersByRole extends AuthorizedUseCase<GetSubGroupUsersByRoleInput, GetSubGroupUsersByRoleOutput>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(GetSubGroupUsersByRole.class);
  private static final Permission permission = new AimModulePermission("GetSubGroupUsersByRole");
  private final AimRepositoryRegistry aimRepositoryRegistry;
  private final TenantIdProvider tenantIdProvider;

  public GetSubGroupUsersByRole(AuthenticationService authenticationService, AuthorizationService authorizationService,
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
  protected GetSubGroupUsersByRoleOutput executeImpl(GetSubGroupUsersByRoleInput input) throws UseCaseException
  {
    //validations
    //null checks/handling
    //testing, edge cases

    if (input == null || StringUtils.isBlank(input.getRole()))
    {
      String errorCode = "BPMS021";
      throw new UseCaseException(errorCode, "Invalid Role Input!");
    }

    GetUsersByRole getUsersByRole = new GetUsersByRole(authenticationService, authorizationService, aimRepositoryRegistry, tenantIdProvider);
    GetUsersByRoleInput getUsersByRoleInput = new GetUsersByRoleInput(input.getRole());
    GetUsersByRoleOutput getUsersByRoleOutput = getUsersByRole.execute(getUsersByRoleInput);

    Collection<User> allUsersByRole = getUsersByRoleOutput.getUsersByRole();
    List<User> allSubGroupUsers = getSubGroupUsers();

    List<User> allSubGroupUsersByRole = new ArrayList<>();

    //TODO: fix me, make more efficient (similar to GetGroupUsersByRole use case)
    for (User userByRole : allUsersByRole)
    {
      for (User subGroupUser : allSubGroupUsers)
      {
        if (userByRole.getUserId().getId().equals(subGroupUser.getUserId().getId()))
        {
          allSubGroupUsersByRole.add(userByRole);
        }
      }
    }

    return new GetSubGroupUsersByRoleOutput(allSubGroupUsersByRole);
  }

  private List<User> getSubGroupUsers() throws UseCaseException
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
      GetGroupSubTree getGroupSubTree = new GetGroupSubTree(authenticationService, authorizationService, aimRepositoryRegistry.getGroupRepository());
      GetGroupSubTreeOutput getGroupSubTreeOutput = getGroupSubTree.execute(new GetGroupSubTreeInput(groupId.getId()));
      GroupTree groupTree = getGroupSubTreeOutput.getGroupTree();

      List<String> subGroupIds = collectSubGroupIds(groupTree);
      List<Membership> allMembershipsByGroup = new ArrayList<>();
      for (String subGroupId : subGroupIds)
      {
        List<Membership> returnedMembershipsByGroup = aimRepositoryRegistry.getMembershipRepository()
            .listAllByGroupId(TenantId.valueOf(tenantId), GroupId.valueOf(subGroupId));
        allMembershipsByGroup.addAll(returnedMembershipsByGroup);
      }

      List<User> allSubGroupUsers = new ArrayList<>();

      for (Membership membership : allMembershipsByGroup)
      {
        String memberUsername = membership.getUsername();
        UserIdentity identity = aimRepositoryRegistry.getUserIdentityRepository().getUserIdentityByUsername(memberUsername);
        if (identity != null)
        {
          User subGroupUser = aimRepositoryRegistry.getUserRepository().findById(identity.getUserId());
          if (subGroupUser != null)
          {
            allSubGroupUsers.add(subGroupUser);
          }
        }
        else
        {
          LOGGER.debug("User identity not found: username [" + memberUsername + "]");
        }
      }

      return allSubGroupUsers;
    }
    catch (AimRepositoryException e)
    {
      throw new UseCaseException(e.getMessage());
    }
  }

  private List<String> collectSubGroupIds(GroupTree groupTree)
  {
    List<String> groupIds = new ArrayList<>();

    Queue<GroupTree> groupsToTraverse = new LinkedList<>();

    groupsToTraverse.add(groupTree);

    while (!groupsToTraverse.isEmpty())
    {
      GroupTree currentGroupTree = groupsToTraverse.peek();

      List<GroupTree> children = currentGroupTree.getChildren();
      Iterator<GroupTree> childrenIterator = children.iterator();

      while (childrenIterator.hasNext())
      {
        groupsToTraverse.add(childrenIterator.next());
      }

      groupIds.add(currentGroupTree.getId());
      groupsToTraverse.remove(currentGroupTree);
    }

    return groupIds;
  }
}

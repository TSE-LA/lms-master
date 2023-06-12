package mn.erin.lms.base.domain.usecase.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.aim.model.group.Group;
import mn.erin.domain.aim.model.membership.Membership;
import mn.erin.domain.aim.model.user.UserAggregate;
import mn.erin.domain.aim.model.user.UserStatus;
import mn.erin.domain.aim.repository.AimRepositoryRegistry;
import mn.erin.domain.aim.usecase.user.GetAllUsers;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.AccessIdentityManagement;
import mn.erin.lms.base.aim.organization.DepartmentPath;
import mn.erin.lms.base.aim.user.LmsUser;
import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.LmsUseCase;
import mn.erin.lms.base.domain.usecase.user.dto.GetUserByRolesInput;
import mn.erin.lms.base.domain.usecase.user.dto.GetUserByRolesOutput;
import mn.erin.lms.base.domain.util.DepartmentPathUtil;

/**
 * @author Oyungerel Chuluunsukh.
 */
@Authorized(users = { LmsUser.class })
public class GetUserByRoles extends LmsUseCase<GetUserByRolesInput, List<GetUserByRolesOutput>>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(GetUserByRoles.class);
  private final AimRepositoryRegistry aimRepositoryRegistry;
  private final AccessIdentityManagement accessIdentityManagement;

  public GetUserByRoles(LmsRepositoryRegistry lmsRepositoryRegistry,
      LmsServiceRegistry lmsServiceRegistry, AimRepositoryRegistry aimRepositoryRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
    this.aimRepositoryRegistry = aimRepositoryRegistry;
    this.accessIdentityManagement = lmsServiceRegistry.getAccessIdentityManagement();
  }

  @Override
  protected List<GetUserByRolesOutput> executeImpl(GetUserByRolesInput input) throws UseCaseException
  {
    GetAllUsers getAllUsers = new GetAllUsers(lmsServiceRegistry.getAuthenticationService(), lmsServiceRegistry.getAuthorizationService(),
        aimRepositoryRegistry.getUserAggregateService());
    String currentUsername = accessIdentityManagement.getCurrentUsername();

    List<UserAggregate> allUsers;

    try
    {
      allUsers = getAllUsers.execute(null).getAllUserAggregates();
      if (!input.isCurrentUserIncluded())
      {
        allUsers = allUsers.stream()
            .filter(user -> !(currentUsername.equals(user.getUsername())) && user.getStatus().equals(UserStatus.ACTIVE))
            .collect(Collectors.toList());
      }
      else
      {
        allUsers = allUsers.stream()
            .filter(user -> user.getStatus().equals(UserStatus.ACTIVE)).collect(Collectors.toList());
      }
    }
    catch (UseCaseException e)
    {
      LOGGER.debug(e.getMessage(), e);
      throw new UseCaseException(e.getMessage(), e);
    }

    String currentDepartment = accessIdentityManagement.getCurrentUserDepartmentId();
    Set<String> subGroupIds = lmsServiceRegistry.getDepartmentService().getSubDepartments(currentDepartment);
    Set<Group> subGroups = aimRepositoryRegistry.getGroupRepository().getAllByIds(subGroupIds);

    Map<String, DepartmentPath> groupIdPathMap = DepartmentPathUtil.getPath(currentDepartment, subGroups);

    List<String> roles = input.getRoles();
    List<GetUserByRolesOutput> result = new ArrayList<>();
    Map<String, Membership> memberships = aimRepositoryRegistry.getMembershipRepository().getAllForUsers(getUsernames(allUsers));
    for (UserAggregate user : allUsers)
    {
      Membership membership = memberships.get(user.getUsername());
      if (membership == null)
      {
        continue;
      }
      if (subGroupIds.contains(membership.getGroupId().getId()) && roles.contains(membership.getRoleId().getId()))
      {
        GetUserByRolesOutput output = new GetUserByRolesOutput(user);
        output.setMembership(membership);
        output.setGroupPath(groupIdPathMap.get(membership.getGroupId().getId()).getPath());
        result.add(output);
      }
    }
    return result;
  }

  private static List<String> getUsernames(List<UserAggregate> allUsers)
  {
    return allUsers.stream().map(UserAggregate::getUsername).collect(Collectors.toList());
  }
}

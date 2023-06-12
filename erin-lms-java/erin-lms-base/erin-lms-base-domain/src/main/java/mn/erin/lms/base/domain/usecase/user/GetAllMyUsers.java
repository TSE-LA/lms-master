package mn.erin.lms.base.domain.usecase.user;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.aim.model.membership.Membership;
import mn.erin.domain.aim.model.user.UserAggregate;
import mn.erin.domain.aim.model.user.UserStatus;
import mn.erin.domain.aim.repository.AimRepositoryRegistry;
import mn.erin.domain.aim.usecase.user.GetAllUsers;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.role.LmsRole;
import mn.erin.lms.base.aim.user.Learner;
import mn.erin.lms.base.aim.user.LmsUser;
import mn.erin.lms.base.aim.user.Manager;
import mn.erin.lms.base.aim.user.Supervisor;
import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.LmsUseCase;
import mn.erin.lms.base.domain.usecase.user.dto.GetUserByRolesOutput;

/**
 * @author Oyungerel Chuluunsukh.
 */
@Authorized(users = { LmsUser.class })
public class GetAllMyUsers extends LmsUseCase<Boolean, List<GetUserByRolesOutput>>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(GetUserByRoles.class);
  private final AimRepositoryRegistry aimRepositoryRegistry;

  public GetAllMyUsers(LmsRepositoryRegistry lmsRepositoryRegistry,
      LmsServiceRegistry lmsServiceRegistry, AimRepositoryRegistry aimRepositoryRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
    this.aimRepositoryRegistry = aimRepositoryRegistry;
  }

  @Override
  protected List<GetUserByRolesOutput> executeImpl(Boolean includeMe) throws UseCaseException
  {
    GetAllUsers getAllUsers = new GetAllUsers(lmsServiceRegistry.getAuthenticationService(),
        lmsServiceRegistry.getAuthorizationService(), aimRepositoryRegistry.getUserAggregateService());
    String currentUsername = lmsServiceRegistry.getAccessIdentityManagement().getCurrentUsername();

    List<UserAggregate> allUsers;

    try
    {
      allUsers = getAllUsers.execute(null).getAllUserAggregates();
      if (!includeMe)
      {
        allUsers = allUsers.stream()
            .filter(user -> !(currentUsername.equals(user.getUsername())) && user.getStatus().equals(UserStatus.ACTIVE))
            .collect(Collectors.toList());
      }
      else
      {
        allUsers = allUsers.stream().filter(user -> user.getStatus().equals(UserStatus.ACTIVE)).collect(Collectors.toList());
      }
    }
    catch (UseCaseException e)
    {
      LOGGER.debug(e.getMessage(), e);
      throw new UseCaseException(e.getMessage());
    }

    Collection<String> roles = getRoles();
    List<GetUserByRolesOutput> result = new ArrayList<>();
    Map<String, Membership> memberships = aimRepositoryRegistry.getMembershipRepository().getAllForUsers(getUsernames(allUsers));
    for (UserAggregate user : allUsers)
    {
      GetUserByRolesOutput output = new GetUserByRolesOutput(user);
      Membership membership = memberships.get(user.getUsername());
      if (membership == null)
      {
        result.add(output);
      }
      else if (roles.isEmpty() || roles.contains(membership.getRoleId().getId()))
      {
        output.setMembership(membership);
        result.add(output);
      }
    }
    return result;
  }

  private Collection<String> getRoles()
  {
    List<String> roles = new ArrayList<>();
    LmsUser currentUser = lmsServiceRegistry.getLmsUserService().getCurrentUser();

    if (currentUser instanceof Manager)
    {
      roles.add(LmsRole.LMS_MANAGER.name());
      roles.add(LmsRole.LMS_SUPERVISOR.name());
      roles.add(LmsRole.LMS_USER.name());
    }

    else if (currentUser instanceof Supervisor)
    {
      roles.add(LmsRole.LMS_SUPERVISOR.name());
      roles.add(LmsRole.LMS_USER.name());
    }

    else if (currentUser instanceof Learner)
    {
      roles.add(LmsRole.LMS_USER.name());
    }

    return roles;
  }

  private static List<String> getUsernames(List<UserAggregate> allUsers)
  {
    return allUsers.stream().map(UserAggregate::getUsername).collect(Collectors.toList());
  }
}

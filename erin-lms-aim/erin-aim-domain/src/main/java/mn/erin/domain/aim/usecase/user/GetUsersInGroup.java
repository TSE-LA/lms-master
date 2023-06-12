package mn.erin.domain.aim.usecase.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.group.GroupId;
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
import mn.erin.domain.base.usecase.UseCaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Zorig
 */
public class GetUsersInGroup extends AuthorizedUseCase<Void, GetUsersInGroupOutput>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(GetUsersInGroup.class);
  private final AimRepositoryRegistry registry;
  private final TenantIdProvider tenantIdProvider;

  public GetUsersInGroup(AuthenticationService authenticationService, AuthorizationService authorizationService,
    AimRepositoryRegistry registry, TenantIdProvider tenantIdProvider)
  {
    super(authenticationService, authorizationService);
    this.registry = registry;
    this.tenantIdProvider = Objects.requireNonNull(tenantIdProvider, "MembershipRepository cannot be null!");
  }

  private static final Permission permission = new AimModulePermission("GetUsersInGroup");

  @Override
  public Permission getPermission()
  {
    return permission;
  }

  @Override
  protected GetUsersInGroupOutput executeImpl(Void input) throws UseCaseException
  {
    try
    {
      String username = authenticationService.getCurrentUsername();
      String tenantId = tenantIdProvider.getCurrentUserTenantId();

      List<Membership> memberships =  registry.getMembershipRepository().listAllByUsername(TenantId.valueOf(tenantId), username);

      //check if empty and then handle it
      if (memberships.isEmpty())
      {
        throw new UseCaseException("Current user does not have any memberships!");
      }

      Membership membership = memberships.get(0);
      GroupId groupId = membership.getGroupId();

      List<Membership> membershipsFilteredByGroup = registry.getMembershipRepository().listAllByGroupId(TenantId.valueOf(tenantId), groupId);
      List<User> usersToReturn = new ArrayList<>();
      for (Membership iterationMembership : membershipsFilteredByGroup)
      {
        UserIdentity identity = registry.getUserIdentityRepository().getUserIdentityByUsername(iterationMembership.getUsername());
        if(identity!=null){
          User user = registry.getUserRepository().findById(identity.getUserId());
          if (user != null)
          {
            usersToReturn.add(user);
          }
        }
        else {
          LOGGER.debug("User identity not found: username [" + iterationMembership.getUsername() + "]");
        }
      }

      return new GetUsersInGroupOutput(usersToReturn);
    }
    catch (AimRepositoryException e)
    {
      throw new UseCaseException(e.getMessage());
    }
  }
}

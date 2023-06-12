package mn.erin.domain.aim.usecase.group;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.membership.Membership;
import mn.erin.domain.aim.model.permission.AimModulePermission;
import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.model.role.RoleId;
import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.aim.model.user.User;
import mn.erin.domain.aim.model.user.UserId;
import mn.erin.domain.aim.model.user.UserIdentity;
import mn.erin.domain.aim.repository.AimRepositoryRegistry;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.aim.service.TenantIdProvider;
import mn.erin.domain.aim.usecase.AuthorizedUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Zorig
 */
public class GetUsersByRole extends AuthorizedUseCase<GetUsersByRoleInput, GetUsersByRoleOutput>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(GetUsersByRole.class);
  private static final Permission permission = new AimModulePermission("GetUsersByRole");

  private final AimRepositoryRegistry aimRepositoryRegistry;
  private final TenantIdProvider tenantIdProvider;

  public GetUsersByRole(AuthenticationService authenticationService, AuthorizationService authorizationService,
      AimRepositoryRegistry aimRepositoryRegistry, TenantIdProvider tenantIdProvider)
  {
    super(authenticationService, authorizationService);
    this.aimRepositoryRegistry = Objects.requireNonNull(aimRepositoryRegistry, "AimRepositoryRegistry cannot be null!");
    this.tenantIdProvider = Objects.requireNonNull(tenantIdProvider, "TenantIdProvider cannot be null!");
  }

  @Override
  public Permission getPermission()
  {
    return permission;
  }

  @Override
  protected GetUsersByRoleOutput executeImpl(GetUsersByRoleInput input) throws UseCaseException
  {
    if (StringUtils.isBlank(input.getRoleId()))
    {
      String errorCode = "BPMS005";
      throw new UseCaseException(errorCode, "Invalid Inputs!");
    }

    try
    {
      List<User> usersByRole = new ArrayList<>();

      Collection<Membership> memberships = aimRepositoryRegistry.getMembershipRepository().listAllByRole(RoleId.valueOf(input.getRoleId()));
      Collection<User> allUsers = aimRepositoryRegistry.getUserRepository().getAllUsers(TenantId.valueOf(tenantIdProvider.getCurrentUserTenantId()));

      for (Membership membership : memberships)
      {
        UserIdentity identity = aimRepositoryRegistry.getUserIdentityRepository().getUserIdentityByUsername(membership.getUsername());
        if (identity != null)
        {
          User currentUserToAdd = findUserById(allUsers, identity.getUserId());
          if (currentUserToAdd != null)
          {
            usersByRole.add(currentUserToAdd);
          }
        }
        else {
            LOGGER.debug("User identity not found: username [" + membership.getUsername() + "]");
        }
      }

      return new GetUsersByRoleOutput(usersByRole, allUsers);
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
}

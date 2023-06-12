package mn.erin.domain.aim.usecase.authentication;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.membership.Membership;
import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.model.role.Role;
import mn.erin.domain.aim.model.role.RoleId;
import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.aim.model.user.User;
import mn.erin.domain.aim.model.user.UserIdentity;
import mn.erin.domain.aim.repository.AimRepositoryRegistry;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;

import static mn.erin.domain.aim.constant.AimErrorMessageConstant.REPOSITORY_REGISTRY_CANNOT_BE_NULL;

/**
 * @author Oyungerel Chuluunsukh.
 */
public class ValidateSession extends AbstractUseCase<Void, LoginUserOutput>
{
  private final AuthenticationService authenticationService;
  private final AimRepositoryRegistry aimRepositoryRegistry;

  public ValidateSession(AuthenticationService authenticationService, AimRepositoryRegistry aimRepositoryRegistry)
  {
    this.authenticationService = Objects.requireNonNull(authenticationService, "AuthenticationService is required!");
    this.aimRepositoryRegistry = Objects.requireNonNull(aimRepositoryRegistry, REPOSITORY_REGISTRY_CANNOT_BE_NULL);
  }

  @Override
  public LoginUserOutput execute(Void input) throws UseCaseException
  {
    if (!authenticationService.isCurrentUserAuthenticated())
    {
      throw new UseCaseException("The current user is not authenticated!");
    }

    // 1. get current user
    String token = authenticationService.getToken();
    String username = authenticationService.getCurrentUsername();

    UserIdentity userIdentity = aimRepositoryRegistry.getUserIdentityRepository().getUserIdentityByUsername(username);

    if (null == userIdentity)
    {
      throw new UseCaseException("The user ID is null!");
    }

    User user = aimRepositoryRegistry.getUserRepository().findById(userIdentity.getUserId());

    if (null == user)
    {
      throw new UseCaseException("User not found with user id = " + userIdentity.getUserId());
    }

    TenantId tenantId = user.getTenantId();

    // 2. check membership access, exclude 'admin' user
    Membership membership = getMembership(tenantId.getId(), username);
    Collection<Permission> permissions = getPermissions(membership);

    return new LoginUserOutput(token, membership.getGroupId().getId(), permissions, username, membership.getRoleId().getId());
  }

  private Membership getMembership(String tenantId, String username) throws UseCaseException
  {
    try
    {
      List<Membership> memberships = aimRepositoryRegistry.getMembershipRepository().listAllByUsername(TenantId.valueOf(tenantId), username);
      if (memberships.isEmpty())
      {
        throw new UseCaseException("User doesn't have any memberships!");
      }
      return memberships.get(0);
    }
    catch (AimRepositoryException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }
  }

  private Collection<Permission> getPermissions(Membership membership)
  {
    RoleId roleId = membership.getRoleId();
    Role role = aimRepositoryRegistry.getRoleRepository().findById(roleId);
    return role.getPermissions();
  }
}

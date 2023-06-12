/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.aim.usecase.authentication;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.membership.Membership;
import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.model.role.Role;
import mn.erin.domain.aim.model.role.RoleId;
import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.aim.model.user.UserIdentity;
import mn.erin.domain.aim.model.user.UserIdentitySource;
import mn.erin.domain.aim.model.user.UserProfile;
import mn.erin.domain.aim.repository.AimRepositoryRegistry;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;

import static mn.erin.domain.aim.constant.AimErrorMessageConstant.AUTHENTICATION_FAILED_USER_DATA_INVALID;
import static mn.erin.domain.aim.constant.AimErrorMessageConstant.REPOSITORY_REGISTRY_CANNOT_BE_NULL;

/**
 * @author EBazarragchaa
 */
public class LoginUser extends AbstractUseCase<LoginUserInput, LoginUserOutput>
{
  private final AuthenticationService authenticationService;
  private final AimRepositoryRegistry aimRepositoryRegistry;

  public LoginUser(AuthenticationService authenticationService, AimRepositoryRegistry aimRepositoryRegistry)
  {
    this.authenticationService = Objects.requireNonNull(authenticationService, "AuthenticationService is required!");
    this.aimRepositoryRegistry = Objects.requireNonNull(aimRepositoryRegistry, REPOSITORY_REGISTRY_CANNOT_BE_NULL);
  }

  @Override
  public LoginUserOutput execute(LoginUserInput input) throws UseCaseException
  {
    if (null == input)
    {
      throw new UseCaseException("Login input data is required!");
    }

    String username = input.getUsername();
    String token = null;
    UserIdentity userIdentity = null;
    try
    {
      // 1. authenticate
      UserProfile userProfile = aimRepositoryRegistry.getUserProfileRepository().findByPhoneNumber(username);
      if (userProfile != null)
      {
        userIdentity = aimRepositoryRegistry.getUserIdentityRepository().getIdentity(userProfile.getUserId(), UserIdentitySource.OWN);
        username = userIdentity.getUsername();
        token = authenticationService.authenticate(input.getTenantId(), username, input.getPassword());
      }
      else
      {
        token = authenticationService.authenticate(input.getTenantId(), username, input.getPassword());
      }

      if (null == token)
      {
        throw new UseCaseException(AUTHENTICATION_FAILED_USER_DATA_INVALID);
      }
    }
    catch (Exception e)
    {
      throw new UseCaseException(AUTHENTICATION_FAILED_USER_DATA_INVALID);
    }

    String tenantId = input.getTenantId();
    username = authenticationService.getCurrentUsername();

    // 2. check membership access, exclude 'admin' user
    Membership membership = getMembership(tenantId, username);
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

  private List<String> getGroups(List<Membership> memberships)
  {
    List<String> groups = new ArrayList<>();

    for (Membership membership : memberships)
    {
      String groupId = membership.getGroupId().getId();

      if (!groups.contains(groupId))
      {
        groups.add(groupId);
      }
    }
    return groups;
  }
}

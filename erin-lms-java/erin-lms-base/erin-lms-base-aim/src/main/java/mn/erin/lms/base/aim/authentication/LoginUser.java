/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.base.aim.authentication;

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
import mn.erin.domain.aim.usecase.authentication.LoginUserInput;
import mn.erin.domain.aim.usecase.authentication.LoginUserOutput;
import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;

import static mn.erin.domain.aim.constant.AimErrorMessageConstant.AUTHENTICATION_FAILED_USER_DATA_INVALID;
import static mn.erin.domain.aim.constant.AimErrorMessageConstant.REPOSITORY_REGISTRY_CANNOT_BE_NULL;

/**
 * @author EBazarragchaa
 */
public class LoginUser extends AbstractUseCase<LoginUserInput, LoginUserOutput>
{
  private static final String USERNAME_NOT_FOUND = "Username not found!";

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

    // 1. Check existence with phone number
    UserProfile userProfile = aimRepositoryRegistry.getUserProfileRepository().findByPhoneNumber(username);
    UserIdentity userIdentity;
    if (userProfile != null)
    {
      userIdentity = aimRepositoryRegistry.getUserIdentityRepository().getIdentity(userProfile.getUserId(), UserIdentitySource.OWN);
      username = userIdentity.getUsername();
    }

    // 2. Check membership access, exclude 'admin' user
    String tenantId = input.getTenantId();
    Membership membership = getMembership(tenantId, username);
    Collection<Permission> permissions = getPermissions(membership);

    // 3. Authenticate and create session
    String token;
    try
    {
      token = authenticationService.authenticate(input.getTenantId(), username, input.getPassword());
      if (null == token)
      {
        throw new UseCaseException(AUTHENTICATION_FAILED_USER_DATA_INVALID);
      }
    }
    catch (Exception e)
    {
      throw new UseCaseException(e.getMessage());
    }

    // 4. return username in case logged in by phone number
    String loggedInUsername = authenticationService.getCurrentUsername();
    return new LoginUserOutput(token, membership.getGroupId().getId(), permissions, loggedInUsername, membership.getRoleId().getId());
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

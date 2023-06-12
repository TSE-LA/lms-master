/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.aim.rest.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import mn.erin.aim.rest.model.RestLoginBody;
import mn.erin.aim.rest.model.RestLoginResult;
import mn.erin.aim.rest.model.RestPermission;
import mn.erin.domain.aim.constant.AimErrorMessageConstant;
import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.repository.AimRepositoryRegistry;
import mn.erin.domain.aim.repository.MembershipRepository;
import mn.erin.domain.aim.repository.RoleRepository;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.aim.service.PermissionService;
import mn.erin.domain.aim.service.TenantIdProvider;
import mn.erin.domain.aim.usecase.authentication.LoginUser;
import mn.erin.domain.aim.usecase.authentication.LoginUserInput;
import mn.erin.domain.aim.usecase.authentication.LoginUserOutput;
import mn.erin.domain.aim.usecase.authentication.LogoutUser;
import mn.erin.domain.aim.usecase.authentication.ValidateSession;
import mn.erin.domain.aim.usecase.permission.GetAllPermissions;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.infrastucture.rest.common.response.RestResponse;
import mn.erin.infrastucture.rest.common.response.RestResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author EBazarragchaa
 */

@Api("AIM")
@RequestMapping(value = "/aim", name = "Provides access and identity management features")
@RestController
public class AimRestApi extends BaseAimRestApi
{
  private final MembershipRepository membershipRepository;
  private final RoleRepository roleRepository;
  private final AimRepositoryRegistry aimRepositoryRegistry;

  @Inject
  public AimRestApi(PermissionService permissionService, AuthenticationService authenticationService, AuthorizationService authorizationService,
      MembershipRepository membershipRepository, RoleRepository roleRepository, TenantIdProvider tenantIdProvider,
      AimRepositoryRegistry aimRepositoryRegistry)
  {
    this.permissionService = permissionService;
    this.authenticationService = authenticationService;
    this.authorizationService = authorizationService;
    this.membershipRepository = membershipRepository;
    this.roleRepository = roleRepository;
    this.tenantIdProvider = tenantIdProvider;
    this.aimRepositoryRegistry = aimRepositoryRegistry;
  }

  @ApiOperation("Login user")
  @PostMapping(value = "/login")
  public ResponseEntity<RestResult> login(@RequestBody RestLoginBody restLoginBody)
  {
    if (null == restLoginBody)
    {
      return RestResponse.badRequest("Login rest body cannot be null!");
    }

    String tenantId = restLoginBody.getTenantId();
    String username = restLoginBody.getUsername();
    String password = restLoginBody.getPassword();

    if (StringUtils.isBlank(tenantId))
    {
      return RestResponse.badRequest("Tenant id cannot be blank!");
    }
    if (StringUtils.isBlank(username))
    {
      return RestResponse.badRequest("User id cannot be blank!");
    }

    LoginUserInput input = new LoginUserInput(tenantId, username, password);
    LoginUser loginUser = new LoginUser(authenticationService, aimRepositoryRegistry);
    try
    {
      LoginUserOutput output = loginUser.execute(input);
      RestLoginResult result = getRestLoginResult(restLoginBody.getUsername(), output);

      return RestResponse.success(result);
    }
    catch (UseCaseException e)
    {
      if (e.getMessage().equalsIgnoreCase(AimErrorMessageConstant.AUTHENTICATION_FAILED_USER_DATA_INVALID))
      {
        return RestResponse.unauthorized(e.getMessage());
      }
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Logout user")
  @GetMapping(value = "/logout")
  public ResponseEntity<RestResult> logout()
  {
    LogoutUser logoutUser = new LogoutUser(authenticationService);
    try
    {
      String userId = logoutUser.execute(null);
      return RestResponse.success(userId);
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Get all permissions")
  @GetMapping(value = "/permissions")
  public ResponseEntity<RestResult> getPermissions()
  {
    GetAllPermissions getAllPermissions = new GetAllPermissions(authenticationService, authorizationService, permissionService);

    try
    {
      Collection<Permission> permissions = getAllPermissions.execute(null);
      return RestResponse.success(permissions);
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  private Set<RestPermission> getRestPermission(List<String> permissions)
  {
    Set<RestPermission> restPermissions = new HashSet<>();

    for (String permission : permissions)
    {
      restPermissions.add(new RestPermission(permission));
    }
    return restPermissions;
  }

  @ApiOperation("Validates user session")
  @GetMapping(value = "/validate-session")
  public ResponseEntity<RestResult> validate()
  {
    // TODO FIX me
    if (!authenticationService.isCurrentUserAuthenticated())
    {
      return RestResponse.unauthorized("The current user is not authenticated!");
    }
    ValidateSession validateSession = new ValidateSession(authenticationService, aimRepositoryRegistry);
    try
    {
      LoginUserOutput output = validateSession.execute(null);
      RestLoginResult result = getRestLoginResult(authenticationService.getCurrentUsername(), output);

      return RestResponse.success(result);
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  private RestLoginResult getRestLoginResult(String userId, LoginUserOutput output)
  {
    RestLoginResult result = new RestLoginResult(output.getToken());

    // todo : refactor implementation, get single group id.
    result.setGroup(output.getGroup());
    result.setUserName(userId);
    List<String> permissions = new ArrayList<>();
    for (Permission permission : output.getPermissions())
    {
      String permissionString = permission.getPermissionString();

      if (!permissions.contains(permissionString))
      {
        permissions.add(permissionString);
      }
    }
    result.setPermissions(getRestPermission(permissions));
    return result;
  }
}

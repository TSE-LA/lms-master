package mn.erin.lms.base.rest.api.aim;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.naming.CommunicationException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authc.AuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.repository.AimRepositoryRegistry;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.usecase.authentication.LoginUserInput;
import mn.erin.domain.aim.usecase.authentication.LoginUserOutput;
import mn.erin.domain.aim.usecase.authentication.ValidateSession;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.infrastucture.rest.common.response.RestError;
import mn.erin.infrastucture.rest.common.response.RestResponse;
import mn.erin.infrastucture.rest.common.response.RestResult;
import mn.erin.lms.base.aim.authentication.LoginUser;
import mn.erin.lms.base.aim.permission.PermissionDto;
import mn.erin.lms.base.rest.api.aim.model.LoginRequest;
import mn.erin.lms.base.rest.api.aim.model.LoginResult;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Api("AIM")
@RequestMapping(value = "/aim", name = "Provides access and identity management features")
@RestController
public class AimRestApi
{
  private static final Logger LOG = LoggerFactory.getLogger(AimRestApi.class);

  private final AuthenticationService authenticationService;
  private final AimRepositoryRegistry repositoryRegistry;

  public AimRestApi(AuthenticationService authenticationService, AimRepositoryRegistry repositoryRegistry)
  {
    this.authenticationService = authenticationService;
    this.repositoryRegistry = repositoryRegistry;
  }

  @ApiOperation("Login user")
  @PostMapping(value = "/login")
  public ResponseEntity<RestResult> login(@RequestBody LoginRequest request)
  {
    LoginUserInput input = new LoginUserInput(request.getTenantId(), request.getUsername(), request.getPassword());
    LoginUser loginUser = new LoginUser(authenticationService, repositoryRegistry);

    LoginUserOutput loginUserOutput;
    try
    {
      loginUserOutput = loginUser.execute(input);
    }
    catch (AuthenticationException e)
    {
      if (e.getCause() instanceof CommunicationException)
      {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(RestError.of(e.getMessage()));
      }

      return RestResponse.unauthorized(e.getMessage());
    }
    catch (UseCaseException e)
    {
      LOG.debug("Authentication failed", e);
      return RestResponse.unauthorized(e.getMessage());
    }

    try
    {
      LoginResult result = getLoginResult(loginUserOutput);

      return RestResponse.success(result);
    }
    catch (NoSuchElementException e)
    {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(RestError.of(e.getMessage()));
    }
  }

  @ApiOperation("Validates user session")
  @GetMapping(value = "/validate-session")
  public ResponseEntity validate()
  {
    if (!authenticationService.isCurrentUserAuthenticated())
    {
      return RestResponse.unauthorized("The current user is not authenticated!");
    }

    ValidateSession validateSession = new ValidateSession(authenticationService, repositoryRegistry);

    try
    {
      LoginUserOutput loginUserOutput = validateSession.execute(null);
      LoginResult result = getLoginResult(loginUserOutput);
      return RestResponse.success(result);
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError("Failed to get the user's role and permissions = " + e.getMessage());
    }
  }

  @ApiOperation("Logout user")
  @GetMapping(value = "/logout")
  public ResponseEntity<RestResult> logout()
  {
    authenticationService.logout();
    return RestResponse.success();
  }

  private LoginResult getLoginResult(LoginUserOutput loginUserOutput)
  {
    LoginResult result = new LoginResult(loginUserOutput.getToken());
    result.setUsername(loginUserOutput.getUsername());
    result.setDepartmentId(loginUserOutput.getGroup());
    result.setRole(loginUserOutput.getRole());
    Set<PermissionDto> resultPermission = new HashSet<>();
    for (Permission permission : loginUserOutput.getPermissions())
    {
      resultPermission.add(new PermissionDto(permission.getPermissionString(), permission.getProperties()));
    }
    result.setPermissions(resultPermission);

    return result;
  }
}

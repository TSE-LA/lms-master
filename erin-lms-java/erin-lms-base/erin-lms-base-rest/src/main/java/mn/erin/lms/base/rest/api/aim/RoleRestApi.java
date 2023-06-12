package mn.erin.lms.base.rest.api.aim;

import java.util.List;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import mn.erin.domain.aim.repository.RoleRepository;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.aim.service.TenantIdProvider;
import mn.erin.domain.aim.usecase.role.GetAllRoles;
import mn.erin.domain.aim.usecase.role.GetRoleOutput;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.infrastucture.rest.common.response.RestResponse;
import mn.erin.infrastucture.rest.common.response.RestResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Api("Role")
@RequestMapping(value = "/aim/roles", name = "Provides role operations")
@RestController
public class RoleRestApi
{
  private final AuthorizationService authorizationService;
  private final AuthenticationService authenticationService;
  private final RoleRepository roleRepository;
  private final TenantIdProvider tenantIdProvider;

  public RoleRestApi(AuthorizationService authorizationService, AuthenticationService authenticationService,
    RoleRepository roleRepository, TenantIdProvider tenantIdProvider)
  {
    this.authorizationService = authorizationService;
    this.authenticationService = authenticationService;
    this.roleRepository = roleRepository;
    this.tenantIdProvider = tenantIdProvider;
  }

  @ApiOperation("Get all roles")
  @GetMapping
  public ResponseEntity<RestResult> readAll(@RequestParam String tenantId) throws UseCaseException
  {
    GetAllRoles getAllRoles = new GetAllRoles(authenticationService, authorizationService, roleRepository, tenantIdProvider);
    List<GetRoleOutput> result = getAllRoles.execute(null);
    return RestResponse.success(result);
  }
}

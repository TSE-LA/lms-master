package mn.erin.aim.rest.controller;

import java.util.List;
import javax.inject.Inject;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import mn.erin.domain.aim.repository.RoleRepository;
import mn.erin.domain.aim.usecase.role.GetAllRoles;
import mn.erin.domain.aim.usecase.role.GetRoleOutput;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.infrastucture.rest.common.response.RestResponse;
import mn.erin.infrastucture.rest.common.response.RestResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Api("Role")
@RequestMapping(value = "/aim/roles", name = "Provides AIM role management features")
@RestController
public class RoleRestApi extends BaseAimRestApi
{
  private final RoleRepository roleRepository;

  @Inject
  public RoleRestApi(RoleRepository roleRepository)
  {
    this.roleRepository = roleRepository;
  }

  @ApiOperation("Get all roles")
  @GetMapping
  public ResponseEntity<RestResult> readAll()
  {
    GetAllRoles getAllRoles = new GetAllRoles(authenticationService, authorizationService, roleRepository, tenantIdProvider);

    try
    {
      List<GetRoleOutput> result = getAllRoles.execute(null);
      return RestResponse.success(result);
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }
}

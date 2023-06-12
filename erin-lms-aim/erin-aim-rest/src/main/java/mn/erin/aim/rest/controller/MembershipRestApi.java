package mn.erin.aim.rest.controller;

import java.util.List;
import javax.inject.Inject;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import mn.erin.aim.rest.model.RestMembership;
import mn.erin.aim.rest.model.RestMembershipRequestBody;
import mn.erin.domain.aim.repository.AimRepositoryRegistry;
import mn.erin.domain.aim.usecase.membership.ChangeUserGroup;
import mn.erin.domain.aim.usecase.membership.ChangeUserGroupInput;
import mn.erin.domain.aim.usecase.membership.ChangeUserGroupOutput;
import mn.erin.domain.aim.usecase.membership.CreateMemberships;
import mn.erin.domain.aim.usecase.membership.CreateMembershipsInput;
import mn.erin.domain.aim.usecase.membership.DeleteMembership;
import mn.erin.domain.aim.usecase.membership.GetMembershipOutput;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.infrastucture.rest.common.response.RestResponse;
import mn.erin.infrastucture.rest.common.response.RestResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Api("Membership")
@RequestMapping(value = "/aim/memberships", name = "Provides AIM membership management features")
@RestController
public class MembershipRestApi extends BaseAimRestApi
{
  private final AimRepositoryRegistry aimRepositoryRegistry;

  @Inject
  public MembershipRestApi(AimRepositoryRegistry aimRepositoryRegistry)
  {
    this.aimRepositoryRegistry = aimRepositoryRegistry;
  }

  @ApiOperation("Create membership")
  @PostMapping
  public ResponseEntity<RestResult> create(@RequestBody RestMembershipRequestBody request)
  {
    CreateMembershipsInput input = new CreateMembershipsInput(request.getGroupId(), request.getRoleId(), request.getUsers(), request.getTenantId());
    CreateMemberships createMemberships = new CreateMemberships(authenticationService, authorizationService, aimRepositoryRegistry, tenantIdProvider);

    try
    {
      List<GetMembershipOutput> memberships = createMemberships.execute(input);
      return RestResponse.success(memberships);
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Deletes a membership")
  @DeleteMapping("/{membershipId}")
  public ResponseEntity<RestResult> delete(@PathVariable String membershipId)
  {
    DeleteMembership deleteMembership = new DeleteMembership(authenticationService, authorizationService, aimRepositoryRegistry.getMembershipRepository());

    try
    {
      boolean isDeleted = deleteMembership.execute(membershipId);
      return RestResponse.success(isDeleted);
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Deletes a membership")
  @PatchMapping
  public ResponseEntity<RestResult> moveUserGroup(@RequestBody RestMembership restMembership)
  {
    ChangeUserGroupInput input = new ChangeUserGroupInput(restMembership.getUserId(), restMembership.getGroupId());
    ChangeUserGroup changeUserGroup =
      new ChangeUserGroup(authenticationService, authorizationService, aimRepositoryRegistry.getMembershipRepository(), tenantIdProvider);

    try
    {
      ChangeUserGroupOutput output = changeUserGroup.execute(input);
      return RestResponse.success(output.isUpdated());
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }
}

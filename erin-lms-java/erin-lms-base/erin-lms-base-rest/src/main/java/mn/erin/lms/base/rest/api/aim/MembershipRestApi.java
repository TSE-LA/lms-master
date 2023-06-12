package mn.erin.lms.base.rest.api.aim;

import java.util.List;
import java.util.stream.Collectors;
import javax.inject.Inject;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mn.erin.domain.aim.repository.AimRepositoryRegistry;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.aim.service.TenantIdProvider;
import mn.erin.domain.aim.service.UserAggregateService;
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
import mn.erin.lms.base.aim.AccessIdentityManagement;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.enrollment.AssignCoursesToLearner;
import mn.erin.lms.base.domain.usecase.enrollment.dto.AssignCoursesInput;
import mn.erin.lms.base.domain.usecase.membership.GetCurrentUserGroupMembers;
import mn.erin.lms.base.domain.usecase.membership.GetCurrentUserGroups;
import mn.erin.lms.base.domain.usecase.membership.GetCurrentUserMembers;
import mn.erin.lms.base.rest.api.aim.model.RestMembership;
import mn.erin.lms.base.rest.api.aim.model.RestMemberships;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Api("Membership")
@RequestMapping(value = "/aim", name = "Provides membership operations")
@RestController
public class MembershipRestApi
{
  private static final Logger LOGGER = LoggerFactory.getLogger(MembershipRestApi.class);

  private final AuthenticationService authenticationService;
  private final AuthorizationService authorizationService;
  private final AimRepositoryRegistry aimRepositoryRegistry;
  private final LmsRepositoryRegistry lmsRepositoryRegistry;
  private final LmsServiceRegistry lmsServiceRegistry;
  private final TenantIdProvider tenantIdProvider;
  private final AccessIdentityManagement accessIdentityManagement;
  private final UserAggregateService userAggregateService;

  @Inject
  public MembershipRestApi(AuthenticationService authenticationService, AuthorizationService authorizationService,
    AimRepositoryRegistry aimRepositoryRegistry, LmsRepositoryRegistry lmsRepositoryRegistry,
    LmsServiceRegistry lmsServiceRegistry, TenantIdProvider tenantIdProvider, AccessIdentityManagement accessIdentityManagement,
      UserAggregateService userAggregateService)
  {
    this.authenticationService = authenticationService;
    this.authorizationService = authorizationService;
    this.aimRepositoryRegistry = aimRepositoryRegistry;
    this.lmsRepositoryRegistry = lmsRepositoryRegistry;
    this.lmsServiceRegistry = lmsServiceRegistry;
    this.tenantIdProvider = tenantIdProvider;
    this.accessIdentityManagement = accessIdentityManagement;
    this.userAggregateService = userAggregateService;
  }

  @ApiOperation("Get All Groups and Members")
  @GetMapping(value = "/group-members")
  public ResponseEntity readAll()
  {
    GetCurrentUserGroupMembers getCurrentUserGroupMembers = new GetCurrentUserGroupMembers(accessIdentityManagement, userAggregateService);
    try
    {
      return RestResponse.success(getCurrentUserGroupMembers.execute(null));

    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError("Could not get group members");
    }
  }

  @ApiOperation("Get All Groups")
  @GetMapping(value = "/current-user-groups")
  public ResponseEntity readAllGroups()
  {
    GetCurrentUserGroups getCurrentUserGroups = new GetCurrentUserGroups(accessIdentityManagement);

    try
    {
      return RestResponse.success(getCurrentUserGroups.execute(null));
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError("Could not get user groups");
    }
  }

  @ApiOperation("Get All Groups")
  @GetMapping(value = "/current-user-group-members")
  public ResponseEntity readAllUsers()
  {
    GetCurrentUserMembers getCurrentUserUsers = new GetCurrentUserMembers(accessIdentityManagement, userAggregateService);

    try
    {
      return RestResponse.success(getCurrentUserUsers.execute(null));
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError("Could not get user group's members");
    }
  }

  @ApiOperation("Create membership")
  @PostMapping("/memberships")
  public ResponseEntity create(@RequestBody RestMemberships request)
  {
    CreateMembershipsInput input =
      new CreateMembershipsInput(request.getGroupId(), request.getRoleId(), request.getUsers(), tenantIdProvider.getCurrentUserTenantId());
    CreateMemberships createMembership = new CreateMemberships(
      authenticationService,
      authorizationService,
      aimRepositoryRegistry,
      tenantIdProvider);

    String role = accessIdentityManagement.getRole(accessIdentityManagement.getCurrentUsername());

    if (("LMS_SUPERVISOR".equals(role) || "LMS_MANAGER".equals(role))
      && "LMS_ADMIN".equals(request.getRoleId()))
    {
      return RestResponse.unauthorized("Supervisor is not allowed to create an admin user");
    }

    try
    {
      List<GetMembershipOutput> result = createMembership.execute(input);

      AssignCoursesToLearner assignCoursesToLearner = new AssignCoursesToLearner(lmsServiceRegistry, lmsRepositoryRegistry, aimRepositoryRegistry.getGroupRepository());
      AssignCoursesInput assignCoursesInput = new AssignCoursesInput(result.stream().map(GetMembershipOutput::getUserId).collect(Collectors.toList()),
          request.getRoleId(), input.getGroupId());
      assignCoursesToLearner.execute(assignCoursesInput);

      return RestResponse.success(result);
    }
    catch (UseCaseException e)
    {
      LOGGER.error(e.getMessage(), e);
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Move User Group")
  @PatchMapping("/memberships")
  public ResponseEntity<RestResult> moveUserGroup(@RequestBody RestMembership restMembership)
  {
    ChangeUserGroupInput input = new ChangeUserGroupInput(restMembership.getUserId(), restMembership.getGroupId());
    ChangeUserGroup moveUserGroup = new ChangeUserGroup(authenticationService, authorizationService, aimRepositoryRegistry.getMembershipRepository(), tenantIdProvider);

    try
    {
      ChangeUserGroupOutput output = moveUserGroup.execute(input);
      return RestResponse.success(output.isUpdated());
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Delete membership")
  @DeleteMapping(value = "/memberships/{membershipId}")
  public ResponseEntity<RestResult> delete(@PathVariable String membershipId)
  {
    DeleteMembership deleteMembership = new DeleteMembership(authenticationService, authorizationService, aimRepositoryRegistry.getMembershipRepository());
    String role = accessIdentityManagement.getRole(accessIdentityManagement.getCurrentUsername());

    if (("LMS_SUPERVISOR".equals(role) || "LMS_MANAGER".equals(role)))
    {
      return RestResponse.unauthorized("Supervisor is not allowed to create an admin user");
    }

    try
    {
      return RestResponse.success(deleteMembership.execute(membershipId));
    }
    catch (UseCaseException e)
    {
      LOGGER.error(e.getMessage(), e);
      return RestResponse.internalError(e.getMessage());
    }
  }
}

package mn.erin.lms.base.rest.api.aim;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import mn.erin.common.datetime.DateTimeUtils;
import mn.erin.domain.aim.model.membership.Membership;
import mn.erin.domain.aim.model.user.UserAggregate;
import mn.erin.domain.aim.repository.AimRepositoryRegistry;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.aim.service.UserAggregateService;
import mn.erin.domain.aim.usecase.user.GetAllUsers;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.infrastucture.rest.common.response.RestResponse;
import mn.erin.infrastucture.rest.common.response.RestResult;
import mn.erin.lms.base.aim.role.LmsRole;
import mn.erin.lms.base.domain.model.RestUserResultForDownload;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.DownloadAllUserInformation;
import mn.erin.lms.base.domain.usecase.user.GetAllMyUsers;
import mn.erin.lms.base.domain.usecase.user.GetInstructors;
import mn.erin.lms.base.domain.usecase.user.GetUserByRoles;
import mn.erin.lms.base.domain.usecase.user.dto.GetUserByRolesInput;
import mn.erin.lms.base.domain.usecase.user.dto.GetUserByRolesOutput;
import mn.erin.lms.base.rest.api.aim.model.MembershipResponse;
import mn.erin.lms.base.rest.api.aim.model.UserResponseModel;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Api("User")
@RequestMapping(value = "lms/users", name = "Provides LMS specific user operations")
@RestController
public class LmsUserRestApi
{
  private static final Logger LOGGER = LoggerFactory.getLogger(LmsUserRestApi.class);

  private final AuthenticationService authenticationService;
  private final AuthorizationService authorizationService;
  private final AimRepositoryRegistry aimRepositoryRegistry;
  private final LmsServiceRegistry lmsServiceRegistry;
  private final UserAggregateService userAggregateService;
  private final LmsRepositoryRegistry lmsRepositoryRegistry;

  @Inject
  public LmsUserRestApi(AuthenticationService authenticationService, AuthorizationService authorizationService,
                        AimRepositoryRegistry aimRepositoryRegistry, UserAggregateService userAggregateService,
                        LmsServiceRegistry lmsServiceRegistry, LmsRepositoryRegistry lmsRepositoryRegistry)
  {
    this.authenticationService = authenticationService;
    this.authorizationService = authorizationService;
    this.aimRepositoryRegistry = aimRepositoryRegistry;
    this.userAggregateService = userAggregateService;
    this.lmsServiceRegistry = lmsServiceRegistry;
    this.lmsRepositoryRegistry = lmsRepositoryRegistry;
  }

  @ApiOperation("Get all users")
  @GetMapping
  public ResponseEntity<RestResult> readAll(@RequestParam boolean includeMe)
  {
    GetAllMyUsers getAllMyUsers = new GetAllMyUsers(lmsRepositoryRegistry, lmsServiceRegistry, aimRepositoryRegistry);
    List<GetUserByRolesOutput> outputs;
    try
    {
      outputs = getAllMyUsers.execute(includeMe);
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError("Failed to get all users cause: " + e.getMessage());
    }

    return RestResponse.success(this.getUserResponseModelList(outputs));
  }

  @ApiOperation("Get all users with role")
  @GetMapping("/all-with-role")
  public ResponseEntity<RestResult> readAllUserWithRole(@RequestParam boolean includeMe)
  {
    List<String> roles = new ArrayList<>();
    roles.add(LmsRole.LMS_ADMIN.name());
    roles.add(LmsRole.LMS_MANAGER.name());
    roles.add(LmsRole.LMS_SUPERVISOR.name());
    roles.add(LmsRole.LMS_USER.name());

    return getUsersByRoles(roles, includeMe);
  }

  @ApiOperation("Get all employees")
  @GetMapping("/employees")
  public ResponseEntity<RestResult> readEmployees()
  {
    List<String> roles = new ArrayList<>();
    roles.add(LmsRole.LMS_USER.name());

    return getUsersByRoles(roles, false);
  }

  @ApiOperation("Get all supervisor and managers")
  @GetMapping("/managers/supervisors")
  public ResponseEntity<RestResult> readManagersAndSupervisors()
  {
    List<String> roles = new ArrayList<>();
    roles.add(LmsRole.LMS_MANAGER.name());
    roles.add(LmsRole.LMS_SUPERVISOR.name());

    return getUsersByRoles(roles, false);
  }

  @ApiOperation("Get all managers")
  @GetMapping("/managers")
  public ResponseEntity<RestResult> readManagers()
  {
    List<String> roles = new ArrayList<>();
    roles.add(LmsRole.LMS_MANAGER.name());

    return getUsersByRoles(roles, false);
  }

  @ApiOperation("Get all supervisor and managers")
  @GetMapping("/supervisors")
  public ResponseEntity<RestResult> readSupervisors()
  {
    List<String> roles = new ArrayList<>();
    roles.add(LmsRole.LMS_SUPERVISOR.name());

    return getUsersByRoles(roles, false);
  }

  @ApiOperation("Get all supervisors and employees")
  @GetMapping("/supersEmployees")
  public ResponseEntity<RestResult> readSupersEmployees()
  {
    List<String> roles = new ArrayList<>();
    roles.add(LmsRole.LMS_USER.name());
    roles.add(LmsRole.LMS_SUPERVISOR.name());

    return getUsersByRoles(roles, false);
  }

  @ApiOperation("Lists all instructors")
  @GetMapping("/instructors")
  public ResponseEntity<RestResult> readInstructors()
  {
    GetInstructors getInstructors = new GetInstructors(lmsServiceRegistry.getDepartmentService());
    Set<String> instructors = getInstructors.execute(null);
    return RestResponse.success(instructors);
  }


  @ApiOperation("Download all user's information")
  @GetMapping("/export")
  public ResponseEntity download()
  {
    GetAllUsers getAllUsers = new GetAllUsers(authenticationService, authorizationService, userAggregateService);
    DownloadAllUserInformation downloadAllUserInformation = new DownloadAllUserInformation(lmsRepositoryRegistry, lmsServiceRegistry);
    List<RestUserResultForDownload> userInfos = new ArrayList<>();
    String currentFormattedDate = DateTimeUtils.getCurrentLocalDateTime(DateTimeUtils.LONG_ISO_DATE_FORMAT);
    try
    {
      List<UserAggregate> allUsers = getAllUsers.execute(null).getAllUserAggregates();

      for (UserAggregate user : allUsers)
      {
        userInfos.add(toUserResponseModelForDownload(user));
      }
      byte[] userInfo = downloadAllUserInformation.execute(userInfos);
      ByteArrayResource resource = new ByteArrayResource(userInfo);
      return ResponseEntity.ok()
          .contentLength(resource.contentLength())
          .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
          .header("Content-Disposition", "attachment; filename=\"All-user's-information-" + currentFormattedDate + ".xlsx\"")
          .body(resource);
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  private ResponseEntity<RestResult> getUsersByRoles(List<String> roles, boolean includeMe)
  {
    GetUserByRoles getUserByRoles = new GetUserByRoles(lmsRepositoryRegistry, lmsServiceRegistry, aimRepositoryRegistry);
    List<GetUserByRolesOutput> outputs = new ArrayList<>();
    try
    {
      GetUserByRolesInput input =  new GetUserByRolesInput(roles, includeMe);
      outputs = getUserByRoles.execute(input);
    }
    catch (UseCaseException e)
    {
      RestResponse.internalError("Failed to get users cause: " + e.getMessage());
    }

    return RestResponse.success(this.getUserResponseModelList(outputs));
  }

  private List<UserResponseModel> getUserResponseModelList(List<GetUserByRolesOutput> users)
  {
    List<UserResponseModel> result = new ArrayList<>();
    for (GetUserByRolesOutput output : users){
      UserAggregate user = output.getUser();
      Membership membership = output.getMembership();
      UserResponseModel userResponseModel = new UserResponseModel();
      userResponseModel.setId(user.getUserId());
      userResponseModel.setUsername(user.getUsername());
      userResponseModel.setFirstName(user.getFirstName());
      userResponseModel.setLastName(user.getLastName());
      userResponseModel.setEmail(user.getEmail());
      userResponseModel.setPhoneNumber(user.getPhoneNumber());
      userResponseModel.setGroupPath(output.getGroupPath());
      if(membership != null){
        userResponseModel
            .setMembership(new MembershipResponse(membership.getMembershipId().getId(), membership.getGroupId().getId(), membership.getRoleId().getId()));
      }
      result.add(userResponseModel);
    }

    return result;
  }

  private RestUserResultForDownload toUserResponseModelForDownload(UserAggregate user)
  {
    RestUserResultForDownload model = new RestUserResultForDownload();
    model.setUsername(user.getUsername());
    model.setFirstname(user.getFirstName());
    model.setLastname(user.getLastName());
    model.setEmail(user.getEmail());
    model.setPhoneNumber(user.getPhoneNumber());
    model.setModifiedDate(user.getLastModified().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm")));
    model.setStatus(user.getStatus());

    return model;
  }
}

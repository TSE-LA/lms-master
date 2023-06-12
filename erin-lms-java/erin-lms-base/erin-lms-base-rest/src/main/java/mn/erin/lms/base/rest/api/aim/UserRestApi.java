package mn.erin.lms.base.rest.api.aim;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.inject.Inject;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import mn.erin.common.formats.ImageExtension;
import mn.erin.domain.aim.model.user.User;
import mn.erin.domain.aim.model.user.UserAggregate;
import mn.erin.domain.aim.repository.AimRepositoryRegistry;
import mn.erin.domain.aim.service.AimApplicationDataChecker;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.aim.service.PasswordEncryptService;
import mn.erin.domain.aim.service.RealmTypeProvider;
import mn.erin.domain.aim.service.TenantIdProvider;
import mn.erin.domain.aim.service.UserAggregateService;
import mn.erin.domain.aim.usecase.authentication.GetRealmType;
import mn.erin.domain.aim.usecase.group.GetGroupUsersByRole;
import mn.erin.domain.aim.usecase.group.GetUsersByRoleInput;
import mn.erin.domain.aim.usecase.group.GetUsersByRoleOutput;
import mn.erin.domain.aim.usecase.membership.GetMembershipOutput;
import mn.erin.domain.aim.usecase.membership.GetUserMemberships;
import mn.erin.domain.aim.usecase.membership.GetUserMembershipsInput;
import mn.erin.domain.aim.usecase.user.ArchiveUser;
import mn.erin.domain.aim.usecase.user.ArchiveUserInput;
import mn.erin.domain.aim.usecase.user.ArchiveUsers;
import mn.erin.domain.aim.usecase.user.CheckUserExists;
import mn.erin.domain.aim.usecase.user.CreateUser;
import mn.erin.domain.aim.usecase.user.CreateUserInput;
import mn.erin.domain.aim.usecase.user.CreateUserOutput;
import mn.erin.domain.aim.usecase.user.DeleteUser;
import mn.erin.domain.aim.usecase.user.DeleteUserInput;
import mn.erin.domain.aim.usecase.user.DeleteUsers;
import mn.erin.domain.aim.usecase.user.GenerateUsersFromCsv;
import mn.erin.domain.aim.usecase.user.GenerateUsersFromCsvInput;
import mn.erin.domain.aim.usecase.user.GetAllUsers;
import mn.erin.domain.aim.usecase.user.GetParentGroupUsersByRole;
import mn.erin.domain.aim.usecase.user.GetParentGroupUsersByRoleInput;
import mn.erin.domain.aim.usecase.user.GetParentGroupUsersByRoleOutput;
import mn.erin.domain.aim.usecase.user.GetSubGroupUsersByRole;
import mn.erin.domain.aim.usecase.user.GetSubGroupUsersByRoleInput;
import mn.erin.domain.aim.usecase.user.GetSubGroupUsersByRoleOutput;
import mn.erin.domain.aim.usecase.user.GetUser;
import mn.erin.domain.aim.usecase.user.GetUserOutput;
import mn.erin.domain.aim.usecase.user.GetUserProfileImageURL;
import mn.erin.domain.aim.usecase.user.GetUserSelf;
import mn.erin.domain.aim.usecase.user.UpdateUser;
import mn.erin.domain.aim.usecase.user.UpdateUserInput;
import mn.erin.domain.aim.usecase.user.UpdateUserSelf;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.infrastucture.rest.common.response.RestResponse;
import mn.erin.infrastucture.rest.common.response.RestResult;
import mn.erin.lms.base.domain.service.TemporaryFileApi;
import mn.erin.lms.base.rest.api.aim.model.RestMembership;
import mn.erin.lms.base.rest.api.aim.model.RestUserBody;
import mn.erin.lms.base.rest.api.aim.model.RestUserResult;
import mn.erin.lms.base.rest.api.aim.model.RestUsersArchiveBody;
import mn.erin.lms.base.rest.model.RestFile;

import static mn.erin.domain.aim.constant.AimErrorMessageConstant.INVALID_ROLE_ID_INPUT;
import static mn.erin.domain.aim.constant.AimErrorMessageConstant.INVALID_USERNAME_INPUT;
import static mn.erin.domain.aim.constant.AimErrorMessageConstant.INVALID_USER_ID_INPUT;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Api("User")
@RequestMapping(value = "/aim/users", name = "Provides user CRUD operations")
@RestController
public class UserRestApi
{
  private static final Logger LOGGER = LoggerFactory.getLogger(UserRestApi.class);

  private final AuthenticationService authenticationService;
  private final AuthorizationService authorizationService;
  private final AimRepositoryRegistry aimRepositoryRegistry;
  private final TenantIdProvider tenantIdProvider;
  private final PasswordEncryptService passwordEncryptService;
  private final UserAggregateService userAggregateService;
  private final AimApplicationDataChecker lmsDataChecker;
  private final RealmTypeProvider realmTypeProvider;
  private final TemporaryFileApi temporaryFileApi;

  private final Map<String, File> attachments = new HashMap<>();

  @Inject
  public UserRestApi(AuthenticationService authenticationService, AuthorizationService authorizationService,
      AimRepositoryRegistry aimRepositoryRegistry, TenantIdProvider tenantIdProvider,
      PasswordEncryptService passwordEncryptService, UserAggregateService userAggregateService,
      AimApplicationDataChecker lmsDataChecker, RealmTypeProvider realmTypeProvider,
      TemporaryFileApi temporaryFileApi)
  {
    this.authenticationService = authenticationService;
    this.authorizationService = authorizationService;
    this.aimRepositoryRegistry = aimRepositoryRegistry;
    this.tenantIdProvider = tenantIdProvider;
    this.passwordEncryptService = passwordEncryptService;
    this.userAggregateService = userAggregateService;
    this.lmsDataChecker = lmsDataChecker;
    this.realmTypeProvider = realmTypeProvider;
    this.temporaryFileApi = temporaryFileApi;
  }

  @ApiOperation("Creates a user")
  @PostMapping
  public ResponseEntity<RestResult> create(@RequestBody RestUserBody request)
  {
    Objects.requireNonNull(request);
    CreateUser createUser = new CreateUser(authenticationService, authorizationService, aimRepositoryRegistry);

    CreateUserInput input = new CreateUserInput(tenantIdProvider.getCurrentUserTenantId(), request.getUsername(),
      passwordEncryptService.encrypt(request.getPassword()));

    input.setFirstName(request.getFirstName());
    input.setLastName(request.getLastName());
    input.setGender(request.getGender());
    input.setBirthday(request.getBirthday());
    input.setJobTitle(request.getJobTitle());
    input.setEmail(request.getEmail());
    input.setPhoneNumber(request.getPhoneNumber());
    input.setProperties(request.getProperties());
    input.setImage(getImage());

    try
    {
      CreateUserOutput output = createUser.execute(input);
      UserAggregate aggregate = output.getUserAggregate();

      RestUserResult result = new RestUserResult();
      result.setUserId(aggregate.getUserId());
      result.setUsername(aggregate.getUsername());
      result.setEmail(aggregate.getEmail());
      result.setPhoneNumber(aggregate.getPhoneNumber());
      result.setLastName(aggregate.getLastName());
      result.setFirstName(aggregate.getFirstName());
      result.setGender(aggregate.getGender());
      result.setBirthday(aggregate.getBirthday());
      result.setJobTitle(aggregate.getJobTitle());
      result.setStatus(aggregate.getStatus());
      result.setDateLastModified(aggregate.getLastModified());
      result.setMessage(aggregate.getMessage());
      result.setDeletable(true);

      LOGGER.info("User with username: [{}] created.", aggregate.getUsername());
      return RestResponse.created(result.getUserId(), result);
    }
    catch (UseCaseException e)
    {
      return RestResponse.badRequest(e.getMessage());
    }
  }

  @ApiOperation("Get all users")
  @GetMapping
  public ResponseEntity<RestResult> readAll()
  {
    GetAllUsers getAllUsers = new GetAllUsers(authenticationService, authorizationService, userAggregateService);

    List<RestUserResult> response = new ArrayList<>();

    try
    {
      List<UserAggregate> allUsers = getAllUsers.execute(true).getAllUserAggregates();

      for (UserAggregate user : allUsers)
      {
        response.add(toUserResponseModelFromAggregate(user));
      }

      // Filter self
      response.removeIf(user -> user.getUsername() != null && user.getUsername().equals(authenticationService.getCurrentUsername()));

      return RestResponse.success(response);
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Get own profile")
  @GetMapping("/profile")
  public ResponseEntity<RestResult> readSelf(@RequestParam (required = false) String username)
  {
    GetUserSelf getUserSelf = new GetUserSelf(authenticationService, authorizationService, aimRepositoryRegistry, userAggregateService);

    try
    {
      return RestResponse.success(getUserSelf.execute(username));
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Import users")
  @PostMapping("/import")
  public ResponseEntity<RestResult> importUsers(@RequestParam("file") MultipartFile file)
  {
    if (file == null || file.isEmpty())
    {
      return RestResponse.badRequest("Please select a CSV file to upload.");
    }

    try
    {
      GenerateUsersFromCsv generateUsers = new GenerateUsersFromCsv(aimRepositoryRegistry, tenantIdProvider, passwordEncryptService);
      return RestResponse.success(generateUsers.execute(new GenerateUsersFromCsvInput(file.getInputStream())));
    }
    catch (UseCaseException | IOException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Check user exists")
  @PostMapping("/check")
  public ResponseEntity<RestResult> checkUserByUsername(@RequestBody String username)
  {
    if (StringUtils.isBlank(username))
    {
      return RestResponse.badRequest(INVALID_USERNAME_INPUT);
    }

    CheckUserExists checkUserExists = new CheckUserExists(authenticationService, authorizationService, aimRepositoryRegistry);
    try
    {
      return RestResponse.success(checkUserExists.execute(username));
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Update user")
  @PutMapping("/{userId}")
  public ResponseEntity<RestResult> update(@PathVariable String userId, @RequestBody RestUserBody request)
  {
    Objects.requireNonNull(request);

    UpdateUser updateUser = new UpdateUser(authenticationService, authorizationService, aimRepositoryRegistry, passwordEncryptService);
    try
    {
      UpdateUserInput input = new UpdateUserInput(userId, request.getUsername(), !StringUtils.isBlank(request.getPassword()) ? request.getPassword() : null);
      input.setFirstName(request.getFirstName());
      input.setLastName(request.getLastName());
      input.setGender(request.getGender());
      input.setBirthday(request.getBirthday());
      input.setEmail(request.getEmail());
      input.setPhoneNumber(request.getPhoneNumber());
      input.setImage(getImage());
      input.setProperties(request.getProperties());
      return RestResponse.success(updateUser.execute(input));
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Update user self")
  @PutMapping("/profile")
  public ResponseEntity<RestResult> updateUserSelf(@RequestBody RestUserBody request)
  {
    Objects.requireNonNull(request);

    UpdateUserSelf updateUserSelf = new UpdateUserSelf(authenticationService, aimRepositoryRegistry, passwordEncryptService);
    try
    {
      UpdateUserInput input = new UpdateUserInput();
      input.setUsername(request.getUsername());
      input.setPassword(request.getPassword());
      input.setFirstName(request.getFirstName());
      input.setLastName(request.getLastName());
      input.setGender(request.getGender());
      input.setBirthday(request.getBirthday());
      input.setJobTitle(request.getJobTitle());
      input.setEmail(request.getEmail());
      input.setPhoneNumber(request.getPhoneNumber());
      input.setImage(getImage());
      input.setOldPassword(request.getOldPassword());
      input.setNewPassword(request.getNewPassword());
      input.setProperties(request.getProperties());
      return RestResponse.success(updateUserSelf.execute(input));
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Uploads an image")
  @PostMapping("/image")
  public ResponseEntity<RestResult> uploadAttachment(@RequestParam MultipartFile multipartFile)
  {
    if (!authenticationService.isCurrentUserAuthenticated())
    {
      return RestResponse.unauthorized("No user logged in.");
    }

    String currentUsername = authenticationService.getCurrentUsername();
    if (multipartFile == null || multipartFile.isEmpty())
    {
      return RestResponse.badRequest("File is empty!");
    }

    String extension = FilenameUtils.getExtension(multipartFile.getOriginalFilename());

    if (extension == null)
    {
      return RestResponse.badRequest("Invalid file extension!");
    }

    if (!Arrays.stream(ImageExtension.values()).map(ImageExtension::name).collect(Collectors.toList()).contains(extension.toUpperCase()))
    {
      return RestResponse.badRequest("Unsupported image format!");
    }

    try
    {
      File file = saveToFile(multipartFile);
      attachments.put(currentUsername, file);
      RestFile result = new RestFile(multipartFile.getOriginalFilename(), file.getPath(), currentUsername);
      return RestResponse.success(result);
    }
    catch (IOException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Delete user")
  @DeleteMapping("/{userId}")
  public ResponseEntity<RestResult> delete(@PathVariable String userId)
  {
    if (StringUtils.isBlank(userId))
    {
      return RestResponse.badRequest(INVALID_USER_ID_INPUT);
    }

    DeleteUser deleteUser = new DeleteUser(authenticationService, authorizationService, aimRepositoryRegistry, lmsDataChecker);

    try
    {
      return RestResponse.success(deleteUser.execute(new DeleteUserInput(userId)));
    }
    catch (UseCaseException e)
    {
      return RestResponse.badRequest(e.getMessage());
    }
  }

  @ApiOperation("Delete users")
  @PostMapping("/delete")
  public ResponseEntity<RestResult> delete(@RequestBody String[] userIds)
  {
    DeleteUsers deleteUsers = new DeleteUsers(authenticationService, authorizationService, aimRepositoryRegistry, lmsDataChecker);
    List<DeleteUserInput> input = Arrays.stream(userIds).filter(userId -> !StringUtils.isBlank(userId)).map(DeleteUserInput::new).collect(Collectors.toList());
    try
    {
      deleteUsers.execute(input);
      return RestResponse.success(true);
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Archive or unarchive user")
  @PutMapping("/archive/{userId}")
  public ResponseEntity<RestResult> archive(@PathVariable String userId, @RequestBody RestUsersArchiveBody request)
  {

    ArchiveUser archiveUser = new ArchiveUser(authenticationService, authorizationService, aimRepositoryRegistry);
    try
    {
      if (StringUtils.isBlank(userId))
      {
        return RestResponse.badRequest(INVALID_USER_ID_INPUT);
      }
      archiveUser.execute(new ArchiveUserInput(userId, request.isArchived()));
      return RestResponse.success(true);
    }
    catch (UseCaseException e)
    {
      LOGGER.error(e.getMessage());
      return RestResponse.success(false);
    }
  }

  @ApiOperation("Archive or unarchive users")
  @PutMapping("/archive")
  public ResponseEntity<RestResult> archive(@RequestBody RestUsersArchiveBody request)
  {

    ArchiveUsers archiveUsers = new ArchiveUsers(authenticationService, authorizationService, aimRepositoryRegistry);
    try
    {
      if (request == null || request.getUserIds().length == 0)
      {
        return RestResponse.badRequest(INVALID_USER_ID_INPUT);
      }

      List<ArchiveUserInput> input = Arrays.stream(request.getUserIds())
        .map(userId -> new ArchiveUserInput(userId, request.isArchived()))
        .collect(Collectors.toList());

      archiveUsers.execute(input);

      return RestResponse.success(true);
    }
    catch (UseCaseException e)
    {
      LOGGER.error(e.getMessage());
      return RestResponse.success(false);
    }
  }

  @ApiOperation("Get all users by role id")
  @GetMapping("/role/{roleId}")
  public ResponseEntity<RestResult> getAllUserByRole(@PathVariable String roleId)
  {
    if (StringUtils.isBlank(roleId))
    {
      return RestResponse.internalError(INVALID_ROLE_ID_INPUT);
    }

    GetGroupUsersByRole getUsersByRole = new GetGroupUsersByRole(authenticationService, authorizationService, aimRepositoryRegistry, tenantIdProvider);

    List<RestUserResult> response = new ArrayList<>();

    try
    {
      GetUsersByRoleOutput output = getUsersByRole.execute(new GetUsersByRoleInput(roleId));
      Collection<User> users = output.getUsersByRole();

      for (User user : users)
      {
        response.add(toUserResponseModel(getUser(user.getUserId().getId())));
      }

      return RestResponse.success(response);
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Get sub group users by role id")
  @GetMapping("/role/{roleId}/sub-groups")
  public ResponseEntity<RestResult> getSubGroupUsersByRole(@PathVariable String roleId)
  {
    if (StringUtils.isBlank(roleId))
    {
      return RestResponse.internalError(INVALID_ROLE_ID_INPUT);
    }

    GetSubGroupUsersByRole getSubGroupUsersByRole = new GetSubGroupUsersByRole(authenticationService, authorizationService, aimRepositoryRegistry,
      tenantIdProvider);

    List<RestUserResult> response = new ArrayList<>();

    try
    {
      GetSubGroupUsersByRoleOutput output = getSubGroupUsersByRole.execute(new GetSubGroupUsersByRoleInput(roleId));
      Collection<User> users = output.getSubgroupUsers();

      for (User user : users)
      {
        response.add(toUserResponseModel(getUser(user.getUserId().getId())));
      }

      return RestResponse.success(response);
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Get parent group users by role id")
  @GetMapping("/role/{roleId}/parent-groups")
  public ResponseEntity<RestResult> getParentGroupUsersByRole(@PathVariable String roleId)
  {
    if (StringUtils.isBlank(roleId))
    {
      return RestResponse.internalError(INVALID_ROLE_ID_INPUT);
    }

    GetParentGroupUsersByRole getParentGroupUsersByRole = new GetParentGroupUsersByRole(authenticationService, authorizationService,
      aimRepositoryRegistry, tenantIdProvider);

    List<RestUserResult> response = new ArrayList<>();

    try
    {
      GetParentGroupUsersByRoleOutput output = getParentGroupUsersByRole.execute(new GetParentGroupUsersByRoleInput(roleId));
      Collection<User> users = output.getParentGroupUsersByRole();

      for (User user : users)
      {
        response.add(toUserResponseModel(getUser(user.getUserId().getId())));
      }

      return RestResponse.success(response);
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Gets Realm Type")
  @GetMapping("/realm-type")
  public ResponseEntity<RestResult> getRealm()
  {
    GetRealmType getRealmType = new GetRealmType(realmTypeProvider);
    try
    {
      String realmType = getRealmType.execute(null);
      return RestResponse.success(realmType);
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Gets the profile image URL")
  @GetMapping("/get-image-url")
  public ResponseEntity<RestResult> getImageURL(@RequestParam(required = false) String username)
  {
    try
    {
      GetUserProfileImageURL getUserProfileImageURL = new GetUserProfileImageURL(aimRepositoryRegistry, authenticationService);
      return RestResponse.success(getUserProfileImageURL.execute(username));
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  private GetUserOutput getUser(String userId) throws UseCaseException
  {
    GetUser getUser = new GetUser(authenticationService, authorizationService, userAggregateService);
    return getUser.execute(userId);
  }

  private RestUserResult toUserResponseModelFromAggregate(UserAggregate user) throws UseCaseException
  {
    RestUserResult model = new RestUserResult();
    model.setUserId(user.getUserId());
    model.setUsername(user.getUsername());
    model.setFirstName(user.getFirstName());
    model.setLastName(user.getLastName());
    model.setGender(user.getGender());
    model.setBirthday(user.getBirthday());
    model.setJobTitle(user.getJobTitle());
    model.setImageId(user.getImageId());
    model.setEmail(user.getEmail());
    model.setPhoneNumber(user.getPhoneNumber());
    model.setStatus(user.getStatus());
    model.setDateLastModified(user.getLastModified());
    model.setMessage(user.getMessage());
    model.setDeletable(user.isDeletable());
    model.setProperties(user.getProperties());

    GetUserMembershipsInput input = new GetUserMembershipsInput(user.getUsername());
    GetUserMemberships getUserMemberships = new GetUserMemberships(authenticationService, authorizationService,
      aimRepositoryRegistry.getMembershipRepository(), tenantIdProvider);
    List<GetMembershipOutput> memberships = getUserMemberships.execute(input);
    model.setMemberships(toMembershipResponse(memberships));

    return model;
  }

  private RestUserResult toUserResponseModel(GetUserOutput user) throws UseCaseException
  {
    RestUserResult model = new RestUserResult();
    model.setUserId(user.getId());
    model.setUsername(user.getUsername());
    model.setFirstName(user.getFirstName());
    model.setLastName(user.getLastName());
    model.setGender(user.getGender());
    model.setBirthday(user.getBirthday());
    model.setJobTitle(user.getJobTitle());
    model.setImageId(user.getImageId());
    model.setEmail(user.getEmail());
    model.setPhoneNumber(user.getPhoneNumber());
    model.setStatus(user.getStatus());
    model.setDateLastModified(user.getDateLastModified());
    model.setMessage(user.getMessage());
    model.setDeletable(user.isDeletable());
    model.setProperties(user.getProperties());

    GetUserMembershipsInput input = new GetUserMembershipsInput(user.getUsername());
    GetUserMemberships getUserMemberships = new GetUserMemberships(authenticationService, authorizationService,
        aimRepositoryRegistry.getMembershipRepository(), tenantIdProvider);
    List<GetMembershipOutput> memberships = getUserMemberships.execute(input);
    model.setMemberships(toMembershipResponse(memberships));

    return model;
  }


  private List<RestMembership> toMembershipResponse(List<GetMembershipOutput> memberships)
  {
    return memberships.stream()
      .map(membership -> {
        RestMembership gmr = new RestMembership();
        gmr.setMembershipId(membership.getMembershipId());
        gmr.setUserId(membership.getUserId());
        gmr.setGroupId(membership.getGroupId());
        gmr.setRoleId(membership.getRoleId());
        return gmr;
      })
      .collect(Collectors.toList());
  }

  private File saveToFile(MultipartFile file) throws IOException
  {
    if (null == file || file.isEmpty())
    {
      throw new IllegalArgumentException("Attachment file cannot be null or empty!");
    }

    String extension = FilenameUtils.getExtension(file.getOriginalFilename());
    return temporaryFileApi.store(UUID.randomUUID() + "." + extension, file.getBytes());
  }

  private File getImage()
  {
    String currentUsername = authenticationService.getCurrentUsername();
    File file = attachments.get(currentUsername) != null ? attachments.get(currentUsername) : null;
    attachments.remove(currentUsername);
    return file;
  }
}

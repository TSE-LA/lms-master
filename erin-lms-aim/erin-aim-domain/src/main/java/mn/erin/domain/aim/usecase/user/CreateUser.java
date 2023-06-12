package mn.erin.domain.aim.usecase.user;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.InputMismatchException;
import java.util.Objects;

import mn.erin.domain.aim.constant.ValidateUtils;
import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.permission.AimModulePermission;
import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.aim.model.user.User;
import mn.erin.domain.aim.model.user.UserAggregate;
import mn.erin.domain.aim.model.user.UserContact;
import mn.erin.domain.aim.model.user.UserGender;
import mn.erin.domain.aim.model.user.UserId;
import mn.erin.domain.aim.model.user.UserIdentity;
import mn.erin.domain.aim.model.user.UserIdentitySource;
import mn.erin.domain.aim.model.user.UserInfo;
import mn.erin.domain.aim.model.user.UserProfile;
import mn.erin.domain.aim.repository.AimRepositoryRegistry;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.aim.usecase.AuthorizedUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import org.apache.commons.lang3.StringUtils;

import static mn.erin.domain.aim.constant.AimErrorMessageConstant.INVALID_INPUT;
import static mn.erin.domain.aim.constant.AimErrorMessageConstant.INVALID_PHONE_NUMBER_PATTERN;
import static mn.erin.domain.aim.constant.AimErrorMessageConstant.REPOSITORY_REGISTRY_CANNOT_BE_NULL;
import static mn.erin.domain.aim.constant.AimErrorMessageConstant.USERNAME_EXISTS;

/**
 * @author Munkh
 */
public class CreateUser extends AuthorizedUseCase<CreateUserInput, CreateUserOutput>
{
  private static final Permission permission = new AimModulePermission("CreateUser");
  private final AimRepositoryRegistry aimRepositoryRegistry;

  public CreateUser(AuthenticationService authenticationService, AuthorizationService authorizationService, AimRepositoryRegistry aimRepositoryRegistry)
  {
    super(authenticationService, authorizationService);
    this.aimRepositoryRegistry = Objects.requireNonNull(aimRepositoryRegistry, REPOSITORY_REGISTRY_CANNOT_BE_NULL);
  }

  @Override
  public Permission getPermission()
  {
    return permission;
  }

  @Override
  protected CreateUserOutput executeImpl(CreateUserInput input) throws UseCaseException
  {
    if (input == null || StringUtils.isBlank(input.getTenantId()) || StringUtils.isBlank(input.getUsername()) ||
        StringUtils.isBlank(input.getPassword()) || StringUtils.isBlank(input.getEmail()))
    {
      throw new UseCaseException(INVALID_INPUT);
    }

    if(!ValidateUtils.isEmail(input.getEmail()) ||
        !ValidateUtils.isUsernameUnicode(input.getUsername()) || !ValidateUtils.isPassword(input.getPassword()))
    {
      throw new UseCaseException(INVALID_INPUT);
    }

    if(!StringUtils.isBlank(input.getPhoneNumber()) && !ValidateUtils.isPhoneNumber(input.getPhoneNumber()))
    {
      throw  new UseCaseException(INVALID_PHONE_NUMBER_PATTERN);
    }

    if (aimRepositoryRegistry.getUserIdentityRepository().existByUsername(input.getUsername(), UserIdentitySource.OWN))
    {
      throw new UseCaseException(USERNAME_EXISTS);
    }

    UserId userId = null;
    try
    {
      User user = aimRepositoryRegistry.getUserRepository().createUser(TenantId.valueOf(input.getTenantId()));
      userId = user.getUserId();

      // create Identity
      UserIdentity userIdentity = new UserIdentity(userId, input.getUsername(), input.getPassword(), UserIdentitySource.OWN);
      aimRepositoryRegistry.getUserIdentityRepository().create(userIdentity);

      // create profile picture
      String imageId = null;
      String folderName = null;
      if (input.getImage() != null)
      {
        folderName = aimRepositoryRegistry.getAimFileSystem().createUserFolder(userId.getId(), userIdentity.getUsername());
        imageId = aimRepositoryRegistry.getAimFileSystem().uploadAttachment(user.getUserId().getId(), input.getImage());
      }

      LocalDateTime birthday = (new SimpleDateFormat("yyyy-MM-dd").parse(input.getBirthday())).toInstant().atZone(ZoneId.of("Asia/Ulaanbaatar")).toLocalDateTime();

      UserProfile userProfile = new UserProfile(userId,
          new UserInfo(input.getFirstName(), input.getLastName(),
              input.getGender() != null ? UserGender.valueOf(input.getGender().toUpperCase()) : UserGender.NA, birthday,
              input.getJobTitle(), imageId, folderName, input.getProperties()), new UserContact(input.getEmail(), input.getPhoneNumber()));

      // create Profile
      aimRepositoryRegistry.getUserProfileRepository().create(userProfile);

      return new CreateUserOutput(new UserAggregate(user, userIdentity, userProfile));
    }
    catch (AimRepositoryException | InputMismatchException | ParseException e)
    {
      if (userId != null)
      {
        // rollback
        aimRepositoryRegistry.getUserProfileRepository().delete(userId);
        aimRepositoryRegistry.getUserIdentityRepository().delete(userId);
        aimRepositoryRegistry.getUserRepository().delete(userId);
      }
      throw new UseCaseException("Failed to create user", e);
    }
  }
}

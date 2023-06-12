package mn.erin.domain.aim.usecase.user;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;

import mn.erin.domain.aim.constant.ValidateUtils;
import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.permission.AimModulePermission;
import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.model.user.User;
import mn.erin.domain.aim.model.user.UserContact;
import mn.erin.domain.aim.model.user.UserGender;
import mn.erin.domain.aim.model.user.UserId;
import mn.erin.domain.aim.model.user.UserIdentity;
import mn.erin.domain.aim.model.user.UserIdentitySource;
import mn.erin.domain.aim.model.user.UserInfo;
import mn.erin.domain.aim.model.user.UserProfile;
import mn.erin.domain.aim.repository.AimFileSystem;
import mn.erin.domain.aim.repository.AimRepositoryRegistry;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.aim.service.PasswordEncryptService;
import mn.erin.domain.aim.usecase.AuthorizedUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import org.apache.commons.lang3.StringUtils;

import static mn.erin.domain.aim.constant.AimErrorMessageConstant.INVALID_INPUT;
import static mn.erin.domain.aim.constant.AimErrorMessageConstant.INVALID_PHONE_NUMBER_PATTERN;
import static mn.erin.domain.aim.constant.AimErrorMessageConstant.PASSWORD_ENCRYPT_SERVICE_CANNOT_BE_NULL;
import static mn.erin.domain.aim.constant.AimErrorMessageConstant.REPOSITORY_REGISTRY_CANNOT_BE_NULL;
import static mn.erin.domain.aim.constant.AimErrorMessageConstant.USER_IDENTITY_NOT_FOUND;
import static mn.erin.domain.aim.constant.AimErrorMessageConstant.USER_NOT_FOUND;
import static mn.erin.domain.aim.constant.AimErrorMessageConstant.USER_PROFILE_NOT_FOUND;

/**
 * @author Munkh
 */
public class UpdateUser extends AuthorizedUseCase<UpdateUserInput, Boolean>
{
  private static final Permission permission = new AimModulePermission("UpdateUser");
  private final AimRepositoryRegistry aimRepositoryRegistry;
  private final PasswordEncryptService passwordEncryptService;

  public UpdateUser(AuthenticationService authenticationService, AuthorizationService authorizationService,
      AimRepositoryRegistry aimRepositoryRegistry, PasswordEncryptService passwordEncryptService)
  {
    super(authenticationService, authorizationService);
    this.aimRepositoryRegistry = Objects.requireNonNull(aimRepositoryRegistry, REPOSITORY_REGISTRY_CANNOT_BE_NULL);
    this.passwordEncryptService = Objects.requireNonNull(passwordEncryptService, PASSWORD_ENCRYPT_SERVICE_CANNOT_BE_NULL);
  }

  @Override
  public Permission getPermission()
  {
    return permission;
  }

  @Override
  protected Boolean executeImpl(UpdateUserInput input) throws UseCaseException
  {

    if (input == null || StringUtils.isBlank(input.getUserId()) || StringUtils.isBlank(input.getEmail()))
    {
      throw new UseCaseException(INVALID_INPUT);
    }

    User user = aimRepositoryRegistry.getUserRepository().findById(UserId.valueOf(input.getUserId()));
    if (user == null)
    {
      throw new UseCaseException(USER_NOT_FOUND);
    }

    UserIdentity userIdentity = aimRepositoryRegistry.getUserIdentityRepository().getIdentity(user.getUserId(), UserIdentitySource.OWN);
    if (userIdentity == null)
    {
      throw new UseCaseException(USER_IDENTITY_NOT_FOUND);
    }

    UserProfile userProfile = aimRepositoryRegistry.getUserProfileRepository().findByUserId(user.getUserId());
    if (userProfile == null)
    {
      throw new UseCaseException(USER_PROFILE_NOT_FOUND);
    }

    // Update Identity
    if (!StringUtils.isBlank(input.getUsername()))
    {
      userIdentity.setUsername(input.getUsername());
    }
    if (!StringUtils.isBlank(input.getPassword()))
    {
      userIdentity.setPassword(passwordEncryptService.encrypt(input.getPassword()));
    }

    try
    {
      aimRepositoryRegistry.getUserIdentityRepository().update(userIdentity);

      if(!ValidateUtils.isEmail(input.getEmail()) ||
          !StringUtils.isBlank(input.getNewPassword()) && !ValidateUtils.isPassword(input.getNewPassword()))
      {
        throw new UseCaseException(INVALID_INPUT);
      }

      if(!StringUtils.isBlank(input.getPhoneNumber()) && !ValidateUtils.isPhoneNumber(input.getPhoneNumber()))
      {
        throw  new UseCaseException(INVALID_PHONE_NUMBER_PATTERN);
      }

      String imageId = null;
      String folderName = null;
      if (input.getImage() != null)
      {
        AimFileSystem aimFileSystem = aimRepositoryRegistry.getAimFileSystem();

        if (aimFileSystem.getUserFolderId(user.getUserId().getId()) == null)
        {
          folderName = aimFileSystem.createUserFolder(user.getUserId().getId(), userIdentity.getUsername());
        }

        if (aimFileSystem.getUserFolderId(user.getUserId().getId()) == null)
        {
          throw new UseCaseException("User folder not found!");
        }
        // Delete old profile image
        if (userProfile.getUserInfo() != null && userProfile.getUserInfo().getImageId() != null)
        {
          aimFileSystem.deleteAttachment(user.getUserId().getId(), userProfile.getUserInfo().getImageId());
        }
        // Create profile image
        imageId = aimFileSystem.uploadAttachment(user.getUserId().getId(), input.getImage());
      }

      LocalDateTime birthday = (new SimpleDateFormat("yyyy-MM-dd").parse(input.getBirthday())).toInstant().atZone(ZoneId.of("Asia/Ulaanbaatar")).toLocalDateTime();

      // Update profile
      aimRepositoryRegistry.getUserProfileRepository().update(new UserProfile(user.getUserId(),
          new UserInfo(
              input.getFirstName(),
              input.getLastName(),
              input.getGender() != null ? UserGender.valueOf(input.getGender().toUpperCase()) : UserGender.NA,
              birthday,
              input.getJobTitle(),
              imageId != null ? imageId : userProfile.getUserInfo().getImageId(),
              folderName != null ? folderName : userProfile.getUserInfo().getFolderName(),
              input.getProperties()),
          new UserContact(input.getEmail(), input.getPhoneNumber())));

      return true;
    }
    catch (AimRepositoryException | UseCaseException | ParseException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }
  }
}

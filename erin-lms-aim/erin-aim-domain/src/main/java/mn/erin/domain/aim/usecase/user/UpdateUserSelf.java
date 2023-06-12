/*
 * (C)opyright, 2021, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.aim.usecase.user;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;

import io.swagger.annotations.Api;
import mn.erin.domain.aim.constant.ValidateUtils;
import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.user.User;
import mn.erin.domain.aim.model.user.UserContact;
import mn.erin.domain.aim.model.user.UserGender;
import mn.erin.domain.aim.model.user.UserIdentity;
import mn.erin.domain.aim.model.user.UserIdentitySource;
import mn.erin.domain.aim.model.user.UserInfo;
import mn.erin.domain.aim.model.user.UserProfile;
import mn.erin.domain.aim.repository.AimFileSystem;
import mn.erin.domain.aim.repository.AimRepositoryRegistry;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.PasswordEncryptService;
import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static mn.erin.domain.aim.constant.AimErrorMessageConstant.INPUT_CANNOT_BE_NULL;
import static mn.erin.domain.aim.constant.AimErrorMessageConstant.INVALID_EMAIL_PATTERN;
import static mn.erin.domain.aim.constant.AimErrorMessageConstant.INVALID_GENDER;
import static mn.erin.domain.aim.constant.AimErrorMessageConstant.INVALID_PHONE_NUMBER_PATTERN;
import static mn.erin.domain.aim.constant.AimErrorMessageConstant.PASSWORD_ENCRYPT_SERVICE_CANNOT_BE_NULL;
import static mn.erin.domain.aim.constant.AimErrorMessageConstant.REPOSITORY_REGISTRY_CANNOT_BE_NULL;
import static mn.erin.domain.aim.constant.AimErrorMessageConstant.USER_IDENTITY_NOT_CORRECT;
import static mn.erin.domain.aim.constant.AimErrorMessageConstant.USER_IDENTITY_NOT_FOUND;
import static mn.erin.domain.aim.constant.AimErrorMessageConstant.USER_NOT_FOUND;
import static mn.erin.domain.aim.constant.AimErrorMessageConstant.USER_PROFILE_NOT_FOUND;

/**
 * @author Temuulen 2021
 */
@Api("User")
@RequestMapping(value = "/aim/users", name = "Provides AIM user management features")
@RestController
public class UpdateUserSelf implements UseCase<UpdateUserInput, Boolean>
{
  private final AimRepositoryRegistry aimRepositoryRegistry;
  private final PasswordEncryptService passwordEncryptService;
  private final AuthenticationService authenticationService;

  public UpdateUserSelf(AuthenticationService authenticationService, AimRepositoryRegistry aimRepositoryRegistry, PasswordEncryptService passwordEncryptService)
  {
    this.authenticationService = Objects.requireNonNull(authenticationService, "Authorization service is required!");
    this.aimRepositoryRegistry = Objects.requireNonNull(aimRepositoryRegistry, REPOSITORY_REGISTRY_CANNOT_BE_NULL);
    this.passwordEncryptService = Objects.requireNonNull(passwordEncryptService, PASSWORD_ENCRYPT_SERVICE_CANNOT_BE_NULL);
  }


  @Override
  public Boolean execute(UpdateUserInput input) throws UseCaseException
  {
    if (input == null)
    {
      throw new UseCaseException(INPUT_CANNOT_BE_NULL);
    }

    if (!StringUtils.isBlank(input.getEmail()) && !ValidateUtils.isEmail(input.getEmail()))
    {
      throw new UseCaseException(INVALID_EMAIL_PATTERN);
    }

    if (!StringUtils.isBlank(input.getPhoneNumber()) && !ValidateUtils.isPhoneNumber(input.getPhoneNumber()))
    {
      throw new UseCaseException(INVALID_PHONE_NUMBER_PATTERN);
    }

    try
    {
      UserGender.valueOf(input.getGender());
    }
    catch (NullPointerException | IllegalArgumentException ignored)
    {
      throw new UseCaseException(INVALID_GENDER);
    }

    String currentUserName = authenticationService.getCurrentUsername();

    if (!currentUserName.equals(input.getUsername()))
    {
      throw new UseCaseException(USER_IDENTITY_NOT_CORRECT);
    }

    UserIdentity userIdentity = aimRepositoryRegistry.getUserIdentityRepository().getUserIdentityByUsername(currentUserName, UserIdentitySource.OWN);
    if (userIdentity == null)
    {
      throw new UseCaseException(USER_IDENTITY_NOT_FOUND);
    }

    User user = aimRepositoryRegistry.getUserRepository().findById(userIdentity.getUserId());
    if (user == null)
    {
      throw new UseCaseException(USER_NOT_FOUND);
    }

    UserProfile userProfile = aimRepositoryRegistry.getUserProfileRepository().findByUserId(user.getUserId());
    if (userProfile == null)
    {
      throw new UseCaseException(USER_PROFILE_NOT_FOUND);
    }

    if (!StringUtils.isBlank(input.getNewPassword()))
    {
      // TODO: validate old password
      userIdentity.setPassword(passwordEncryptService.encrypt(input.getNewPassword()));
    }

    try
    {

      LocalDateTime birthday = (new SimpleDateFormat("yyyy-MM-dd").parse(input.getBirthday())).toInstant().atZone(ZoneId.of("Asia/Ulaanbaatar")).toLocalDateTime();

      // Upload profile image
      String imageId = null;
      String folderName = null;
      if (input.getImage() != null)
      {
        AimFileSystem aimFileSystem = aimRepositoryRegistry.getAimFileSystem();

        if (aimFileSystem.getUserFolderId(user.getUserId().getId()) == null)
        {
          folderName = aimFileSystem.createUserFolder(user.getUserId().getId(), currentUserName);
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

      // Updates identity
      aimRepositoryRegistry.getUserIdentityRepository().update(userIdentity);

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
    catch (AimRepositoryException | ParseException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }
  }
}

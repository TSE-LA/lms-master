package mn.erin.domain.aim.usecase.user;

import org.apache.commons.lang3.StringUtils;

import mn.erin.domain.aim.model.user.UserIdentity;
import mn.erin.domain.aim.model.user.UserIdentitySource;
import mn.erin.domain.aim.model.user.UserProfile;
import mn.erin.domain.aim.repository.AimRepositoryRegistry;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;

import static mn.erin.domain.aim.constant.AimErrorMessageConstant.USER_IDENTITY_NOT_FOUND;
import static mn.erin.domain.aim.constant.AimErrorMessageConstant.USER_PROFILE_NOT_FOUND;

/**
 * @author Temuulen Naranbold
 */
public class GetUserProfileImageURL implements UseCase<String, String>
{
  private final AimRepositoryRegistry aimRepositoryRegistry;
  private final AuthenticationService authenticationService;

  public GetUserProfileImageURL(AimRepositoryRegistry aimRepositoryRegistry, AuthenticationService authenticationService)
  {
    this.aimRepositoryRegistry = aimRepositoryRegistry;
    this.authenticationService = authenticationService;
  }

  @Override
  public String execute(String username) throws UseCaseException
  {
    String currentUsername = StringUtils.isBlank(username) ? authenticationService.getCurrentUsername() : username;
    UserIdentity userIdentity = aimRepositoryRegistry.getUserIdentityRepository().getUserIdentityByUsername(currentUsername, UserIdentitySource.OWN);
    if (userIdentity == null)
    {
      throw new UseCaseException(USER_IDENTITY_NOT_FOUND);
    }

    UserProfile userProfile = aimRepositoryRegistry.getUserProfileRepository().findByUserId(userIdentity.getUserId());
    if (userProfile == null)
    {
      throw new UseCaseException(USER_PROFILE_NOT_FOUND);
    }

    return userProfile.getUserInfo().getImageURL();
  }
}

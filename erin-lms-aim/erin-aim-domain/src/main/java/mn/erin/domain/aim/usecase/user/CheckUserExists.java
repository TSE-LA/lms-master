package mn.erin.domain.aim.usecase.user;

import mn.erin.domain.aim.model.permission.AimModulePermission;
import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.model.user.UserIdentitySource;
import mn.erin.domain.aim.repository.AimRepositoryRegistry;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.aim.usecase.AuthorizedUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import org.apache.commons.lang3.StringUtils;

import static mn.erin.domain.aim.constant.AimErrorMessageConstant.INVALID_USERNAME_INPUT;

/**
 * @author Munkh
 */
public class CheckUserExists extends AuthorizedUseCase<String, Boolean>
{
  private static final Permission permission = new AimModulePermission("CheckUserExists");
  private final AimRepositoryRegistry aimRepositoryRegistry;

  public CheckUserExists(AuthenticationService authenticationService,
      AuthorizationService authorizationService, AimRepositoryRegistry aimRepositoryRegistry)
  {
    super(authenticationService, authorizationService);
    this.aimRepositoryRegistry = aimRepositoryRegistry;
  }

  @Override
  public Permission getPermission()
  {
    return permission;
  }

  @Override
  protected Boolean executeImpl(String username) throws UseCaseException
  {
    if (StringUtils.isBlank(username))
    {
      throw new UseCaseException(INVALID_USERNAME_INPUT);
    }

    return aimRepositoryRegistry.getUserIdentityRepository().existByUsername(username, UserIdentitySource.OWN);
  }
}

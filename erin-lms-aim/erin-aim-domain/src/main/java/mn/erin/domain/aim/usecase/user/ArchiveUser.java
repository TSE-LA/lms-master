package mn.erin.domain.aim.usecase.user;

import java.util.Objects;

import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.permission.AimModulePermission;
import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.model.user.UserId;
import mn.erin.domain.aim.model.user.UserStateChangeSource;
import mn.erin.domain.aim.model.user.UserStatus;
import mn.erin.domain.aim.repository.AimRepositoryRegistry;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.aim.usecase.AuthorizedUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import org.apache.commons.lang3.StringUtils;

import static mn.erin.domain.aim.constant.AimErrorMessageConstant.INVALID_INPUT;
import static mn.erin.domain.aim.constant.AimErrorMessageConstant.REPOSITORY_REGISTRY_CANNOT_BE_NULL;
import static mn.erin.domain.aim.constant.AimErrorMessageConstant.USER_STATE_NOT_FOUND;

/**
 * @author Munkh
 */
public class ArchiveUser extends AuthorizedUseCase<ArchiveUserInput, Void>
{
  private static final Permission permission = new AimModulePermission("ArchiveUser");
  private final AimRepositoryRegistry aimRepositoryRegistry;

  public ArchiveUser(AuthenticationService authenticationService, AuthorizationService authorizationService,
      AimRepositoryRegistry aimRepositoryRegistry)
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
  protected Void executeImpl(ArchiveUserInput input) throws UseCaseException
  {
    if (input == null || StringUtils.isBlank(input.getUserId()))
    {
      throw new UseCaseException(INVALID_INPUT);
    }
    if (!aimRepositoryRegistry.getUserRepository().doesExistById(UserId.valueOf(input.getUserId())))
    {
      throw new UseCaseException(USER_STATE_NOT_FOUND);
    }

    try
    {
      aimRepositoryRegistry.getUserRepository().changeState(
        UserId.valueOf(input.getUserId()),
        input.isArchived() ? UserStatus.ACTIVE : UserStatus.ARCHIVED,
        UserStateChangeSource.ADMIN);
    }
    catch (AimRepositoryException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }

    return null;
  }
}

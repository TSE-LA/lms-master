package mn.erin.domain.aim.usecase.user;

import java.util.List;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static mn.erin.domain.aim.constant.AimErrorMessageConstant.INVALID_INPUT;
import static mn.erin.domain.aim.constant.AimErrorMessageConstant.REPOSITORY_REGISTRY_CANNOT_BE_NULL;
import static mn.erin.domain.aim.constant.AimErrorMessageConstant.USER_NOT_FOUND;

/**
 * @author Munkh
 */
public class ArchiveUsers extends AuthorizedUseCase<List<ArchiveUserInput>, Boolean>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(ArchiveUsers.class);
  private static final Permission permission = new AimModulePermission("ArchiveUsers");
  private final AimRepositoryRegistry aimRepositoryRegistry;

  public ArchiveUsers(AuthenticationService authenticationService, AuthorizationService authorizationService,
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
  protected Boolean executeImpl(List<ArchiveUserInput> input) throws UseCaseException
  {
    if (input == null || input.isEmpty())
    {
      throw new UseCaseException(INVALID_INPUT);
    }

    for (ArchiveUserInput archiveInput : input)
    {
      if (!aimRepositoryRegistry.getUserRepository().doesExistById(UserId.valueOf(archiveInput.getUserId())))
      {
        LOGGER.warn(USER_NOT_FOUND);
        continue;
      }

      try
      {
        aimRepositoryRegistry.getUserRepository().changeState(
          UserId.valueOf(archiveInput.getUserId()),
          archiveInput.isArchived() ? UserStatus.ACTIVE : UserStatus.ARCHIVED,
          UserStateChangeSource.ADMIN);
      }
      catch (AimRepositoryException e)
      {
        LOGGER.error(e.getMessage());
      }
    }

    return true;
  }
}

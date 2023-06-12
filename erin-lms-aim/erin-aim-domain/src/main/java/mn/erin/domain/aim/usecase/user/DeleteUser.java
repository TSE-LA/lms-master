package mn.erin.domain.aim.usecase.user;

import java.util.Objects;

import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.membership.Membership;
import mn.erin.domain.aim.model.permission.AimModulePermission;
import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.model.user.User;
import mn.erin.domain.aim.model.user.UserId;
import mn.erin.domain.aim.model.user.UserIdentity;
import mn.erin.domain.aim.model.user.UserIdentitySource;
import mn.erin.domain.aim.model.user.UserInfo;
import mn.erin.domain.aim.model.user.UserProfile;
import mn.erin.domain.aim.repository.AimRepositoryRegistry;
import mn.erin.domain.aim.repository.MembershipRepository;
import mn.erin.domain.aim.service.AimApplicationDataChecker;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.aim.service.UserDataResult;
import mn.erin.domain.aim.usecase.AuthorizedUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static mn.erin.domain.aim.constant.AimErrorMessageConstant.INVALID_INPUT;
import static mn.erin.domain.aim.constant.AimErrorMessageConstant.REPOSITORY_REGISTRY_CANNOT_BE_NULL;

/**
 * @author Munkh
 */
public class DeleteUser extends AuthorizedUseCase<DeleteUserInput, Boolean>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(DeleteUser.class);
  private static final Permission permission = new AimModulePermission("DeleteUser");

  private final AimRepositoryRegistry aimRepositoryRegistry;
  private final MembershipRepository membershipRepository;
  private final AimApplicationDataChecker dataChecker;

  public DeleteUser(AuthenticationService authenticationService, AuthorizationService authorizationService,
    AimRepositoryRegistry aimRepositoryRegistry, AimApplicationDataChecker dataChecker)
  {
    super(authenticationService, authorizationService);
    this.aimRepositoryRegistry = Objects.requireNonNull(aimRepositoryRegistry, REPOSITORY_REGISTRY_CANNOT_BE_NULL);
    membershipRepository = aimRepositoryRegistry.getMembershipRepository();
    this.dataChecker = dataChecker;
  }

  @Override
  public Permission getPermission()
  {
    return permission;
  }

  @Override
  protected Boolean executeImpl(DeleteUserInput input) throws UseCaseException
  {
    if (input == null || StringUtils.isBlank(input.getUserId()))
    {
      throw new UseCaseException(INVALID_INPUT);
    }

    UserIdentity identity = aimRepositoryRegistry.getUserIdentityRepository().getIdentity(UserId.valueOf(input.getUserId()), UserIdentitySource.OWN);
    UserProfile profile = aimRepositoryRegistry.getUserProfileRepository().findByUserId(identity.getUserId());
    UserDataResult result = dataChecker.hasAssociatedData(identity.getUsername());
    if (result.affirmative) // has user associated data
    {
      throw new UseCaseException("User cannot be deleted [" + result.getMessage() + "]");
    }

    User user = aimRepositoryRegistry.getUserRepository().findById(UserId.valueOf(input.getUserId()));
    try
    {
      Membership membership = membershipRepository.findByUsername(identity.getUsername());
      membershipRepository.delete(membership.getMembershipId());
    }
    catch (AimRepositoryException e)
    {
      LOGGER.warn("Membership of a user with ID: [{}] not found.", user.getUserId());
    }

    UserInfo info = profile != null ? profile.getUserInfo() : null;

    if (info.getFolderName() != null)
    {
      aimRepositoryRegistry.getAimFileSystem().deleteUserFolder(user.getUserId().getId());
    }
    aimRepositoryRegistry.getUserProfileRepository().delete(user.getUserId());
    aimRepositoryRegistry.getUserIdentityRepository().delete(identity);
    aimRepositoryRegistry.getUserRepository().delete(user.getUserId());

    return true;
  }
}

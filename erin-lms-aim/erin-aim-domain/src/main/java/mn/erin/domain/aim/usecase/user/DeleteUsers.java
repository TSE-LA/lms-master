package mn.erin.domain.aim.usecase.user;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static mn.erin.domain.aim.constant.AimErrorMessageConstant.INVALID_INPUT;
import static mn.erin.domain.aim.constant.AimErrorMessageConstant.REPOSITORY_REGISTRY_CANNOT_BE_NULL;

/**
 * @author Munkh
 */
public class DeleteUsers extends AuthorizedUseCase<List<DeleteUserInput>, Boolean>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(DeleteUsers.class);
  private static final Permission permission = new AimModulePermission("DeleteUsers");

  private final AimRepositoryRegistry aimRepositoryRegistry;
  private final MembershipRepository membershipRepository;
  private final AimApplicationDataChecker dataChecker;

  public DeleteUsers(AuthenticationService authenticationService, AuthorizationService authorizationService,
    AimRepositoryRegistry aimRepositoryRegistry, AimApplicationDataChecker dataChecker)
  {
    super(authenticationService, authorizationService);
    this.aimRepositoryRegistry = Objects.requireNonNull(aimRepositoryRegistry, REPOSITORY_REGISTRY_CANNOT_BE_NULL);
    this.membershipRepository = aimRepositoryRegistry.getMembershipRepository();
    this.dataChecker = dataChecker;
  }

  @Override
  public Permission getPermission()
  {
    return permission;
  }

  @Override
  protected Boolean executeImpl(List<DeleteUserInput> input) throws UseCaseException
  {
    if (input == null || input.isEmpty())
    {
      throw new UseCaseException(INVALID_INPUT);
    }

    Map<String, String> usernamesByID = new HashMap<>();
    Set<String> usersWithData = new LinkedHashSet<>();


    // check all user associated data
    for (DeleteUserInput deleteUser : input)
    {
      UserIdentity identity = aimRepositoryRegistry.getUserIdentityRepository().getIdentity(UserId.valueOf(deleteUser.getUserId()), UserIdentitySource.OWN);
      UserDataResult result = dataChecker.hasAssociatedData(identity.getUsername());
      if (result.affirmative)
      {
        usersWithData.add(identity.getUsername());
      }
      usernamesByID.put(deleteUser.getUserId(), identity.getUsername());
    }

    if (!usersWithData.isEmpty())
    {
      throw new UseCaseException("User(s) cannot be deleted " + usersWithData);
    }

    for (Map.Entry<String, String> usernameID : usernamesByID.entrySet())
    {
      User user = aimRepositoryRegistry.getUserRepository().findById(UserId.valueOf(usernameID.getKey()));
      try
      {
        Membership membership = membershipRepository.findByUsername(usernameID.getValue());
        membershipRepository.delete(membership.getMembershipId());
      }
      catch (AimRepositoryException e)
      {
        LOGGER.warn("Membership of a user with ID: [{}] not found.", user.getUserId());
      }
      UserProfile profile = aimRepositoryRegistry.getUserProfileRepository().findByUserId(user.getUserId());
      UserInfo info = profile != null ? profile.getUserInfo() : null;
      String imageId = info != null ? info.getImageId() : null;

      if (imageId != null)
      {
        aimRepositoryRegistry.getAimFileSystem().deleteAttachment(user.getUserId().getId(), imageId);
      }

      aimRepositoryRegistry.getUserProfileRepository().delete(user.getUserId());
      aimRepositoryRegistry.getUserIdentityRepository().delete(user.getUserId());
      aimRepositoryRegistry.getUserRepository().delete(user.getUserId());
    }

    return true;
  }
}

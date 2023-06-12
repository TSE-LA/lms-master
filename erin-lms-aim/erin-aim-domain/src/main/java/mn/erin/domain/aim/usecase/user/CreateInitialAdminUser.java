package mn.erin.domain.aim.usecase.user;

import java.util.List;

import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.group.Group;
import mn.erin.domain.aim.model.user.User;
import mn.erin.domain.aim.model.user.UserId;
import mn.erin.domain.aim.model.user.UserIdentity;
import mn.erin.domain.aim.model.user.UserIdentitySource;
import mn.erin.domain.aim.model.user.UserInfo;
import mn.erin.domain.aim.model.user.UserProfile;
import mn.erin.domain.aim.repository.AimRepositoryRegistry;
import mn.erin.domain.aim.repository.GroupRepository;
import mn.erin.domain.aim.repository.MembershipRepository;
import mn.erin.domain.aim.repository.UserIdentityRepository;
import mn.erin.domain.aim.repository.UserProfileRepository;
import mn.erin.domain.aim.repository.UserRepository;
import mn.erin.domain.aim.service.AimConfigProvider;
import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateInitialAdminUser extends AbstractUseCase<Void, Void>
{
  private static final String DEFAULT_ADMIN_USERNAME = "admin";
  private static final Logger LOGGER = LoggerFactory.getLogger(CreateInitialAdminUser.class);

  private final UserRepository userRepo;
  private final UserIdentityRepository identityRepo;
  private final UserProfileRepository profileRepo;

  private final GroupRepository groupRepo;
  private final MembershipRepository membershipRepo;

  private final AimConfigProvider config;

  public CreateInitialAdminUser(AimRepositoryRegistry registry, AimConfigProvider config)
  {
    this.userRepo = registry.getUserRepository();
    this.identityRepo = registry.getUserIdentityRepository();
    this.profileRepo = registry.getUserProfileRepository();
    this.groupRepo = registry.getGroupRepository();
    this.membershipRepo = registry.getMembershipRepository();
    this.config = config;
  }

  @Override
  public Void execute(Void input) throws UseCaseException
  {
    if (identityRepo.existByUsername(DEFAULT_ADMIN_USERNAME, UserIdentitySource.OWN))
    {
      return null;
    }
    UserId userId = null;
    try
    {
      LOGGER.info("Creating application admin");
      User adminUser = userRepo.createUser(config.getDefaultTenantId());
      userId = adminUser.getUserId();
      identityRepo.create(new UserIdentity(userId, DEFAULT_ADMIN_USERNAME, config.getDefaultPassword()));
      profileRepo.create(new UserProfile(userId, new UserInfo("Administrator")));

      // create root group
      List<Group> rootGroups = groupRepo.getAllRootGroups(config.getDefaultTenantId());
      Group group;
      if (rootGroups.isEmpty())
      {
        group = groupRepo.createGroup(config.getRootGroupName(), null, config.getRootGroupName(), config.getDefaultTenantId());
      }
      else
      {
        group = rootGroups.get(0);
      }

      // create membership
      try
      {
        membershipRepo.findByUsername(DEFAULT_ADMIN_USERNAME);
      }
      catch (AimRepositoryException ignored)
      {
        membershipRepo.create(DEFAULT_ADMIN_USERNAME, group.getId(), config.getAdminRoleId(), adminUser.getTenantId());
      }
    }
    catch (AimRepositoryException e)
    {
      if (userId != null)
      {
        profileRepo.delete(userId);
        identityRepo.delete(userId);
        userRepo.delete(userId);
      }
      throw new UseCaseException("Failed to create admin user", e);
    }
    return null;
  }
}

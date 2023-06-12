package mn.erin.lms.base.analytics.service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.aim.model.user.UserIdentity;
import mn.erin.domain.aim.repository.UserIdentityRepository;
import mn.erin.domain.aim.service.AimConfigProvider;

/**
 * @author Munkh
 */
public class UserServiceImpl implements UserService
{
  private static final Logger LOGGER = LoggerFactory.getLogger("performance");
  private List<UserIdentity> identities = new ArrayList<>();
  private Set<String> usernames = new HashSet<>();

  private final UserIdentityRepository userIdentityRepository;
  private final AimConfigProvider aimConfigProvider;

  public UserServiceImpl(UserIdentityRepository userIdentityRepository, AimConfigProvider aimConfigProvider)
  {
    this.userIdentityRepository = userIdentityRepository;
    this.aimConfigProvider = aimConfigProvider;
  }

  @Override
  @Scheduled(fixedDelay = 1000 * 60 * 30/*30 MIN*/, initialDelay = 0)
  public void overwriteUsers()
  {
    TenantId tenantId = aimConfigProvider.getDefaultTenantId();
    if (tenantId != null)
    {
      // todo: make this to get users by tenant after tenant update
      Instant start = Instant.now();
      this.identities = new ArrayList<>(userIdentityRepository.findAll());
      Instant end = Instant.now();
      LOGGER.warn("Getting all users: [{}]", Duration.between(start, end));
      this.usernames = identities.stream().map(UserIdentity::getUsername).collect(Collectors.toSet());
    }
  }

  @Override
  public List<UserIdentity> getIdentities()
  {
    return Collections.unmodifiableList(identities);
  }

  @Override
  public boolean exists(String username)
  {
    return usernames.contains(username);
  }

  @Override
  public void cleanup()
  {
    identities.clear();
    usernames.clear();
  }
}

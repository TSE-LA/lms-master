package mn.erin.aim.repository.memory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.user.UserId;
import mn.erin.domain.aim.model.user.UserIdentity;
import mn.erin.domain.aim.model.user.UserIdentitySource;
import mn.erin.domain.aim.repository.UserIdentityRepository;
import mn.erin.domain.base.model.EntityId;
import org.springframework.stereotype.Repository;

import static mn.erin.domain.aim.constant.AimErrorMessageConstant.USERNAME_EXISTS;
import static mn.erin.domain.aim.constant.AimErrorMessageConstant.USER_IDENTITY_NOT_FOUND;

/**
 * @author Munkh
 */
@Repository
public class MemoryUserIdentityRepository implements UserIdentityRepository
{
  private static final String DEFAULT_PASSWORD = "$2a$10$llw0G6IyibUob8h5XRt9xuvm40mZh.VPZ9b2ptXerwKC0quCydLPC";
  private static List<UserIdentity> identities = new ArrayList<>();

  private static final UserIdentity EBANK = new UserIdentity(UserId.valueOf("ebank"), "ebank", DEFAULT_PASSWORD, UserIdentitySource.OWN);
  private static final UserIdentity ADMIN = new UserIdentity(UserId.valueOf("admin"), "admin", DEFAULT_PASSWORD, UserIdentitySource.OWN);
  private static final UserIdentity TEST_USER = new UserIdentity(UserId.valueOf("testuser"), "testuser", DEFAULT_PASSWORD, UserIdentitySource.OWN);

  static
  {
    identities.add(EBANK);
    identities.add(ADMIN);
    identities.add(TEST_USER);
  }

  public MemoryUserIdentityRepository()
  {
    // system always
  }

  @Override
  public UserIdentity findById(EntityId entityId)
  {
    return identities.stream().filter(identity -> identity.getId().equals(entityId)).findFirst().orElse(null);
  }

  @Override
  public Collection<UserIdentity> findAll()
  {
    return identities;
  }

  @Override
  public void create(UserIdentity userIdentity) throws AimRepositoryException
  {
    // Unique
    if (find(userIdentity.getUserId().getId(), userIdentity.getUserIdentitySource()) != null)
    {
      throw new AimRepositoryException(USERNAME_EXISTS);
    }
    identities.add(userIdentity);
  }

  @Override
  public void update(UserIdentity userIdentity) throws AimRepositoryException
  {
    UserIdentity updatingIdentity = find(userIdentity.getUserId());

    if (updatingIdentity == null)
    {
      throw new AimRepositoryException(USER_IDENTITY_NOT_FOUND);
    }
    updatingIdentity.setUsername(userIdentity.getUsername());
    updatingIdentity.setPassword(userIdentity.getPassword());
    updatingIdentity.setUserIdentitySource(userIdentity.getUserIdentitySource());
  }

  @Override
  public boolean delete(UserId userId)
  {
    identities.removeIf(userIdentity -> userIdentity.getUserId().sameValueAs(userId));
    return true;
  }

  @Override
  public void delete(UserIdentity userIdentity)
  {
    identities.remove(userIdentity);
  }

  @Override
  public UserIdentity getUserIdentityByUsername(String username, UserIdentitySource... sources)
  {
    if (sources == null)
    {
      sources = UserIdentitySource.values();
    }
    return Arrays.stream(sources)
      .map(source -> find(username, source))
      .filter(Objects::nonNull)
      .findFirst()
      .orElse(null);
  }

  @Override
  public List<UserIdentity> getAllBySource(UserIdentitySource userIdentitySource)
  {
    return identities.stream().filter(identity -> identity.getUserIdentitySource() == userIdentitySource).collect(Collectors.toList());
  }

  @Override
  public UserIdentity getIdentity(UserId userId)
  {
    return getIdentity(userId, UserIdentitySource.OWN);
  }

  @Override
  public UserIdentity getIdentity(UserId userId, UserIdentitySource userIdentitySource)
  {
    for (UserIdentity userIdentity : identities)
    {
      if (userIdentity.getUserId().sameValueAs(userId) && userIdentity.getUserIdentitySource().equals(userIdentitySource))
      {
        return userIdentity;
      }
    }
    return null;
  }

  @Override
  public boolean existByUsername(String username, UserIdentitySource source)
  {
    return find(username, source) != null;
  }

  private UserIdentity find(String username, UserIdentitySource userIdentitySource)
  {
    for (UserIdentity userIdentity : identities)
    {
      if (userIdentity.getUsername().equalsIgnoreCase(username) && userIdentity.getUserIdentitySource().equals(userIdentitySource))
      {
        return userIdentity;
      }
    }
    return null;
  }

  private UserIdentity find(UserId userId)
  {
    for (UserIdentity userIdentity : identities)
    {
      if (userIdentity.getUserId().sameValueAs(userId))
      {
        return userIdentity;
      }
    }
    return null;
  }
}

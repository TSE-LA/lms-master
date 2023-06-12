package mn.erin.aim.repository.memory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.user.UserContact;
import mn.erin.domain.aim.model.user.UserId;
import mn.erin.domain.aim.model.user.UserInfo;
import mn.erin.domain.aim.model.user.UserProfile;
import mn.erin.domain.aim.model.user.UserProfileId;
import mn.erin.domain.aim.repository.UserProfileRepository;
import mn.erin.domain.base.model.EntityId;
import org.springframework.stereotype.Repository;

import static mn.erin.domain.aim.constant.AimErrorMessageConstant.USER_PROFILE_EXISTS;
import static mn.erin.domain.aim.constant.AimErrorMessageConstant.USER_PROFILE_NOT_FOUND;

/**
 * @author Munkh
 */
@Repository
public class MemoryUserProfileRepository implements UserProfileRepository
{
  private static List<UserProfile> profiles = new ArrayList<>();

  private static final UserProfile EBANK =
    new UserProfile(new UserProfileId(UUID.randomUUID().toString()), UserId.valueOf("ebank"),
      new UserContact("ebank@erin.systems", null));
  private static final UserProfile ADMIN =
    new UserProfile(new UserProfileId(UUID.randomUUID().toString()), UserId.valueOf("admin"),
      new UserInfo("first", "last"),
      new UserContact("admin@erin.systems", "-1"));
  private static final UserProfile TEST_USER = new
    UserProfile(new UserProfileId(UUID.randomUUID().toString()), UserId.valueOf("testuser"),
    new UserInfo("tester", "user"),
    new UserContact("testuser@erin.systems", "99xxxxxx"));

  static
  {
    profiles.add(EBANK);
    profiles.add(ADMIN);
    profiles.add(TEST_USER);
  }

  public MemoryUserProfileRepository()
  {
    // system always
  }

  @Override
  public void create(UserProfile userProfile) throws AimRepositoryException
  {
    if (findByUserId(userProfile.getUserId()) != null)
    {
      throw new AimRepositoryException(USER_PROFILE_EXISTS);
    }
    profiles.add(userProfile);
  }

  @Override
  public void update(UserProfile userProfile) throws AimRepositoryException
  {
    if (findByUserId(userProfile.getUserId()) == null)
    {
      throw new AimRepositoryException(USER_PROFILE_NOT_FOUND);
    }
    delete(userProfile.getUserId());
    profiles.add(userProfile);
  }

  @Override
  public boolean delete(UserId userId)
  {
    UserProfile userProfile = findByUserId(userId);
    if (userProfile != null)
    {
      profiles.remove(userProfile);
      return true;
    }
    return false;
  }

  @Override
  public UserProfile findByUserId(UserId userId)
  {
    return null;
  }

  @Override
  public UserProfile findByPhoneNumber(String phoneNumber)
  {
    return null;
  }

  private UserProfile find(UserId userId)
  {
    for (UserProfile userProfile : profiles)
    {
      if (userProfile.getUserId().equals(userId))
      {
        return userProfile;
      }
    }
    return null;
  }

  @Override
  public UserProfile findById(EntityId entityId)
  {
    return find((UserId) entityId);
  }

  @Override
  public Collection<UserProfile> findAll()
  {
    return profiles;
  }
}

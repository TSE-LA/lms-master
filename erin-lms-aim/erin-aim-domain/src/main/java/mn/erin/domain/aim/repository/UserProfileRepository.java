package mn.erin.domain.aim.repository;

import java.util.LinkedHashMap;
import java.util.Map;

import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.user.UserId;
import mn.erin.domain.aim.model.user.UserProfile;
import mn.erin.domain.base.repository.Repository;

/**
 * @author Munkh
 */
public interface UserProfileRepository extends Repository<UserProfile>
{
  /**
   * Creates the profile of a user
   *
   * @param userProfile creating user profile
   * @throws AimRepositoryException if user profile already exists
   */
  void create(UserProfile userProfile) throws AimRepositoryException;

  /**
   * Updates existing profile
   *
   * @param userProfile updating user profile
   * @throws AimRepositoryException if user profile not found
   */
  void update(UserProfile userProfile) throws AimRepositoryException;

  /**
   * Deletes the profile of the user
   *
   * @param userId The user ID of the deleting profile
   */
  boolean delete(UserId userId);

  UserProfile findByUserId(UserId userId);

  UserProfile findByPhoneNumber(String phoneNumber);

  default Map<UserId, UserProfile> asMap()
  {
    Map<UserId, UserProfile> profileMap = new LinkedHashMap<>();
    for (UserProfile profile : findAll())
    {
      profileMap.put(profile.getUserId(), profile);
    }
    return profileMap;
  }
}

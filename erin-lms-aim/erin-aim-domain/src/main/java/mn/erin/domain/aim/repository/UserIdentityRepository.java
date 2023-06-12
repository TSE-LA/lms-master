package mn.erin.domain.aim.repository;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.user.UserId;
import mn.erin.domain.aim.model.user.UserIdentity;
import mn.erin.domain.aim.model.user.UserIdentitySource;
import mn.erin.domain.base.repository.Repository;

/**
 * @author Munkh
 */
public interface UserIdentityRepository extends Repository<UserIdentity>
{
  /**
   * Creates new identity of a user
   *
   * @param userIdentity creating identity
   * @throws AimRepositoryException if user identity already exists
   */
  void create(UserIdentity userIdentity) throws AimRepositoryException;

  /**
   * Gets own identity by username
   *
   * @param username the searching username
   * @param sources multiple sources
   * @return find first identity object
   */
  UserIdentity getUserIdentityByUsername(String username, UserIdentitySource... sources);

  /**
   * Gets the list of identities of the source
   *
   * @param userIdentitySource the source of getting identities
   * @return List of identity objects
   */
  List<UserIdentity> getAllBySource(UserIdentitySource userIdentitySource);


  UserIdentity getIdentity(UserId userId);

  /**
   * Gets other sources identity by user ID
   *
   * @param userId             the searching user ID
   * @param userIdentitySource the source of the identity
   * @return other sources identity
   */
  UserIdentity getIdentity(UserId userId, UserIdentitySource userIdentitySource);

  /**
   * Case insensitive.
   */
  boolean existByUsername(String username, UserIdentitySource source);

  /**
   * Updates user identity
   *
   * @param userIdentity the updating identity
   * @throws AimRepositoryException if not found
   */
  void update(UserIdentity userIdentity) throws AimRepositoryException;

  /**
   * Deletes all of the user's identity
   *
   * @param userId ID of the deleting identities of the user
   */
  boolean delete(UserId userId);

  /**
   * Deletes only given identity
   *
   * @param userIdentity deleting identity
   */
  void delete(UserIdentity userIdentity);

  default Map<UserId, UserIdentity> asMap(UserIdentitySource source)
  {
    Map<UserId, UserIdentity> identityMap = new LinkedHashMap<>();
    for (UserIdentity identity : getAllBySource(source))
    {
      identityMap.put(identity.getUserId(), identity);
    }
    return identityMap;
  }
}

package mn.erin.domain.aim.repository;

import java.util.List;

import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.aim.model.user.User;
import mn.erin.domain.aim.model.user.UserId;
import mn.erin.domain.aim.model.user.UserStateChangeSource;
import mn.erin.domain.aim.model.user.UserStatus;
import mn.erin.domain.base.repository.Repository;

/**
 * @author Bat-Erdene Tsogoo.
 */
public interface UserRepository extends Repository<User>
{
  /**
   * Lists all users in the repository
   *
   * @param tenantId The ID of the tenant
   * @return The list of all users
   */
  List<User> getAllUsers(TenantId tenantId);

  /**
   * Creates a new user
   *
   * @param tenantId The tenant ID of the user
   * @return A new user object
   */
  User createUser(TenantId tenantId);

  /**
   * Changes the tenant of a user
   *
   * @param userId      The ID of the user whose tenant will be changed
   * @param newTenantId A new tenant ID
   * @return An updated user
   */
  User changeTenant(UserId userId, TenantId newTenantId) throws AimRepositoryException;

  /**
   * Deletes a user
   *
   * @param userId The ID of the user
   * @return true if deleted; otherwise, false
   */
  boolean delete(UserId userId);

  /**
   * Checks if user exists
   *
   * @param userId The user ID of a checking user
   * @return true if exists; otherwise false
   */
  boolean doesExistById(UserId userId);

  void changeState(UserId userId, UserStatus status, UserStateChangeSource source) throws AimRepositoryException;
}

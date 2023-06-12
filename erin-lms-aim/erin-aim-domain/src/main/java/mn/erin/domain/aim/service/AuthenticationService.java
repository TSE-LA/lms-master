package mn.erin.domain.aim.service;

/**
 * Only Responsible for authenticating a user to a tenant using username and password. An authenticated user shall have a valid session id.
 *
 * @author EBazarragchaa
 */
public interface AuthenticationService
{
  /**
   * Authenticates a user to a tenant.
   *
   * @param tenantId the tenant id can represent a company or system
   * @param username login username
   * @param password login user password
   * @return valid token string, otherwise null
   */
  String authenticate(String tenantId, String username, String password);

  String logout();

  /**
   * Returns current authenticated username
   *
   * @return username or null
   */
  String getCurrentUsername();

  /**
   * Returns current user authenticated state
   *
   * @return authentication true or false
   */
  boolean isCurrentUserAuthenticated();
  /**
   * Returns current user token
   *
   * @return token
   */
  String getToken();
}

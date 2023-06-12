package mn.erin.lms.unitel.domain.model.user;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class Membership
{
  private final String userId;
  private final String role;

  public Membership(String userId, String role)
  {
    this.userId = userId;
    this.role = role;
  }

  public String getUserId()
  {
    return userId;
  }

  public String getRole()
  {
    return role;
  }
}

package mn.erin.lms.base.rest.api.aim.model;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class LoginRequest
{
  private String username;
  private String password;
  private String tenantId;

  public void setUsername(String username)
  {
    this.username = username;
  }

  public void setPassword(String password)
  {
    this.password = password;
  }

  public String getUsername()
  {
    return username;
  }

  public String getPassword()
  {
    return password;
  }

  public String getTenantId()
  {
    return tenantId;
  }

  public void setTenantId(String tenantId)
  {
    this.tenantId = tenantId;
  }
}

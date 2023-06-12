package mn.erin.domain.aim.usecase.authentication;

import java.util.Collection;

import mn.erin.domain.aim.model.permission.Permission;

/**
 * @author Tamir
 */
public class LoginUserOutput
{
  private String token;
  private String group;
  private Collection<Permission> permissions;
  private String username;
  private String role;

  public LoginUserOutput(String token, String group, Collection<Permission> permissions, String username, String role)
  {
    this.token = token;
    this.group = group;
    this.permissions = permissions;
    this.username = username;
    this.role = role;
  }

  public String getToken()
  {
    return token;
  }

  public void setToken(String token)
  {
    this.token = token;
  }

  public String getGroup()
  {
    return group;
  }

  public void setGroup(String group)
  {
    this.group = group;
  }

  public Collection<Permission> getPermissions()
  {
    return permissions;
  }

  public void setPermissions(Collection<Permission> permissions)
  {
    this.permissions = permissions;
  }

  public String getUsername()
  {
    return username;
  }

  public void setUsername(String username)
  {
    this.username = username;
  }

  public String getRole()
  {
    return role;
  }

  public void setRole(String role)
  {
    this.role = role;
  }
}

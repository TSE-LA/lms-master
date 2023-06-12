package mn.erin.lms.base.rest.api.aim.model;

import java.util.Set;

import mn.erin.lms.base.aim.permission.PermissionDto;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class LoginResult
{
  private final String token;
  private String userId;
  private String username;
  private String departmentId;
  private String role;

  private Set<PermissionDto> permissions;

  public LoginResult(String token)
  {
    this.token = token;
  }

  public String getUserId()
  {
    return userId;
  }

  public String getToken()
  {
    return token;
  }

  public String getUsername()
  {
    return username;
  }

  public void setUsername(String username)
  {
    this.username = username;
  }

  public String getDepartmentId()
  {
    return departmentId;
  }

  public void setDepartmentId(String departmentId)
  {
    this.departmentId = departmentId;
  }

  public String getRole()
  {
    return role;
  }

  public void setRole(String role)
  {
    this.role = role;
  }

  public Set<PermissionDto> getPermissions()
  {
    return permissions;
  }

  public void setPermissions(Set<PermissionDto> permissions)
  {
    this.permissions = permissions;
  }

  public void setUserId(String userId)
  {
    this.userId = userId;
  }
}

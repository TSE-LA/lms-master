package mn.erin.lms.jarvis.domain.report.usecase.dto;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class OtherActivityData
{
  private String username;
  private String groupName;
  private String role;
  private Float status;

  public String getUsername()
  {
    return username;
  }

  public void setUsername(String username)
  {
    this.username = username;
  }

  public String getGroupName()
  {
    return groupName;
  }

  public void setGroupName(String groupName)
  {
    this.groupName = groupName;
  }

  public String getRole()
  {
    return role;
  }

  public void setRole(String role)
  {
    this.role = role;
  }

  public Float getStatus()
  {
    return status;
  }

  public void setStatus(Float status)
  {
    this.status = status;
  }
}

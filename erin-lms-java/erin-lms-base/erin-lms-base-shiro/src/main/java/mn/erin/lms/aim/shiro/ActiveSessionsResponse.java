package mn.erin.lms.aim.shiro;

import java.util.List;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class ActiveSessionsResponse
{
  private Integer activeUsersCount;
  private List<String> activeUsers;

  public Integer getActiveUsersCount()
  {
    return activeUsersCount;
  }

  public void setActiveUsersCount(Integer activeUsersCount)
  {
    this.activeUsersCount = activeUsersCount;
  }

  public List<String> getActiveUsers()
  {
    return activeUsers;
  }

  public void setActiveUsers(List<String> activeUsers)
  {
    this.activeUsers = activeUsers;
  }
}

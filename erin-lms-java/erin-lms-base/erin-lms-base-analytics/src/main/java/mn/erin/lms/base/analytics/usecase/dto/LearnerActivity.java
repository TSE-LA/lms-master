package mn.erin.lms.base.analytics.usecase.dto;

/**
 * @author Munkh
 */
public class LearnerActivity
{
  String username;
  double status;
  String role;
  String groupPath;
  String spentTime;

  public LearnerActivity(String username, double status, String role, String groupPath, String spentTime)
  {
    this.username = username;
    this.status = status;
    this.role = role;
    this.groupPath = groupPath;
    this.spentTime = spentTime;
  }

  public String getUsername()
  {
    return username;
  }

  public void setUsername(String username)
  {
    this.username = username;
  }

  public double getStatus()
  {
    return status;
  }

  public void setStatus(double status)
  {
    this.status = status;
  }

  public String getRole()
  {
    return role;
  }

  public void setRole(String role)
  {
    this.role = role;
  }

  public String getGroupPath()
  {
    return groupPath;
  }

  public void setGroupPath(String groupPath)
  {
    this.groupPath = groupPath;
  }

  public String getSpentTime()
  {
    return spentTime;
  }

  public void setSpentTime(String spentTime)
  {
    this.spentTime = spentTime;
  }
}

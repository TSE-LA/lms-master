package mn.erin.lms.base.analytics.usecase.dto;

public class AnnouncementUserInfo
{
  private String username;
  private String firstName;
  private String lastName;
  private String departmentName;
  private String viewedDate;

  public AnnouncementUserInfo(String username, String firstName, String lastName, String departmentName)
  {
    this.username = username;
    this.firstName = firstName;
    this.lastName = lastName;
    this.departmentName = departmentName;
  }

  public String getFirstName()
  {
    return firstName;
  }

  public String getLastName()
  {
    return lastName;
  }

  public String getDepartmentName()
  {
    return departmentName;
  }

  public void setFirstName(String firstName)
  {
    this.firstName = firstName;
  }

  public void setLastName(String lastName)
  {
    this.lastName = lastName;
  }

  public void setDepartmentName(String groupId)
  {
    this.departmentName = groupId;
  }

  public String getViewedDate()
  {
    return viewedDate;
  }

  public void setViewedDate(String viewedDate)
  {
    this.viewedDate = viewedDate;
  }

  public String getUsername()
  {
    return username;
  }

  public void setUsername(String username)
  {
    this.username = username;
  }
}

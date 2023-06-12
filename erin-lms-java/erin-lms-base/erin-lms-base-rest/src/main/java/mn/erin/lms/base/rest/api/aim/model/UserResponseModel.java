package mn.erin.lms.base.rest.api.aim.model;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class UserResponseModel
{
  private String id;
  private String username;
  private String email;
  private String phoneNumber;
  private String firstName;
  private String lastName;
  private String groupPath;
  private MembershipResponse membership;

  public String getId()
  {
    return id;
  }

  public void setId(String id)
  {
    this.id = id;
  }

  public String getEmail()
  {
    return email;
  }

  public void setEmail(String email)
  {
    this.email = email;
  }

  public String getPhoneNumber()
  {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber)
  {
    this.phoneNumber = phoneNumber;
  }

  public MembershipResponse getMembership()
  {
    return membership;
  }

  public void setMembership(MembershipResponse membership)
  {
    this.membership = membership;
  }

  public String getUsername()
  {
    return username;
  }

  public void setUsername(String username)
  {
    this.username = username;
  }

  public String getFirstName()
  {
    return firstName;
  }

  public void setFirstName(String firstName)
  {
    this.firstName = firstName;
  }

  public String getLastName()
  {
    return lastName;
  }

  public void setLastName(String lastName)
  {
    this.lastName = lastName;
  }

  public String getGroupPath()
  {
    return groupPath;
  }

  public void setGroupPath(String groupPath)
  {
    this.groupPath = groupPath;
  }
}

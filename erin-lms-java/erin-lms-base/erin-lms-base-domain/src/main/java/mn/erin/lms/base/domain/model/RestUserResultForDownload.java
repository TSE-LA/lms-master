package mn.erin.lms.base.domain.model;

import mn.erin.domain.aim.model.user.UserStatus;

public class RestUserResultForDownload
{
  private String username;
  private String firstname;
  private String lastname;
  private String email;
  private String phoneNumber;
  private String modifiedDate;
  private UserStatus status;

  public RestUserResultForDownload()
  {
  }
  public String getUsername()
  {
    return username;
  }

  public void setUsername(String username)
  {
    this.username = username;
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

  public String getFirstname()
  {
    return firstname;
  }

  public void setFirstname(String firstname)
  {
    this.firstname = firstname;
  }

  public String getLastname()
  {
    return lastname;
  }

  public void setLastname(String lastname)
  {
    this.lastname = lastname;
  }

  public String getModifiedDate()
  {
    return modifiedDate;
  }

  public void setModifiedDate(String modifiedDate)
  {
    this.modifiedDate = modifiedDate;
  }

  public UserStatus getStatus()
  {
    return status;
  }

  public void setStatus(UserStatus status)
  {
    this.status = status;
  }
}

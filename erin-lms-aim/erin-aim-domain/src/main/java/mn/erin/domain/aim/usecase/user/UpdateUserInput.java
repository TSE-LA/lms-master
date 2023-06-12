package mn.erin.domain.aim.usecase.user;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Munkh
 */
public class UpdateUserInput
{
  private String userId;
  private String firstName;
  private String lastName;
  private String email;
  private String phoneNumber;
  private String username;
  private String password;
  private String gender;
  private String birthday;
  private String jobTitle;
  private File image;
  private Map<String, String> properties = new HashMap<>();

  private String oldPassword;
  private String newPassword;

  public UpdateUserInput()
  {
  }

  public UpdateUserInput(String userId, String username, String password)
  {
    this.userId = userId;
    this.username = username;
    this.password = password;
  }


  public String getUserId()
  {
    return userId;
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

  public String getUsername()
  {
    return username;
  }

  public void setUsername(String username)
  {
    this.username = username;
  }

  public String getPassword()
  {
    return password;
  }

  public void setPassword(String password)
  {
    this.password = password;
  }

  public String getGender()
  {
    return gender;
  }

  public void setGender(String gender) { this.gender = gender; }

  public String getBirthday()
  {
    return birthday;
  }

  public void setBirthday(String birthday)
  {
    this.birthday = birthday;
  }

  public String getJobTitle() { return jobTitle; }

  public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }

  public File getImage()
  {
    return image;
  }

  public void setImage(File image)
  {
    this.image = image;
  }

  public Map<String, String> getProperties()
  {
    return properties;
  }

  public void setProperties(Map<String, String> properties)
  {
    this.properties = properties;
  }

  public String getOldPassword()
  {
    return oldPassword;
  }

  public void setOldPassword(String oldPassword)
  {
    this.oldPassword = oldPassword;
  }

  public String getNewPassword()
  {
    return newPassword;
  }

  public void setNewPassword(String newPassword)
  {
    this.newPassword = newPassword;
  }
}


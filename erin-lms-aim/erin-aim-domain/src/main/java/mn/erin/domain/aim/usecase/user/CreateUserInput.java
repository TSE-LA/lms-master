package mn.erin.domain.aim.usecase.user;

import java.io.File;
import java.util.Map;
import java.util.Objects;

import static mn.erin.domain.aim.constant.AimErrorMessageConstant.PASSWORD_CANNOT_BE_NULL;
import static mn.erin.domain.aim.constant.AimErrorMessageConstant.TENANT_ID_CANNOT_BE_NULL;
import static mn.erin.domain.aim.constant.AimErrorMessageConstant.USERNAME_CANNOT_BE_NULL;

/**
 * @author Munkh
 */
public class CreateUserInput
{
  private final String tenantId;
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
  private Map<String, String> properties;

  public CreateUserInput(String tenantId, String username, String password)
  {
    this.tenantId = Objects.requireNonNull(tenantId, TENANT_ID_CANNOT_BE_NULL);
    this.username = Objects.requireNonNull(username, USERNAME_CANNOT_BE_NULL);
    this.password = Objects.requireNonNull(password, PASSWORD_CANNOT_BE_NULL);
  }

  public String getTenantId()
  {
    return tenantId;
  }

  public String getFirstName()  { return firstName; }

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

  public String getUsername() { return username; }

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
}

package mn.erin.lms.base.rest.api.aim.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import mn.erin.domain.aim.model.user.UserGender;
import mn.erin.domain.aim.model.user.UserStatus;

/**
 * @author Munkh
 */
public class RestUserResult
{
  private String userId;
  private String username;
  private String email;
  private String phoneNumber;
  private String lastName;
  private String firstName;
  private UserGender gender;
  private String birthday;
  private String jobTitle;
  private String imageId;
  private UserStatus status;
  private String dateLastModified;

  private String message;
  private boolean deletable;

  private List<RestMembership> memberships;
  private Map<String, String> properties;

  public RestUserResult()
  {
    userId = null;
    username = null;
    email = null;
    phoneNumber = null;
    lastName = null;
    firstName = null;
    gender = null;
    birthday = null;
    jobTitle = null;
    imageId = null;
    status = null;
    dateLastModified = null;
    message = null;
    deletable = false;
  }

  public String getUserId()
  {
    return userId;
  }

  public void setUserId(String userId)
  {
    this.userId = userId;
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

  public String getLastName()
  {
    return lastName;
  }

  public void setLastName(String lastName)
  {
    this.lastName = lastName;
  }

  public String getFirstName()
  {
    return firstName;
  }

  public void setFirstName(String firstName)
  {
    this.firstName = firstName;
  }

  public UserGender getGender()
  {
    return gender;
  }

  public void setGender(UserGender gender)
  {
    this.gender = gender;
  }

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

  public String getImageId()
  {
    return imageId;
  }

  public void setImageId(String imageId)
  {
    this.imageId = imageId;
  }

  public UserStatus getStatus()
  {
    return status;
  }

  public void setStatus(UserStatus status)
  {
    this.status = status;
  }

  public String getDateLastModified()
  {
    return dateLastModified;
  }

  public void setDateLastModified(String dateLastModified)
  {
    this.dateLastModified = dateLastModified;
  }

  public void setDateLastModified(LocalDateTime dateLastModified)
  {
    this.dateLastModified = dateLastModified.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm"));
  }

  public String getMessage()
  {
    return message;
  }

  public void setMessage(String message)
  {
    this.message = message;
  }

  public boolean isDeletable()
  {
    return deletable;
  }

  public void setDeletable(boolean deletable)
  {
    this.deletable = deletable;
  }

  public List<RestMembership> getMemberships()
  {
    return memberships;
  }

  public void setMemberships(List<RestMembership> memberships)
  {
    this.memberships = memberships;
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

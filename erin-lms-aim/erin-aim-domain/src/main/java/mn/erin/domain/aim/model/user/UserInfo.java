package mn.erin.domain.aim.model.user;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import mn.erin.domain.base.model.ValueObject;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Zorig
 */
public class UserInfo implements ValueObject<UserInfo>
{
  private static final String ROOT_URL = "/alfresco/Users/";

  private String firstName;
  private String lastName;
  private String displayName;
  private UserGender gender;
  private LocalDateTime birthday;
  private String jobTitle;
  private String imageId;
  private String folderName;
  private Map<String, String> properties = new HashMap<>();

  public UserInfo()
  {
    this("", "", UserGender.NA, null, "", "", "", new HashMap<>());
  }

  public UserInfo(String firstName)
  {
    this(firstName, "", UserGender.NA, null, "", "", "", new HashMap<>());
  }

  public UserInfo(String firstName, String lastName)
  {
    this(firstName, lastName, UserGender.NA, null, "", "", "", new HashMap<>());
  }

  public UserInfo(String firstName, String lastName, UserGender gender, LocalDateTime birthday, String jobTitle, String imageId, String folderName, Map<String, String> properties)
  {
    this.firstName = firstName;
    this.lastName = lastName;
    this.gender = gender;
    this.birthday = birthday;
    this.displayName = toDisplayName(firstName, lastName);
    this.jobTitle = jobTitle;
    this.imageId = imageId;
    this.folderName = folderName;
    this.properties = properties;
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

  public String getDisplayName()
  {
    return displayName;
  }

  public void setDisplayName(String displayName)
  {
    this.displayName = displayName;
  }

  public UserGender getGender()
  {
    return gender;
  }

  public void setGender(UserGender gender)
  {
    this.gender = gender;
  }

  public LocalDateTime getBirthday()
  {
    return birthday;
  }

  public void setBirthday(LocalDateTime birthday)
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

  public String getFolderName()
  {
    return folderName;
  }

  public void setFolderName(String folderName)
  {
    this.folderName = folderName;
  }

  public Map<String, String> getProperties()
  {
    return properties;
  }

  public void setProperties(Map<String, String> properties)
  {
    this.properties = properties;
  }

  public String getUserName()
  {
    if (null != lastName && lastName.length() > 1 && !StringUtils.isBlank(firstName))
    {
      return lastName.charAt(0) + "." + firstName;
    }
    if (StringUtils.isBlank(firstName))
    {
      return "(empty name)";
    }
    else
    {
      return firstName;
    }

  }

  @Override
  public boolean sameValueAs(UserInfo other)
  {
    return false;
  }

  private String toDisplayName(String firstName, String lastName)
  {
    if(firstName == null){
      return (lastName == null ? "" : lastName );
    }

    return (lastName == null ? firstName : (firstName + " " + lastName) );
  }

  public String getImageURL()
  {
    return ROOT_URL + folderName + "/" + imageId;
  }
}

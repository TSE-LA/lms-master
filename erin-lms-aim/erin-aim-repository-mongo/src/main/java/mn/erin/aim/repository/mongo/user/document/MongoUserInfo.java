package mn.erin.aim.repository.mongo.user.document;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author Munkh
 */
@Document
public class MongoUserInfo
{
  private String firstName;
  private String lastName;
  private String gender;
  private LocalDateTime birthday;
  private String jobTitle;
  private String imageId;
  private String folderName;
  private Map<String, String> properties = new HashMap<>();

  public String getFirstName()
  {
    return firstName;
  }

  public MongoUserInfo setFirstName(String firstName)
  {
    this.firstName = firstName;
    return this;
  }

  public String getLastName()
  {
    return lastName;
  }

  public MongoUserInfo setLastName(String lastName)
  {
    this.lastName = lastName;
    return this;
  }

  public String getGender()
  {
    return gender;
  }

  public MongoUserInfo setGender(String gender)
  {
    this.gender = gender;
    return this;
  }

  public LocalDateTime getBirthday()
  {
    return birthday;
  }

  public MongoUserInfo setBirthday(LocalDateTime birthday)
  {
    this.birthday = birthday;
    return this;
  }

  public String getJobTitle()
  {
    return jobTitle;
  }

  public MongoUserInfo setJobTitle(String jobTitle)
  {
    this.jobTitle = jobTitle;
    return this;
  }

  public String getImageId()
  {
    return imageId;
  }

  public MongoUserInfo setImageId(String imageId)
  {
    this.imageId = imageId;
    return this;
  }

  public String getFolderName()
  {
    return folderName;
  }

  public MongoUserInfo setFolderName(String folderName)
  {
    this.folderName = folderName;
    return this;
  }

  public Map<String, String> getProperties()
  {
    return properties;
  }

  public MongoUserInfo setProperties(Map<String, String> properties)
  {
    this.properties = properties;
    return this;
  }
}

package mn.erin.domain.aim.usecase.user;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import mn.erin.domain.aim.model.user.UserGender;
import mn.erin.domain.aim.model.user.UserStatus;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class GetUserOutput
{
  private String id;
  private String tenantId;
  private String username;
  private String firstName;
  private String lastName;
  private String displayName;
  private UserGender gender;
  private String birthday;
  private String jobTitle;
  private String imageId;
  private String imageUrl;
  private String email;
  private String phoneNumber;
  private UserStatus status;
  private LocalDateTime dateLastModified;

  private String message;
  private boolean deletable;

  private Map<String, String> properties = new HashMap<>();

  private GetUserOutput()
  {
  }

  public String getId()
  {
    return id;
  }

  public String getTenantId()
  {
    return tenantId;
  }

  public String getUsername()
  {
    return username;
  }

  public String getFirstName()
  {
    return firstName;
  }

  public String getLastName()
  {
    return lastName;
  }

  public String getDisplayName()
  {
    return displayName;
  }

  public UserGender getGender()
  {
    return gender;
  }

  public String getBirthday()
  {
    return birthday;
  }

  public String getJobTitle()
  {
    return jobTitle;
  }

  public String getImageId()
  {
    return imageId;
  }

  public String getImageUrl()
  {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl)
  {
    this.imageUrl = imageUrl;
  }

  public String getEmail()
  {
    return email;
  }

  public String getPhoneNumber()
  {
    return phoneNumber;
  }

  public UserStatus getStatus()
  {
    return status;
  }

  public LocalDateTime getDateLastModified()
  {
    return dateLastModified;
  }

  public String getMessage()
  {
    return message;
  }

  public boolean isDeletable()
  {
    return deletable;
  }

  public Map<String, String> getProperties()
  {
    return properties;
  }

  public static class Builder
  {
    private final String id;
    private String tenantId;
    private String username;
    private String firstName;
    private String lastName;
    private String displayName;
    private UserGender gender;
    private String birthday;
    private String jobTitle;
    private String imageId;
    private String imageUrl;
    private String email;
    private String phoneNumber;
    private UserStatus status;
    private LocalDateTime dateLastModified;

    private String message;
    private boolean deletable;

    private Map<String, String> properties = new HashMap<>();

    public Builder(String id)
    {
      this.id = id;
    }

    public Builder withTenant(String tenantId)
    {
      this.tenantId = tenantId;
      return this;
    }

    public Builder withUsername(String username)
    {
      this.username = username;
      return this;
    }

    public Builder withFirstName(String firstName)
    {
      this.firstName = firstName;
      return this;
    }

    public Builder withLastName(String lastName)
    {
      this.lastName = lastName;
      return this;
    }

    public Builder withDisplayName(String displayName)
    {
      this.displayName = displayName;
      return this;
    }

    public Builder withGender(UserGender gender)
    {
      this.gender = gender;
      return this;
    }

    public Builder withBirthday(String birthday)
    {
      this.birthday = birthday;
      return this;
    }

    public Builder withJobTitle(String jobTitle)
    {
      this.jobTitle = jobTitle;
      return this;
    }

    public Builder withImageId(String imageId)
    {
      this.imageId = imageId;
      return this;
    }

    public Builder withImageUrl(String imageUrl)
    {
      this.imageUrl = imageUrl;
      return this;
    }

    public Builder withEmail(String email)
    {
      this.email = email;
      return this;
    }

    public Builder withPhoneNumber(String phoneNumber)
    {
      this.phoneNumber = phoneNumber;
      return this;
    }

    public Builder withStatus(UserStatus status)
    {
      this.status = status;
      return this;
    }

    public Builder withDateLastModified(LocalDateTime dateLastModified)
    {
      this.dateLastModified = dateLastModified;
      return this;
    }

    public Builder withMessage(String message)
    {
      this.message = message;
      return this;
    }

    public Builder hasDeletable(boolean deletable)
    {
      this.deletable = deletable;
      return this;
    }

    public Builder withProperties(Map<String, String> properties)
    {
      this.properties = properties;
      return this;
    }

    public GetUserOutput build()
    {
      GetUserOutput output = new GetUserOutput();
      output.id = this.id;
      output.tenantId = this.tenantId;
      output.username = this.username;
      output.firstName = this.firstName;
      output.lastName = this.lastName;
      output.displayName = this.displayName;
      output.gender = this.gender;
      output.birthday = this.birthday;
      output.jobTitle = this.jobTitle;
      output.imageId = this.imageId;
      output.imageUrl = this.imageUrl;
      output.email = this.email;
      output.phoneNumber = this.phoneNumber;
      output.status = this.status;
      output.dateLastModified = this.dateLastModified;
      output.message = this.message;
      output.deletable = this.deletable;
      output.properties = this.properties;
      return output;
    }
  }
}

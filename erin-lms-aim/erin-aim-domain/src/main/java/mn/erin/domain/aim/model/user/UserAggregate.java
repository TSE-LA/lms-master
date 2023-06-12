package mn.erin.domain.aim.model.user;

import java.time.LocalDateTime;
import java.util.Map;

import mn.erin.domain.aim.service.UserDataResult;
import mn.erin.domain.base.model.Aggregate;

public class UserAggregate implements Aggregate<User>
{
  private final User user;
  private final UserIdentity identity;
  private final UserProfile profile;
  private UserDataResult dataResult;

  public UserAggregate(User user, UserIdentity identity, UserProfile profile)
  {
    this.user = user;
    this.identity = identity;
    this.profile = profile;
    dataResult = new UserDataResult(false);
  }

  @Override
  public User getRootEntity()
  {
    return user;
  }

  public UserIdentity getIdentity()
  {
    return identity;
  }

  public UserProfile getProfile()
  {
    return profile;
  }

  public String getUserId()
  {
    return user.getUserId().getId();
  }

  public String getTenantId()
  {
    return user.getTenantId().getId();
  }

  public String getUsername()
  {
    return identity != null ? identity.getUsername() : null;
  }

  public String getFirstName()
  {
    return profile != null && profile.getUserInfo() != null ? profile.getUserInfo().getFirstName() : null;
  }

  public String getLastName()
  {
    return profile != null && profile.getUserInfo() != null ? profile.getUserInfo().getLastName() : null;
  }

  public String getDisplayName()
  {
    return profile != null && profile.getUserInfo() != null ? profile.getUserInfo().getDisplayName() : null;
  }

  public UserGender getGender()
  {
    return profile != null && profile.getUserInfo() != null ? profile.getUserInfo().getGender() : null;
  }

  public String getBirthday()
  {
    if (profile != null && profile.getUserInfo() != null &&
        profile.getUserInfo().getBirthday() != null)
    {
      return profile.getUserInfo().getBirthday().toLocalDate().toString();
    }
    return null;
  }

  public String getJobTitle() { return profile != null && profile.getUserInfo() != null ? profile.getUserInfo().getJobTitle() : null; }

  public String getImageId()
  {
    return profile.getUserInfo().getImageId();
  }

  public String getEmail()
  {
    return profile != null && profile.getUserContact() != null ? profile.getUserContact().getEmail() : null;
  }

  public String getPhoneNumber()
  {
    return profile != null && profile.getUserContact() != null ? profile.getUserContact().getPhoneNumber() : null;
  }

  public UserStatus getStatus()
  {
    return user.getStatus();
  }

  public LocalDateTime getLastModified()
  {
    return user.getLastModified();
  }

  public void setDataResult(UserDataResult dataResult)
  {
    this.dataResult = dataResult;
  }

  public String getMessage()
  {
    return dataResult.getMessage();
  }

  public boolean isDeletable()
  {
    return !dataResult.affirmative;
  }

  public Map<String, String> getProperties()
  {
    return profile != null && profile.getUserInfo() != null ? profile.getUserInfo().getProperties() : null;
  }
}

package mn.erin.domain.aim.model.user;

import java.util.Objects;

import mn.erin.domain.base.model.Entity;

import static mn.erin.domain.aim.constant.AimErrorMessageConstant.USER_CONTACT_CANNOT_BE_NULL;
import static mn.erin.domain.aim.constant.AimErrorMessageConstant.USER_ID_CANNOT_BE_NULL;

/**
 * @author Munkh
 */
public class UserProfile implements Entity<UserProfile>
{
  private final UserProfileId id;
  private final UserId userId;
  private final UserInfo userInfo;
  private final UserContact userContact;

  public UserProfile(UserId userId, UserContact contact)
  {
    this(null, userId, new UserInfo(), contact);
  }

  public UserProfile(UserId userId, UserInfo info)
  {
    this(null, userId, info, new UserContact());
  }

  public UserProfile(UserId userId, UserInfo userInfo, UserContact userContact)
  {
    this(null, userId, userInfo, userContact);
  }

  /**
   * Creates profile of the user
   * User can created with empty profile
   *
   * @param userId The user of the profile's
   */
  public UserProfile(UserProfileId id, UserId userId, UserContact userContact)
  {
    this(id, userId, new UserInfo(), userContact);
  }

  /**
   * Creates profile with properties
   * Profile properties can be nullable
   *
   * @param userId      The user of the profile
   * @param userInfo    The user's info
   * @param userContact The contact info of the user
   */
  public UserProfile(UserProfileId id, UserId userId, UserInfo userInfo, UserContact userContact)
  {
    this.id = id;
    this.userId = Objects.requireNonNull(userId, USER_ID_CANNOT_BE_NULL);
    this.userInfo = userInfo;
    this.userContact = Objects.requireNonNull(userContact, USER_CONTACT_CANNOT_BE_NULL);
  }

  public UserProfileId getId()
  {
    return id;
  }

  public UserId getUserId()
  {
    return userId;
  }

  public UserInfo getUserInfo()
  {
    return userInfo;
  }

  public UserContact getUserContact()
  {
    return userContact;
  }

  @Override
  public boolean sameIdentityAs(UserProfile other)
  {
    return other.userId.equals(this.userId);
  }
}

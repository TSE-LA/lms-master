package mn.erin.domain.aim.model.user;

import java.util.Objects;

import mn.erin.domain.base.model.Entity;

import static mn.erin.domain.aim.constant.AimErrorMessageConstant.SOURCE_CANNOT_BE_NULL;
import static mn.erin.domain.aim.constant.AimErrorMessageConstant.USERNAME_CANNOT_BE_NULL;
import static mn.erin.domain.aim.constant.AimErrorMessageConstant.USER_ID_CANNOT_BE_NULL;

/**
 * @author Munkh
 */
public class UserIdentity implements Entity<UserIdentity>
{
  private final UserIdentityId id;
  private final UserId userId;
  private String username;
  private String password;
  private UserIdentitySource source;

  public UserIdentity(UserId userId, String username, String password)
  {
    this(userId, username, password, UserIdentitySource.OWN);
  }

  public UserIdentity(UserId userId, String username, String password, UserIdentitySource source)
  {
    this(null, userId, username, password, source);
  }

  /**
   * Creates user identity with password
   * The identity which created with our system
   *
   * @param userId             The user of the identity
   * @param username           The username of the identity
   * @param password           The password provided from the source
   * @param source The source which created the identity
   */
  public UserIdentity(UserIdentityId id, UserId userId, String username, String password, UserIdentitySource source)
  {
    this.id = id;
    this.userId = Objects.requireNonNull(userId, USER_ID_CANNOT_BE_NULL);
    this.username = Objects.requireNonNull(username, USERNAME_CANNOT_BE_NULL);
    this.password = password;
    this.source = Objects.requireNonNull(source, SOURCE_CANNOT_BE_NULL);
  }

  public UserIdentityId getId()
  {
    return id;
  }

  public UserId getUserId()
  {
    return userId;
  }

  public String getUsername()
  {
    return username;
  }

  public void setUsername(String username)
  {
    this.username = Objects.requireNonNull(username, USERNAME_CANNOT_BE_NULL);
  }

  public String getPassword()
  {
    return password;
  }

  public void setPassword(String password)
  {
    this.password = password;
  }

  public UserIdentitySource getUserIdentitySource()
  {
    return source;
  }

  public void setUserIdentitySource(UserIdentitySource userIdentitySource)
  {
    this.source = Objects.requireNonNull(userIdentitySource, SOURCE_CANNOT_BE_NULL);
  }

  @Override
  public boolean sameIdentityAs(UserIdentity other)
  {
    return other.userId.equals(this.userId);
  }
}

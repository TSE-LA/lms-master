package mn.erin.domain.aim.model.user;

import java.time.LocalDateTime;

import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.base.model.Entity;

/**
 * @author Zorig
 */
public class User implements Entity<User>
{
  private final UserId userId;
  private final TenantId tenantId;
  private UserStatus status;
  private UserStateChangeSource stateChangeSource;
  private LocalDateTime lastModified;

  public User(UserId userId, TenantId tenantId)
  {
    this(userId, tenantId, UserStatus.ACTIVE, UserStateChangeSource.SYSTEM, LocalDateTime.MIN);
  }

  public User(UserId userId, TenantId tenantId, UserStatus status, UserStateChangeSource source, LocalDateTime lastModified)
  {
    this.userId = userId;
    this.tenantId = tenantId;
    this.status = status;
    this.stateChangeSource = source;
    this.lastModified = lastModified;
  }

  public UserId getUserId()
  {
    return userId;
  }

  public TenantId getTenantId()
  {
    return tenantId;
  }

  public UserStatus getStatus()
  {
    return status;
  }

  public void setStatus(UserStatus status)
  {
    this.status = status;
  }

  public UserStateChangeSource getStateChangeSource()
  {
    return stateChangeSource;
  }

  public void setStateChangeSource(UserStateChangeSource stateChangeSource)
  {
    this.stateChangeSource = stateChangeSource;
  }

  public LocalDateTime getLastModified()
  {
    return lastModified;
  }

  public void setStateLastModified(LocalDateTime lastModified)
  {
    this.lastModified = lastModified;
  }

  @Override
  public boolean sameIdentityAs(User other)
  {
    return other.userId.equals(this.userId);
  }
}

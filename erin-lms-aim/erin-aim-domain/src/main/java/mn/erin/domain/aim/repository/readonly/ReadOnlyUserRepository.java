package mn.erin.domain.aim.repository.readonly;

import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.aim.model.user.User;
import mn.erin.domain.aim.model.user.UserId;
import mn.erin.domain.aim.model.user.UserStateChangeSource;
import mn.erin.domain.aim.model.user.UserStatus;
import mn.erin.domain.aim.repository.UserRepository;

public abstract class ReadOnlyUserRepository implements UserRepository
{
  @Override
  public User createUser(TenantId tenantId)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public User changeTenant(UserId userId, TenantId newTenantId)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean delete(UserId userId)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void changeState(UserId userId, UserStatus status, UserStateChangeSource source)
  {
    throw new UnsupportedOperationException();
  }
}

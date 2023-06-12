package mn.erin.domain.aim.repository.readonly;

import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.user.UserId;
import mn.erin.domain.aim.model.user.UserIdentity;
import mn.erin.domain.aim.repository.UserIdentityRepository;

public abstract class ReadOnlyUserIdentityRepository implements UserIdentityRepository
{
  @Override
  public void create(UserIdentity userIdentity) throws AimRepositoryException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void update(UserIdentity userIdentity) throws AimRepositoryException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean delete(UserId userId)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void delete(UserIdentity userIdentity)
  {
    throw new UnsupportedOperationException();
  }
}

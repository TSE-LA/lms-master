package mn.erin.domain.aim.repository.readonly;

import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.user.UserId;
import mn.erin.domain.aim.model.user.UserProfile;
import mn.erin.domain.aim.repository.UserProfileRepository;

public abstract class ReadOnlyUserProfileRepository implements UserProfileRepository
{
  @Override
  public void create(UserProfile userProfile) throws AimRepositoryException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void update(UserProfile userProfile) throws AimRepositoryException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean delete(UserId userId)
  {
    throw new UnsupportedOperationException();
  }
}

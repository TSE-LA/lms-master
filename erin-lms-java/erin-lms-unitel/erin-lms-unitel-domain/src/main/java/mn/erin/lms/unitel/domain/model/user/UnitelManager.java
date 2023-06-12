package mn.erin.lms.unitel.domain.model.user;

import mn.erin.domain.base.model.EntityId;
import mn.erin.domain.base.model.person.PersonId;
import mn.erin.lms.base.aim.user.Manager;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class UnitelManager implements Manager
{
  private final PersonId userId;

  public UnitelManager(PersonId userId)
  {
    this.userId = userId;
  }

  @Override
  public EntityId getId()
  {
    return this.userId;
  }
}

package mn.erin.lms.unitel.domain.model.user;

import mn.erin.domain.base.model.EntityId;
import mn.erin.domain.base.model.person.PersonId;
import mn.erin.lms.base.aim.user.Supervisor;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class UnitelSupervisor implements Supervisor
{
  private final PersonId userId;

  public UnitelSupervisor(PersonId userId)
  {
    this.userId = userId;
  }

  @Override
  public EntityId getId()
  {
    return this.userId;
  }
}

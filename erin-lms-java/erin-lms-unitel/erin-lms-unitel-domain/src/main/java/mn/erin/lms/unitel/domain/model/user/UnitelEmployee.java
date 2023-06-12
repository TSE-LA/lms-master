package mn.erin.lms.unitel.domain.model.user;

import mn.erin.domain.base.model.EntityId;
import mn.erin.domain.base.model.person.PersonId;
import mn.erin.lms.base.aim.user.Learner;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class UnitelEmployee implements Learner
{
  private final PersonId personId;

  public UnitelEmployee(PersonId personId)
  {
    this.personId = personId;
  }

  @Override
  public EntityId getId()
  {
    return this.personId;
  }
}

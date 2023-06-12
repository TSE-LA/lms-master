package mn.erin.lms.base.aim.user;

import mn.erin.domain.base.model.EntityId;
import mn.erin.domain.base.model.person.PersonId;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class LmsEmployee implements Learner
{
  private final PersonId personId;

  public LmsEmployee(PersonId personId)
  {
    this.personId = personId;
  }

  @Override
  public EntityId getId()
  {
    return this.personId;
  }
}

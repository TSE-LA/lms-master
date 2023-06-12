package mn.erin.lms.base.aim.user;

import mn.erin.domain.base.model.EntityId;
import mn.erin.domain.base.model.person.PersonId;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class LmsAdmin implements Instructor, Author
{
  private final PersonId userId;

  public LmsAdmin(PersonId userId)
  {
    this.userId = userId;
  }

  @Override
  public EntityId getId()
  {
    return this.userId;
  }
}

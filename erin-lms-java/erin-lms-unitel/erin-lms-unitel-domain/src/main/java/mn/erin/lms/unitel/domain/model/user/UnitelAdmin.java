package mn.erin.lms.unitel.domain.model.user;

import mn.erin.domain.base.model.EntityId;
import mn.erin.domain.base.model.person.PersonId;
import mn.erin.lms.base.aim.user.Author;
import mn.erin.lms.base.aim.user.Instructor;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class UnitelAdmin implements Instructor, Author
{
  private final PersonId userId;

  public UnitelAdmin(PersonId userId)
  {
    this.userId = userId;
  }

  @Override
  public EntityId getId()
  {
    return this.userId;
  }
}

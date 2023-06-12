package mn.erin.lms.base.aim.user;

import mn.erin.domain.base.model.EntityId;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class AuthorId extends EntityId
{
  private AuthorId(String id)
  {
    super(id);
  }

  public static AuthorId valueOf(String id)
  {
    return new AuthorId(id);
  }
}

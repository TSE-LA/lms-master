package mn.erin.lms.base.aim.user;

import mn.erin.domain.base.model.EntityId;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class LearnerId extends EntityId
{
  public LearnerId(String id)
  {
    super(id);
  }

  public static LearnerId valueOf(String id)
  {
    return new LearnerId(id);
  }
}

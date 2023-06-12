package mn.erin.lms.base.domain.model.assessment;

import mn.erin.domain.base.model.EntityId;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class QuizId extends EntityId
{
  private QuizId(String id)
  {
    super(id);
  }

  public static QuizId valueOf(String id)
  {
    return new QuizId(id);
  }
}

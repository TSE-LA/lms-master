package mn.erin.lms.base.domain.model.exam.question;

import mn.erin.domain.base.model.EntityId;

/**
 * @author Galsan Bayart
 */
public class QuestionStateId extends EntityId
{
  public QuestionStateId(String id)
  {
    super(id);
  }
  public static QuestionStateId valueOf(String id)
  {
    return new QuestionStateId(id);
  }

}

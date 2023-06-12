package mn.erin.lms.base.domain.model.exam.question;

import mn.erin.domain.base.model.EntityId;

/**
 * @author Galsan Bayart
 */
public class QuestionId extends EntityId
{
  public QuestionId(String id)
  {
    super(id);
  }

  public static QuestionId valueOf(String id)
  {
    return new QuestionId(id);
  }

}

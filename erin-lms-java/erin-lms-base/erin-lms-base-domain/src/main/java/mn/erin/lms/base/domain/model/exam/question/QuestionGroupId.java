package mn.erin.lms.base.domain.model.exam.question;

import mn.erin.domain.base.model.EntityId;

/**
 * @author Galsan Bayart
 */
public class QuestionGroupId extends EntityId
{
  public QuestionGroupId(String id)
  {
    super(id);
  }

  public static QuestionGroupId valueOf(String id)
  {
    return new QuestionGroupId(id);
  }
}

package mn.erin.lms.base.domain.model.exam;

import mn.erin.domain.base.model.EntityId;

/**
 * @author Galsan Bayart.
 */
public class ExamGroupId extends EntityId
{
  private ExamGroupId(String id)
  {
    super(id);
  }

  public static ExamGroupId valueOf(String id)
  {
    return new ExamGroupId(id);
  }

}

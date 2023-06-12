package mn.erin.lms.base.domain.model.exam;

import mn.erin.domain.base.model.EntityId;

/**
 * @author Galsan Bayart
 */
public class ExamId extends EntityId
{
  private ExamId(String id)
  {
    super(id);
  }

  public static ExamId valueOf(String id)
  {
    return new ExamId(id);
  }
}

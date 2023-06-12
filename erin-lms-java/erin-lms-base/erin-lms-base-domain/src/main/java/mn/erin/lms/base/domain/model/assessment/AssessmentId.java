package mn.erin.lms.base.domain.model.assessment;

import mn.erin.domain.base.model.EntityId;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class AssessmentId extends EntityId
{
  private AssessmentId(String id)
  {
    super(id);
  }

  public static AssessmentId valueOf(String id)
  {
    return new AssessmentId(id);
  }
}

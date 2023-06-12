package mn.erin.lms.legacy.domain.lms.model.audit;

import mn.erin.domain.base.model.EntityId;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class CourseAuditId extends EntityId
{
  private CourseAuditId(String id)
  {
    super(id);
  }

  public static CourseAuditId valueOf(String id)
  {
    return new CourseAuditId(id);
  }
}

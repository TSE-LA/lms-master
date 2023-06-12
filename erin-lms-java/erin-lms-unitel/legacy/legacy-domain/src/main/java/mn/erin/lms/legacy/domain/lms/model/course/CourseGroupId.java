package mn.erin.lms.legacy.domain.lms.model.course;

import mn.erin.domain.base.model.EntityId;

/**
 * @author Zorig
 */
public class CourseGroupId extends EntityId
{
  private static final long serialVersionUID = -5363947097771450128L;

  public CourseGroupId(String id)
  {
    super(id);
  }

  public static CourseGroupId valueOf(String id)
  {
    return new CourseGroupId(id);
  }
}

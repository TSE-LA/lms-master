package mn.erin.lms.base.domain.model.course;

import mn.erin.domain.base.model.EntityId;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class CourseId extends EntityId
{
  public CourseId(String id)
  {
    super(id);
  }

  public static CourseId valueOf(String id)
  {
    return new CourseId(id);
  }
}

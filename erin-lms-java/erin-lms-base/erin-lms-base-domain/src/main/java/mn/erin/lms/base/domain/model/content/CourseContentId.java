package mn.erin.lms.base.domain.model.content;

import mn.erin.domain.base.model.EntityId;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class CourseContentId extends EntityId
{
  private CourseContentId(String id)
  {
    super(id);
  }

  public static CourseContentId valueOf(String id)
  {
    return new CourseContentId(id);
  }
}

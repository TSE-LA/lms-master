package mn.erin.lms.base.test;

import mn.erin.lms.base.domain.model.course.CourseType;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class DummyCourseType implements CourseType
{
  @Override
  public String getType()
  {
    return "Dummy";
  }
}

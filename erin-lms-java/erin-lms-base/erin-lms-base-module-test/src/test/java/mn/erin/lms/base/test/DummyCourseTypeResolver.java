package mn.erin.lms.base.test;

import mn.erin.lms.base.domain.model.course.CourseType;
import mn.erin.lms.base.domain.service.CourseTypeResolver;
import mn.erin.lms.base.domain.service.UnknownCourseTypeException;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class DummyCourseTypeResolver implements CourseTypeResolver
{
  private static DummyCourseType courseType = new DummyCourseType();

  @Override
  public CourseType resolve(String type) throws UnknownCourseTypeException
  {
    if (!courseType.getType().equals(type))
    {
      throw new UnknownCourseTypeException("Unknown course type!");
    }
    return courseType;
  }
}

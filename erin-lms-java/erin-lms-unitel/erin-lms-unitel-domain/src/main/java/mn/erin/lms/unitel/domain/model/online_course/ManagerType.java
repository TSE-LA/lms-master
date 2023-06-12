package mn.erin.lms.unitel.domain.model.online_course;

import mn.erin.lms.base.domain.model.course.CourseType;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class ManagerType implements CourseType
{
  @Override
  public String getType()
  {
    return OnlineCourseType.MANAGER.name();
  }
}

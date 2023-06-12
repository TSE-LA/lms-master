package mn.erin.lms.base.domain.model.online_course;

import mn.erin.lms.base.domain.model.course.CourseType;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class EmployeeType implements CourseType
{
  @Override
  public String getType()
  {
    return OnlineCourseType.EMPLOYEE.name();
  }
}

package mn.erin.lms.base.domain.service.impl;

import org.apache.commons.lang3.Validate;

import mn.erin.lms.base.domain.model.course.CourseType;
import mn.erin.lms.base.domain.service.CourseTypeResolver;
import mn.erin.lms.base.domain.service.UnknownCourseTypeException;
import mn.erin.lms.base.domain.model.online_course.EmployeeType;
import mn.erin.lms.base.domain.model.online_course.ManagerType;
import mn.erin.lms.base.domain.model.online_course.SupervisorType;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class OnlineCourseTypeResolver implements CourseTypeResolver
{
  private static final EmployeeType EMPLOYEE = new EmployeeType();
  private static final SupervisorType SUPERVISOR = new SupervisorType();
  private static final ManagerType MANAGER = new ManagerType();

  @Override
  public CourseType resolve(String type) throws UnknownCourseTypeException
  {
    Validate.notBlank(type, "Type cannot be null or blank!");

    if (type.equalsIgnoreCase(EMPLOYEE.getType()))
    {
      return EMPLOYEE;
    }
    else if (type.equalsIgnoreCase(SUPERVISOR.getType()))
    {
      return SUPERVISOR;
    }
    else if (type.equalsIgnoreCase(MANAGER.getType()))
    {
      return MANAGER;
    }
    else
    {
      throw new UnknownCourseTypeException("Unknown course type");
    }
  }
}

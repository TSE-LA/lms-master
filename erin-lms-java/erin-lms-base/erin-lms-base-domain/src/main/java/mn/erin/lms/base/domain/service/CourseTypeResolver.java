package mn.erin.lms.base.domain.service;

import mn.erin.lms.base.domain.model.course.CourseType;

/**
 * @author Bat-Erdene Tsogoo.
 */
public interface CourseTypeResolver
{
  CourseType resolve(String type) throws UnknownCourseTypeException;
}

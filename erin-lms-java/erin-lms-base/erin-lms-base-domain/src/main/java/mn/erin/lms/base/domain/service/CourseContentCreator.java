package mn.erin.lms.base.domain.service;

import mn.erin.lms.base.domain.model.course.Course;

/**
 * @author Erdenetulga
 */
public interface CourseContentCreator
{
  boolean createContent(Course course);
}

package mn.erin.lms.legacy.domain.lms.service;

import java.util.List;

import mn.erin.lms.legacy.domain.lms.model.course.Course;
import mn.erin.lms.legacy.domain.lms.repository.LMSRepositoryException;

/**
 * author Naranbaatar Avir.
 */
public interface CourseService
{
  List<Course> search(String text, boolean searchByName, boolean searchByDescription)
      throws LMSRepositoryException;
}

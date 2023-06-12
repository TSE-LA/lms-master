package mn.erin.lms.unitel.domain.repository;

import java.util.Map;

import mn.erin.lms.base.aim.organization.DepartmentId;
import mn.erin.lms.base.domain.model.course.CourseId;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.unitel.domain.model.report.CourseReport;

/**
 * @author Bat-Erdene Tsogoo.
 */
public interface CourseReportRepository
{
  void save(CourseReport courseReport);

  CourseReport find(CourseId courseId, DepartmentId departmentId) throws LmsRepositoryException;

  void update(CourseId courseId, Map<String, Object> data) throws LmsRepositoryException;

  boolean delete(CourseId courseId);
}

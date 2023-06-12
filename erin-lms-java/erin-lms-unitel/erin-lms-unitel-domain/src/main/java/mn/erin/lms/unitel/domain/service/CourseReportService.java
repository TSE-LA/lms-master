package mn.erin.lms.unitel.domain.service;

import java.time.LocalDate;
import java.util.List;

import mn.erin.lms.base.aim.organization.DepartmentId;
import mn.erin.lms.base.domain.model.category.CourseCategoryId;
import mn.erin.lms.base.domain.model.course.Course;
import mn.erin.lms.base.domain.model.course.CourseType;
import mn.erin.lms.unitel.domain.model.report.CourseReport;

/**
 * @author Bat-Erdene Tsogoo.
 */
public interface CourseReportService
{
  List<CourseReport> generateCourseReports(DepartmentId departmentId, LocalDate startDate, LocalDate endDate);

  List<CourseReport> generateCourseReports(DepartmentId departmentId, CourseType courseType, LocalDate startDate, LocalDate endDate);

  List<CourseReport> generateCourseReports(DepartmentId departmentId, CourseCategoryId courseCategoryId, CourseType courseType,
      LocalDate startDate, LocalDate endDate);

  List<CourseReport> generateCourseReports(DepartmentId departmentId, CourseCategoryId courseCategoryId, LocalDate startDate, LocalDate endDate);

  List<Course> generateCourseReportWithoutRuntimeData(DepartmentId departmentId, LocalDate startDate, LocalDate endDate);

  List<Course> generateCourseReportsWithoutRuntimeData(DepartmentId departmentId, CourseCategoryId courseCategoryId, LocalDate startDate, LocalDate endDate);
}

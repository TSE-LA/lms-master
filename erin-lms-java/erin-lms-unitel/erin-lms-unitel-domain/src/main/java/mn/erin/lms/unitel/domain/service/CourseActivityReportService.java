package mn.erin.lms.unitel.domain.service;

import java.time.LocalDate;
import java.util.List;

import mn.erin.lms.base.aim.organization.DepartmentId;
import mn.erin.lms.base.aim.user.LearnerId;
import mn.erin.lms.unitel.domain.model.report.CourseReport;

/**
 * @author Munkh
 */
public interface CourseActivityReportService
{
  List<CourseReport> generateCourseReports(DepartmentId departmentId, LearnerId learnerId, LocalDate startDate, LocalDate endDate);
}

package mn.erin.lms.legacy.domain.lms.service;

import java.util.List;
import java.util.Map;

import mn.erin.lms.legacy.domain.lms.model.report.CourseReport;

/**
 * @author Munkh
 */
public interface CourseActivityReportService
{
  List<CourseReport> generateCourseActivityReports(String learnerId, Map<String, Object> courseProperties);
}

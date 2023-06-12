package mn.erin.lms.unitel.domain.service;

import mn.erin.lms.base.domain.service.LmsServiceRegistry;

/**
 * @author Bat-Erdene Tsogoo.
 */
public interface UnitelLmsServiceRegistry extends LmsServiceRegistry
{
  CourseReportService getCourseReportService();

  CourseActivityReportService getCourseActivityReportService();

  CourseAnalyticsService getCourseAnalyticsService();

}

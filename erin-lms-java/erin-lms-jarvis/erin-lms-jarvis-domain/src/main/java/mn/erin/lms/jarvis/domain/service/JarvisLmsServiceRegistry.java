package mn.erin.lms.jarvis.domain.service;


import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.jarvis.domain.report.service.CourseActivityReportService;
import mn.erin.lms.jarvis.domain.report.service.CourseAnalyticsService;
import mn.erin.lms.jarvis.domain.report.service.CourseReportService;

/**
 * @author Bat-Erdene Tsogoo.
 */
public interface JarvisLmsServiceRegistry extends LmsServiceRegistry
{
  CourseReportService getCourseReportService();

  CourseActivityReportService getCourseActivityReportService();

  CourseAnalyticsService getCourseAnalyticsService();

}

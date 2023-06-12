package mn.erin.lms.jarvis.domain.report.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import mn.erin.lms.base.aim.organization.DepartmentId;
import mn.erin.lms.base.aim.user.LearnerId;
import mn.erin.lms.base.domain.model.course.CourseId;
import mn.erin.lms.jarvis.domain.report.model.analytics.CourseAnalyticData;
import mn.erin.lms.jarvis.domain.report.model.analytics.CourseAnalytics;

/**
 * @author Bat-Erdene Tsogoo.
 */
public interface CourseAnalyticsService
{
  CourseAnalytics getCourseAnalytics(CourseId courseId, DepartmentId departmentId, LocalDate startDate, LocalDate endDate);

  List<Map<Integer, String>> getLearnersAssessmentDatas(String courseId, DepartmentId departmentId);

  byte[] generateSurveyAnalyticsData(CourseId courseId, DepartmentId departmentId, LocalDate startDate, LocalDate endDate, String userId);

  byte[] generateAnalyticsData(CourseId courseId, DepartmentId departmentId, LocalDate startDate, LocalDate endDate);

  /**
   * Lists the learner's analytics data by course
   *
   * @param courseId The unique ID of the course to return the analytic of
   * @param learnerId The unique ID of the learner
   * @return Course analytic data
   */
  CourseAnalyticData getCourseAnalytics(CourseId courseId, LearnerId learnerId);
}

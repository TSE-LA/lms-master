package mn.erin.lms.base.analytics.repository.mongo;

import java.time.LocalDate;
import java.util.List;

import mn.erin.lms.base.analytics.model.survey.SurveyAnalytic;
import mn.erin.lms.base.domain.model.assessment.AssessmentId;
import mn.erin.lms.base.domain.model.course.CourseId;

/**
 * @author Munkh
 */
public interface SurveyAnalyticsRepository
{
  List<SurveyAnalytic> getAllBySurveyId(AssessmentId assessmentId, LocalDate startDate, LocalDate endDate);

  List<SurveyAnalytic> getAllBySurveyAndCourseId(AssessmentId assessmentId, CourseId courseId, LocalDate startDate, LocalDate endDate);
}

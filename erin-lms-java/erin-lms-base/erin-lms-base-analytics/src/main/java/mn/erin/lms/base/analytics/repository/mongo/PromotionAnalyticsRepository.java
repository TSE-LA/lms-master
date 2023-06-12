package mn.erin.lms.base.analytics.repository.mongo;

import java.time.LocalDate;
import java.util.List;

import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.lms.base.analytics.exception.AnalyticsRepositoryException;
import mn.erin.lms.base.analytics.model.Analytic;
import mn.erin.lms.base.analytics.usecase.dto.LearnerActivity;
import mn.erin.lms.base.domain.model.course.CourseType;

/**
 * @author Munkh
 */
public interface PromotionAnalyticsRepository
{
  List<Analytic> getAllAnalytics(GroupId groupId, LocalDate startDate, LocalDate endDate) throws AnalyticsRepositoryException;
  List<Analytic> getAllAnalytics(GroupId groupId, String categoryName, LocalDate startDate, LocalDate endDate) throws AnalyticsRepositoryException;
  List<Analytic> getAllAnalytics(GroupId groupId, CourseType courseType, LocalDate startDate, LocalDate endDate) throws AnalyticsRepositoryException;
  List<Analytic> getAllAnalytics(GroupId groupId, String categoryName, CourseType courseType, LocalDate startDate, LocalDate endDate)
      throws AnalyticsRepositoryException;

  List<LearnerActivity> getAllActivities(GroupId groupId);
}

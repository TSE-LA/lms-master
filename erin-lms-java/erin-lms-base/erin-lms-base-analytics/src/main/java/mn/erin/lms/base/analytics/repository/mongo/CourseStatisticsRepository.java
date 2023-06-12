package mn.erin.lms.base.analytics.repository.mongo;

import java.time.LocalDate;
import java.util.List;

import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.lms.base.analytics.exception.AnalyticsRepositoryException;
import mn.erin.lms.base.analytics.model.Analytic;
import mn.erin.lms.base.domain.model.course.CourseId;

/**
 * @author Munkh
 */
public interface CourseStatisticsRepository
{
  List<Analytic> getStatistics(CourseId courseId, GroupId groupId, LocalDate startDate, LocalDate endDate) throws AnalyticsRepositoryException;
}

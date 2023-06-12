package mn.erin.lms.base.analytics.repository.mongo;

import java.time.LocalDate;
import java.util.List;

import mn.erin.lms.base.aim.user.LearnerId;
import mn.erin.lms.base.analytics.model.Analytic;
import mn.erin.lms.base.domain.model.category.CourseCategoryId;

/**
 * @author Munkh
 */
public interface LearnerAnalyticsRepository
{
  List<Analytic> getAnalytics(LearnerId learnerId, CourseCategoryId categoryId, LocalDate startDate, LocalDate endDate);
}

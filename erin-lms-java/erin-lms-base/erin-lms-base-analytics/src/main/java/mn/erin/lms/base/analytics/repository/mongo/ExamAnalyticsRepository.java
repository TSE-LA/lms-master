package mn.erin.lms.base.analytics.repository.mongo;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import mn.erin.lms.base.analytics.model.Analytic;

/**
 * @author Byambajav
 */
public interface ExamAnalyticsRepository
{
  List<Analytic> getAll(Set<String> groups, Set<String>  categories, Set<String>  statuses, Set<String>  types,  LocalDate startDate, LocalDate endDate);
}

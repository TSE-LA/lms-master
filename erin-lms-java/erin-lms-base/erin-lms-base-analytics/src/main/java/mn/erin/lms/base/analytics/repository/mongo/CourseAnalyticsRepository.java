package mn.erin.lms.base.analytics.repository.mongo;

import java.time.LocalDate;
import java.util.List;

import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.lms.base.analytics.model.Analytic;
import mn.erin.lms.base.domain.model.category.CourseCategoryId;
import mn.erin.lms.base.domain.model.course.CourseType;

/**
 * @author Munkh
 */
public interface CourseAnalyticsRepository
{
  List<Analytic> getAll(GroupId groupId, CourseCategoryId categoryId, LocalDate startDate, LocalDate endDate);
  List<Analytic> getAll(GroupId groupId, CourseCategoryId categoryId, CourseType courseType, LocalDate startDate, LocalDate endDate);
}

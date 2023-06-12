package mn.erin.lms.base.analytics.usecase.promotion;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.analytics.exception.AnalyticsRepositoryException;
import mn.erin.lms.base.analytics.model.Analytic;
import mn.erin.lms.base.analytics.repository.AnalyticsRepositoryRegistry;
import mn.erin.lms.base.analytics.usecase.dto.CourseAnalytics;
import mn.erin.lms.base.analytics.usecase.dto.CourseFilter;
import mn.erin.lms.base.domain.service.CourseTypeResolver;
import mn.erin.lms.base.domain.service.UnknownCourseTypeException;

/**
 * @author Munkh
 */
public class GeneratePromotionAnalytics implements UseCase<CourseFilter, CourseAnalytics>
{
  private final AnalyticsRepositoryRegistry analyticsRepositoryRegistry;
  private final CourseTypeResolver courseTypeResolver;

  public GeneratePromotionAnalytics(AnalyticsRepositoryRegistry analyticsRepositoryRegistry, CourseTypeResolver courseTypeResolver)
  {
    this.analyticsRepositoryRegistry = analyticsRepositoryRegistry;
    this.courseTypeResolver = courseTypeResolver;
  }

  @Override
  public CourseAnalytics execute(CourseFilter input) throws UseCaseException
  {
    Validate.notNull(input);
    Validate.notNull(input.getGroupId());

    List<Analytic> analytics;
    try
    {
      if (!StringUtils.isBlank(input.getCategoryName()) && !StringUtils.isBlank(input.getCourseType()))
      {
          analytics = analyticsRepositoryRegistry.getPromotionAnalyticsRepository().getAllAnalytics(
              GroupId.valueOf(input.getGroupId()),
              input.getCategoryName(),
              courseTypeResolver.resolve(input.getCourseType()),
              input.getDateFilter().getStartDate(),
              input.getDateFilter().getEndDate()
          );
      }
      else if (StringUtils.isBlank(input.getCategoryName()) && !StringUtils.isBlank(input.getCourseType()))
      {
          analytics = analyticsRepositoryRegistry.getPromotionAnalyticsRepository().getAllAnalytics(
              GroupId.valueOf(input.getGroupId()),
              courseTypeResolver.resolve(input.getCourseType()),
              input.getDateFilter().getStartDate(),
              input.getDateFilter().getEndDate()
          );
      }
      else if (!StringUtils.isBlank(input.getCategoryName()) && StringUtils.isBlank(input.getCourseType()))
      {
        analytics = analyticsRepositoryRegistry.getPromotionAnalyticsRepository().getAllAnalytics(
            GroupId.valueOf(input.getGroupId()),
            input.getCategoryName(),
            input.getDateFilter().getStartDate(),
            input.getDateFilter().getEndDate()
        );
      }
      else
      {
        analytics = analyticsRepositoryRegistry.getPromotionAnalyticsRepository().getAllAnalytics(
            GroupId.valueOf(input.getGroupId()),
            input.getDateFilter().getStartDate(),
            input.getDateFilter().getEndDate()
        );
      }
    }
    catch (UnknownCourseTypeException | AnalyticsRepositoryException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }

    return new CourseAnalytics(analytics);
  }
}

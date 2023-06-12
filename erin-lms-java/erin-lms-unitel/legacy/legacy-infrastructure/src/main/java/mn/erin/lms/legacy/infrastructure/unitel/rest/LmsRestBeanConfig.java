package mn.erin.lms.legacy.infrastructure.unitel.rest;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import mn.erin.lms.legacy.domain.lms.repository.CourseCategoryRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseEnrollmentRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseRepository;
import mn.erin.lms.legacy.domain.unitel.service.EmployeeAnalytics;
import mn.erin.lms.legacy.infrastructure.unitel.LmsServiceBeanConfig;
import mn.erin.lms.legacy.infrastructure.unitel.analytics.PromotionAnalyticsImpl;
import mn.erin.lms.legacy.infrastructure.unitel.rest.analytics.EmployeeAnalyticsRestApi;
import mn.erin.lms.legacy.infrastructure.unitel.rest.notification.NotificationRestApi;
import mn.erin.lms.legacy.infrastructure.unitel.rest.promotion.PromotionReadershipRestApi;
import mn.erin.lms.legacy.infrastructure.unitel.rest.promotion_category.PromotionCategoryRestApi;
import mn.erin.lms.legacy.infrastructure.unitel.rest.promotion_statistics.PromotionStatisticsRestApi;
import mn.erin.lms.legacy.infrastructure.unitel.rest.report.PromotionReportRestApi;

/**
 * @author EBazarragchaa
 */
@Configuration
@Import({ LmsServiceBeanConfig.class })
public class LmsRestBeanConfig
{
  @Bean(name = "legacyPromoCategoryRestApi")
  public PromotionCategoryRestApi promotionCategoryRestApi(CourseCategoryRepository courseCategoryRepository)
  {
    return new PromotionCategoryRestApi(courseCategoryRepository);
  }

  @Bean(name = "legacyNotificationRestApi")
  public NotificationRestApi notificationRestApi(CourseCategoryRepository courseCategoryRepository, CourseRepository courseRepository,
      CourseEnrollmentRepository courseEnrollmentRepository)
  {
    return new NotificationRestApi(courseCategoryRepository, courseRepository, courseEnrollmentRepository);
  }

  @Bean(name = "legacyPromotionStatisticsRestApi")
  public PromotionStatisticsRestApi promotionStatisticsRestApi(CourseRepository courseRepository, CourseCategoryRepository courseCategoryRepository,
      PromotionAnalyticsImpl promotionAnalytics)
  {
    return new PromotionStatisticsRestApi(courseRepository, courseCategoryRepository, promotionAnalytics);
  }

  @Bean(name = "legacyEmployeeAnalyticsRestApi")
  public EmployeeAnalyticsRestApi employeeAnalyticsRestApi(EmployeeAnalytics employeeAnalytics)
  {
    return new EmployeeAnalyticsRestApi(employeeAnalytics);
  }

  @Bean(name = "legacyPromoReportRestApi")
  public PromotionReportRestApi promotionReportRestApi()
  {
    return new PromotionReportRestApi();
  }

  @Bean(name = "legacyPromoReadershipRestApi")
  public PromotionReadershipRestApi promotionReadershipRestApi()
  {
    return new PromotionReadershipRestApi();
  }

  @Bean(name = "legacyPromoRelationRestApi")
  public PromotionRelationApi promotionRelationApi()
  {
    return new PromotionRelationApi();
  }
}

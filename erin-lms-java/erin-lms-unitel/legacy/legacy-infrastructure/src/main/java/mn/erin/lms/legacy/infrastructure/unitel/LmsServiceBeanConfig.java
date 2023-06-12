package mn.erin.lms.legacy.infrastructure.unitel;

import com.mongodb.MongoClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;

import mn.erin.lms.legacy.domain.lms.repository.CourseAssessmentRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseAuditRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseCategoryRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseEnrollmentRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseGroupRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseTestRepository;
import mn.erin.lms.legacy.domain.lms.service.CourseActivityReportService;
import mn.erin.lms.legacy.domain.lms.service.CourseReportService;
import mn.erin.lms.legacy.domain.lms.service.EnrollmentStateService;
import mn.erin.lms.legacy.domain.scorm.repository.RuntimeDataRepository;
import mn.erin.lms.legacy.domain.unitel.service.EmployeeAnalytics;
import mn.erin.lms.legacy.domain.unitel.service.PromoExcelReportGenerator;
import mn.erin.lms.legacy.domain.unitel.service.PromotionAnalytics;
import mn.erin.lms.legacy.infrastructure.lms.repository.ErinLmsRepositoryBeanConfig;
import mn.erin.lms.legacy.infrastructure.scorm.repository.mongo.MongoSCORMBeanConfig;
import mn.erin.lms.legacy.infrastructure.unitel.analytics.EmployeeAnalyticsImpl;
import mn.erin.lms.legacy.infrastructure.unitel.analytics.PromotionAnalyticsImpl;
import mn.erin.lms.legacy.infrastructure.unitel.notification.mail.LegacyUnitelEmailBeanConfig;
import mn.erin.lms.legacy.infrastructure.unitel.notification.sms.UnitelSmsBeanConfig;
import mn.erin.lms.legacy.infrastructure.unitel.report.PromoReportExcelGeneratorImpl;
import mn.erin.lms.legacy.infrastructure.unitel.report.PromotionActivityReportsImpl;
import mn.erin.lms.legacy.infrastructure.unitel.report.PromotionReportsImpl;
import mn.erin.lms.legacy.infrastructure.unitel.scorm.EnrollmentStateServiceImpl;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Configuration
@Import({ ErinLmsRepositoryBeanConfig.class, MongoSCORMBeanConfig.class, LegacyUnitelEmailBeanConfig.class, UnitelSmsBeanConfig.class })
public class LmsServiceBeanConfig
{
  @Bean(name = "legacyPromoAnalytics")
  public PromotionAnalyticsImpl promotionAnalytics(MongoClient mongoClient, RuntimeDataRepository legacyRuntimeDataStore)
  {
    MongoTemplate mongoTemplate = new MongoTemplate(mongoClient, "LMS");
    return new PromotionAnalyticsImpl(mongoTemplate, legacyRuntimeDataStore);
  }

  @Bean(name = "legacyEnrollmentStateService")
  public EnrollmentStateService enrollmentStateService()
  {
    return new EnrollmentStateServiceImpl();
  }

  @Bean(name = "legacyEmployeeAnalytics")
  public EmployeeAnalytics employeeAnalytics(CourseRepository courseRepository,
      CourseEnrollmentRepository enrollmentRepository, PromotionAnalytics promotionAnalytics)
  {
    return new EmployeeAnalyticsImpl(courseRepository, enrollmentRepository, promotionAnalytics);
  }

  @Bean(name = "legacyCourseReportService")
  public CourseReportService courseReportService(CourseRepository courseRepository, PromotionAnalytics promotionAnalytics,
      CourseCategoryRepository courseCategoryRepository, CourseAssessmentRepository courseAssessmentRepository, CourseTestRepository courseTestRepository,
      CourseEnrollmentRepository courseEnrollmentRepository, CourseGroupRepository courseGroupRepository, CourseAuditRepository courseAuditRepository)
  {
    return new PromotionReportsImpl(courseRepository, promotionAnalytics, courseCategoryRepository,
        courseAssessmentRepository, courseTestRepository, courseEnrollmentRepository, courseGroupRepository,courseAuditRepository);
  }

  @Bean(name = "legacyCourseActivityReportService")
  public CourseActivityReportService courseActivityReportService(RuntimeDataRepository legacyRuntimeDataStore, CourseRepository courseRepository,
      CourseCategoryRepository courseCategoryRepository,
      CourseEnrollmentRepository courseEnrollmentRepository)
  {
    return new PromotionActivityReportsImpl(courseCategoryRepository, legacyRuntimeDataStore, courseRepository, courseEnrollmentRepository);
  }

  @Bean(name = "legacyPromoExcelReportGenerator")
  public PromoExcelReportGenerator promoExcelReportGenerator()
  {
    return new PromoReportExcelGeneratorImpl();
  }
}

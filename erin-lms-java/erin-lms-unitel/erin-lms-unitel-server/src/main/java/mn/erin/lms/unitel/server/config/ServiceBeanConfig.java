package mn.erin.lms.unitel.server.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import mn.erin.common.mail.EmailService;
import mn.erin.common.sms.SmsMessageFactory;
import mn.erin.common.sms.SmsSender;
import mn.erin.lms.base.aim.AccessIdentityManagement;
import mn.erin.lms.base.aim.AuthorIdProvider;
import mn.erin.lms.base.aim.AuthorIdProviderImpl;
import mn.erin.lms.base.aim.DepartmentServiceImpl;
import mn.erin.lms.base.aim.LmsDepartmentService;
import mn.erin.lms.base.aim.LmsUserService;
import mn.erin.lms.base.dms.DmsCodecConverterService;
import mn.erin.lms.base.dms.DmsImageConverterService;
import mn.erin.lms.base.dms.DmsThumbnailService;
import mn.erin.lms.base.dms.LmsFileSystemServiceImpl;
import mn.erin.lms.base.domain.repository.CourseRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.CodecService;
import mn.erin.lms.base.domain.service.CourseContentCreator;
import mn.erin.lms.base.domain.service.CourseLauncher;
import mn.erin.lms.base.domain.service.CoursePublisher;
import mn.erin.lms.base.domain.service.CourseTypeResolver;
import mn.erin.lms.base.domain.service.ImageService;
import mn.erin.lms.base.domain.service.LmsFileSystemService;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.service.LmsTaskScheduler;
import mn.erin.lms.base.domain.service.NotificationService;
import mn.erin.lms.base.domain.service.OrganizationIdProvider;
import mn.erin.lms.base.domain.service.ProgressTrackingService;
import mn.erin.lms.base.domain.service.QuestionnaireService;
import mn.erin.lms.base.domain.service.TemporaryFileApi;
import mn.erin.lms.base.domain.service.TemporaryFileApiImpl;
import mn.erin.lms.base.domain.service.ThumbnailService;
import mn.erin.lms.base.domain.service.UseCaseResolver;
import mn.erin.lms.base.domain.service.exam.ExamScheduledTaskRemover;
import mn.erin.lms.base.domain.service.exam.ExamScheduledTaskRemoverImpl;
import mn.erin.lms.base.domain.service.exam.ExamExpirationService;
import mn.erin.lms.base.domain.service.exam.ExamExpirationServiceImpl;
import mn.erin.lms.base.domain.service.exam.ExamInteractionService;
import mn.erin.lms.base.domain.service.exam.ExamPublicationService;
import mn.erin.lms.base.domain.service.exam.ExamPublicationServiceImpl;
import mn.erin.lms.base.domain.service.exam.ExamStartService;
import mn.erin.lms.base.domain.service.exam.ExamStartServiceImpl;
import mn.erin.lms.base.domain.service.exam.ExamUserServiceImpl;
import mn.erin.lms.base.domain.service.exam.ScheduledTaskInitializer;
import mn.erin.lms.base.domain.service.exam.ScheduledTaskInitializerImpl;
import mn.erin.lms.base.domain.service.impl.UseCaseResolverImpl;
import mn.erin.lms.base.domain.usecase.GetCoursesUseCaseDelegator;
import mn.erin.lms.base.domain.usecase.GetSuggestedClassroomCoursesUseCaseDelegator;
import mn.erin.lms.base.domain.usecase.SearchCourseUseCaseDelegator;
import mn.erin.lms.base.domain.usecase.course.GetCourses;
import mn.erin.lms.base.domain.usecase.course.GetSuggestedCourses;
import mn.erin.lms.base.domain.usecase.course.SearchCourse;
import mn.erin.lms.base.rest.scheduler.SpringThreadPoolTaskScheduler;
import mn.erin.lms.base.scorm.LmsSCORMContentService;
import mn.erin.lms.base.scorm.LmsSCORMRuntimeService;
import mn.erin.lms.base.scorm.SCOModel;
import mn.erin.lms.base.scorm.repository.SCORMRepositoryRegistry;
import mn.erin.lms.base.scorm.service.AssessmentReportService;
import mn.erin.lms.base.scorm.service.ScormContentCreator;
import mn.erin.lms.unitel.UnitelNotificationBeanConfig;
import mn.erin.lms.unitel.UnitelNotificationService;
import mn.erin.lms.unitel.domain.repository.UnitelLmsRepositoryRegistry;
import mn.erin.lms.unitel.domain.service.AssessmentReportServiceImpl;
import mn.erin.lms.unitel.domain.service.CourseActivityReportService;
import mn.erin.lms.unitel.domain.service.CourseActivityReportServiceImpl;
import mn.erin.lms.unitel.domain.service.CourseAnalyticsService;
import mn.erin.lms.unitel.domain.service.CourseAnalyticsServiceImpl;
import mn.erin.lms.unitel.domain.service.CourseReportService;
import mn.erin.lms.unitel.domain.service.CourseReportServiceImpl;
import mn.erin.lms.unitel.domain.service.EmployeeAnalyticsService;
import mn.erin.lms.unitel.domain.service.EmployeeAnalyticsServiceImpl;
import mn.erin.lms.unitel.domain.service.MainCourseTypeResolver;
import mn.erin.lms.unitel.domain.service.PromotionQuestionnaireService;
import mn.erin.lms.unitel.domain.service.UnitelOrganizationIdProvider;
import mn.erin.lms.unitel.server.registry.UnitelLmsServiceRegistryImpl;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Configuration
@Import({ UnitelNotificationBeanConfig.class })
public class ServiceBeanConfig
{
  @Autowired
  private LmsUserService lmsUserService;

  @Autowired
  private AccessIdentityManagement accessIdentityManagement;

  @Autowired
  private LmsRepositoryRegistry lmsRepositoryRegistry;

  @Bean
  public LmsServiceRegistry serviceRegistry()
  {
    return new UnitelLmsServiceRegistryImpl();
  }

  @Bean
  public CourseReportService courseReportService()
  {
    return new CourseReportServiceImpl(lmsUserService, lmsDepartmentService());
  }

  @Bean
  public CourseActivityReportService courseActivityReportService()
  {
    return new CourseActivityReportServiceImpl(lmsUserService, lmsDepartmentService(), organizationIdProvider());
  }

  @Bean
  public AssessmentReportService assessmentReportService()
  {
    return new AssessmentReportServiceImpl((UnitelLmsRepositoryRegistry) lmsRepositoryRegistry);
  }

  @Bean
  public CourseAnalyticsService courseAnalyticsService()
  {
    return new CourseAnalyticsServiceImpl(lmsDepartmentService());
  }

  @Bean
  public CoursePublisher coursePublisher()
  {
    return new LmsSCORMContentService();
  }

  @Bean
  public CourseLauncher<List<SCOModel>> courseLauncher()
  {
    return new LmsSCORMContentService();
  }

  @Bean
  public AuthorIdProvider authorIdProvider()
  {
    return new AuthorIdProviderImpl(accessIdentityManagement);
  }

  @Bean
  public CourseTypeResolver courseTypeResolver()
  {
    return new MainCourseTypeResolver();
  }

  @Bean
  public LmsDepartmentService lmsDepartmentService()
  {
    return new DepartmentServiceImpl(accessIdentityManagement);
  }

  @Bean
  public LmsTaskScheduler lmsTaskScheduler()
  {
    return new SpringThreadPoolTaskScheduler(lmsRepositoryRegistry.getScheduledTaskRepository(), new ThreadPoolTaskScheduler());
  }

  @Bean
  public ScheduledTaskInitializer scheduledTaskInitializer()
  {
    return new ScheduledTaskInitializerImpl(lmsRepositoryRegistry.getScheduledTaskRepository(), serviceRegistry());
  }

  @Bean
  public ExamStartService examStartService()
  {
    return new ExamStartServiceImpl(lmsRepositoryRegistry, lmsTaskScheduler());
  }

  @Bean
  public ExamPublicationService examPublicationService()
  {
    return new ExamPublicationServiceImpl(lmsRepositoryRegistry, lmsTaskScheduler());
  }

  @Bean
  public ExamExpirationService examExpirationService()
  {
    return new ExamExpirationServiceImpl(lmsRepositoryRegistry, lmsTaskScheduler());
  }

  @Bean
  public ExamInteractionService examInteractionService()
  {
    return new ExamUserServiceImpl(lmsRepositoryRegistry, accessIdentityManagement);
  }

  @Bean
  public ExamScheduledTaskRemover examScheduledTaskRemover()
  {
    return new ExamScheduledTaskRemoverImpl(lmsRepositoryRegistry);
  }

  @Bean
  public NotificationService notificationService(EmailService emailService, SmsSender smsSender,
      SmsMessageFactory smsMessageFactory)
  {
    return new UnitelNotificationService(lmsUserService, emailService, smsSender, smsMessageFactory, accessIdentityManagement);
  }

  @Bean
  public OrganizationIdProvider organizationIdProvider()
  {
    return new UnitelOrganizationIdProvider();
  }

  @Bean
  public ProgressTrackingService progressTrackingService()
  {
    return new LmsSCORMRuntimeService();
  }

  @Bean
  public LmsFileSystemService lmsFileSystemService()
  {
    return new LmsFileSystemServiceImpl();
  }


  @Bean
  public UseCaseResolver useCaseResolver()
  {
    UseCaseResolverImpl useCaseResolver = new UseCaseResolverImpl();
    useCaseResolver.registerUseCase(GetSuggestedCourses.class.getName(), new GetSuggestedClassroomCoursesUseCaseDelegator());
    useCaseResolver.registerUseCase(GetCourses.class.getName(), new GetCoursesUseCaseDelegator());
    useCaseResolver.registerUseCase(SearchCourse.class.getName(), new SearchCourseUseCaseDelegator());

    return useCaseResolver;
  }

  @Bean
  public QuestionnaireService questionnaireService()
  {
    return new PromotionQuestionnaireService();
  }

  @Bean
  public EmployeeAnalyticsService employeeAnalyticsService()
  {
    return new EmployeeAnalyticsServiceImpl();
  }

  @Bean
  public ThumbnailService thumbnailService(SCORMRepositoryRegistry scormRepositoryRegistry, CourseRepository courseRepository)
  {
    return new DmsThumbnailService(imageService(), lmsFileSystemService(),
        scormRepositoryRegistry.getSCORMContentRepository(), courseRepository, temporaryFileApi());
  }

  @Bean
  public ImageService imageService()
  {
    return new DmsImageConverterService();
  }

  @Bean
  public CodecService codecService()
  {
    return new DmsCodecConverterService();
  }

  @Bean
  public CourseContentCreator courseContentCreator(
    LmsFileSystemService lmsFileSystemService,
    SCORMRepositoryRegistry scormRepositoryRegistry
  )
  {
    return new ScormContentCreator(lmsFileSystemService, lmsRepositoryRegistry, scormRepositoryRegistry);
  }

  @Bean
  public TemporaryFileApi temporaryFileApi()
  {
    return new TemporaryFileApiImpl();
  }
}

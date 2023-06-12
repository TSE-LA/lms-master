package mn.erin.lms.unitel.server.registry;

import javax.inject.Inject;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.lms.base.aim.AccessIdentityManagement;
import mn.erin.lms.base.aim.AuthorIdProvider;
import mn.erin.lms.base.aim.LmsDepartmentService;
import mn.erin.lms.base.aim.LmsUserService;
import mn.erin.lms.base.domain.service.CourseLauncher;
import mn.erin.lms.base.domain.service.CoursePublisher;
import mn.erin.lms.base.domain.service.CourseTypeResolver;
import mn.erin.lms.base.domain.service.LmsFileSystemService;
import mn.erin.lms.base.domain.service.LmsTaskScheduler;
import mn.erin.lms.base.domain.service.NotificationService;
import mn.erin.lms.base.domain.service.OrganizationIdProvider;
import mn.erin.lms.base.domain.service.ProgressTrackingService;
import mn.erin.lms.base.domain.service.QuestionnaireService;
import mn.erin.lms.base.domain.service.TemporaryFileApi;
import mn.erin.lms.base.domain.service.ThumbnailService;
import mn.erin.lms.base.domain.service.UseCaseResolver;
import mn.erin.lms.base.domain.service.exam.ExamScheduledTaskRemover;
import mn.erin.lms.base.domain.service.exam.ExamExpirationService;
import mn.erin.lms.base.domain.service.exam.ExamInteractionService;
import mn.erin.lms.base.domain.service.exam.ExamPublicationService;
import mn.erin.lms.base.domain.service.exam.ExamStartService;
import mn.erin.lms.base.domain.service.exam.ScheduledTaskInitializer;
import mn.erin.lms.unitel.domain.service.CourseActivityReportService;
import mn.erin.lms.unitel.domain.service.CourseAnalyticsService;
import mn.erin.lms.unitel.domain.service.CourseReportService;
import mn.erin.lms.unitel.domain.service.UnitelLmsServiceRegistry;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class UnitelLmsServiceRegistryImpl implements UnitelLmsServiceRegistry, BeanFactoryAware
{
  private CourseReportService courseReportService;
  private CourseActivityReportService courseActivityReportService;
  private CourseAnalyticsService courseAnalyticsService;
  private CoursePublisher coursePublisher;
  private CourseLauncher<?> courseLauncher;
  private AuthorIdProvider authorIdProvider;
  private CourseTypeResolver courseTypeResolver;
  private LmsDepartmentService lmsDepartmentService;
  private LmsTaskScheduler lmsTaskScheduler;
  private NotificationService notificationService;
  private OrganizationIdProvider organizationIdProvider;
  private ProgressTrackingService progressTrackingService;
  private LmsUserService lmsUserService;
  private LmsFileSystemService lmsFileSystemService;
  private UseCaseResolver useCaseResolver;
  private QuestionnaireService questionnaireService;
  private ThumbnailService thumbnailService;
  private AuthenticationService authenticationService;
  private AuthorizationService authorizationService;
  private AccessIdentityManagement accessIdentityManagement;
  private TemporaryFileApi temporaryFileApi;
  private BeanFactory beanFactory;

  @Inject
  public void setCourseReportService(CourseReportService courseReportService)
  {
    this.courseReportService = courseReportService;
  }

  @Inject
  public void setCourseActivityReportService(CourseActivityReportService courseActivityReportService)
  {
    this.courseActivityReportService = courseActivityReportService;
  }

  @Inject
  public void setCourseAnalyticsService(CourseAnalyticsService courseAnalyticsService)
  {
    this.courseAnalyticsService = courseAnalyticsService;
  }

  @Inject
  public void setCoursePublisher(CoursePublisher coursePublisher)
  {
    this.coursePublisher = coursePublisher;
  }

  @Inject
  public void setCourseLauncher(CourseLauncher<?> courseLauncher)
  {
    this.courseLauncher = courseLauncher;
  }

  @Inject
  public void setAuthorIdProvider(AuthorIdProvider authorIdProvider)
  {
    this.authorIdProvider = authorIdProvider;
  }

  @Inject
  public void setCourseTypeResolver(CourseTypeResolver courseTypeResolver)
  {
    this.courseTypeResolver = courseTypeResolver;
  }

  @Inject
  public void setLmsDepartmentService(LmsDepartmentService lmsDepartmentService)
  {
    this.lmsDepartmentService = lmsDepartmentService;
  }

  @Inject
  public void setNotificationService(NotificationService notificationService)
  {
    this.notificationService = notificationService;
  }

  @Inject
  public void setOrganizationIdProvider(OrganizationIdProvider organizationIdProvider)
  {
    this.organizationIdProvider = organizationIdProvider;
  }

  @Inject
  public void setProgressTrackingService(ProgressTrackingService progressTrackingService)
  {
    this.progressTrackingService = progressTrackingService;
  }

  @Inject
  public void setLmsUserService(LmsUserService lmsUserService)
  {
    this.lmsUserService = lmsUserService;
  }

  @Inject
  public void setLmsFileSystemService(LmsFileSystemService lmsFileSystemService)
  {
    this.lmsFileSystemService = lmsFileSystemService;
  }

  @Inject
  public void setUseCaseResolver(UseCaseResolver useCaseResolver)
  {
    this.useCaseResolver = useCaseResolver;
  }

  @Inject
  public void setQuestionnaireService(QuestionnaireService questionnaireService)
  {
    this.questionnaireService = questionnaireService;
  }

  @Inject
  public void setThumbnailService(ThumbnailService thumbnailService)
  {
    this.thumbnailService = thumbnailService;
  }

  @Inject
  public void setAuthenticationService(AuthenticationService authenticationService)
  {
    this.authenticationService = authenticationService;
  }

  @Inject
  public void setAuthorizationService(AuthorizationService authorizationService)
  {
    this.authorizationService = authorizationService;
  }

  @Inject
  public void setAccessIdentityManagement(AccessIdentityManagement accessIdentityManagement)
  {
    this.accessIdentityManagement = accessIdentityManagement;
  }

  @Inject
  public void setLmsTaskScheduler(LmsTaskScheduler lmsTaskScheduler)
  {
    this.lmsTaskScheduler = lmsTaskScheduler;
  }

  @Inject
  public void setTemporaryFileApi(TemporaryFileApi temporaryFileApi)
  {
    this.temporaryFileApi = temporaryFileApi;
  }

  @Override
  public CourseReportService getCourseReportService()
  {
    return this.courseReportService;
  }

  @Override
  public CourseActivityReportService getCourseActivityReportService()
  {
    return this.courseActivityReportService;
  }

  @Override
  public CourseAnalyticsService getCourseAnalyticsService()
  {
    return this.courseAnalyticsService;
  }

  @Override
  public CoursePublisher getCoursePublisher()
  {
    return this.coursePublisher;
  }

  @Override
  public CourseLauncher<?> getCourseLauncher()
  {
    return this.courseLauncher;
  }

  @Override
  public AuthorIdProvider getAuthorIdProvider()
  {
    return this.authorIdProvider;
  }

  @Override
  public CourseTypeResolver getCourseTypeResolver()
  {
    return this.courseTypeResolver;
  }

  @Override
  public LmsDepartmentService getDepartmentService()
  {
    return this.lmsDepartmentService;
  }

  @Override
  public LmsTaskScheduler getLmsScheduler()
  {
    return this.lmsTaskScheduler;
  }

  @Override
  public NotificationService getNotificationService()
  {
    return this.notificationService;
  }

  @Override
  public OrganizationIdProvider getOrganizationIdProvider()
  {
    return this.organizationIdProvider;
  }

  @Override
  public ProgressTrackingService getProgressTrackingService()
  {
    return this.progressTrackingService;
  }

  @Override
  public LmsUserService getLmsUserService()
  {
    return this.lmsUserService;
  }

  @Override
  public LmsFileSystemService getLmsFileSystemService()
  {
    return this.lmsFileSystemService;
  }



  @Override
  public UseCaseResolver getUseCaseResolver()
  {
    return this.useCaseResolver;
  }

  @Override
  public QuestionnaireService getQuestionnaireService()
  {
    return this.questionnaireService;
  }

  @Override
  public ThumbnailService getThumbnailService()
  {
    return this.thumbnailService;
  }

  @Override
  public AuthenticationService getAuthenticationService()
  {
    return this.authenticationService;
  }

  @Override
  public AuthorizationService getAuthorizationService()
  {
    return this.authorizationService;
  }

  @Override
  public AccessIdentityManagement getAccessIdentityManagement()
  {
    return this.accessIdentityManagement;
  }

  @Override
  public TemporaryFileApi getTemporaryFileApi()
  {
    return this.temporaryFileApi;
  }

  @Override
  public ExamPublicationService getExamPublicationService()
  {
    return beanFactory.getBean(ExamPublicationService.class);
  }

  @Override
  public ExamStartService getExamStartService()
  {
    return beanFactory.getBean(ExamStartService.class);
  }

  @Override
  public ExamExpirationService getExamExpirationService()
  {
    return beanFactory.getBean(ExamExpirationService.class);
  }

  @Override
  public ScheduledTaskInitializer getScheduledTaskInitializer()
  {
    return beanFactory.getBean(ScheduledTaskInitializer.class);
  }

  @Override
  public ExamInteractionService getExamInteractionService()
  {
    return beanFactory.getBean(ExamInteractionService.class);
  }

  @Override
  public ExamScheduledTaskRemover getExamScheduledTaskRemover()
  {
    return beanFactory.getBean(ExamScheduledTaskRemover.class);
  }

  @Override
  public void setBeanFactory(BeanFactory beanFactory) throws BeansException
  {
    this.beanFactory = beanFactory;
  }
}

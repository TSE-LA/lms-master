package mn.erin.lms.base.domain.service;

import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.lms.base.aim.AccessIdentityManagement;
import mn.erin.lms.base.aim.AuthorIdProvider;
import mn.erin.lms.base.aim.LmsDepartmentService;
import mn.erin.lms.base.aim.LmsUserService;
import mn.erin.lms.base.domain.service.exam.ExamScheduledTaskRemover;
import mn.erin.lms.base.domain.service.exam.ExamExpirationService;
import mn.erin.lms.base.domain.service.exam.ExamInteractionService;
import mn.erin.lms.base.domain.service.exam.ExamPublicationService;
import mn.erin.lms.base.domain.service.exam.ExamStartService;
import mn.erin.lms.base.domain.service.exam.ScheduledTaskInitializer;

/**
 * @author Bat-Erdene Tsogoo.
 */
public interface LmsServiceRegistry
{
  CoursePublisher getCoursePublisher();

  CourseLauncher<?> getCourseLauncher();

  AuthorIdProvider getAuthorIdProvider();

  CourseTypeResolver getCourseTypeResolver();

  LmsDepartmentService getDepartmentService();

  LmsTaskScheduler getLmsScheduler();

  NotificationService getNotificationService();

  OrganizationIdProvider getOrganizationIdProvider();

  ProgressTrackingService getProgressTrackingService();

  LmsUserService getLmsUserService();

  LmsFileSystemService getLmsFileSystemService();

  UseCaseResolver getUseCaseResolver();

  QuestionnaireService getQuestionnaireService();

  ThumbnailService getThumbnailService();

  AuthenticationService getAuthenticationService();

  AuthorizationService getAuthorizationService();

  AccessIdentityManagement getAccessIdentityManagement();

  ExamStartService getExamStartService();

  ExamExpirationService getExamExpirationService();

  ExamPublicationService getExamPublicationService();

  ScheduledTaskInitializer getScheduledTaskInitializer();

  ExamInteractionService getExamInteractionService();

  TemporaryFileApi getTemporaryFileApi();

  ExamScheduledTaskRemover getExamScheduledTaskRemover();
}

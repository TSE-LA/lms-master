package mn.erin.lms.jarvis.domain;

import java.util.List;
import java.util.Map;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;

import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.lms.base.aim.organization.OrganizationId;
import mn.erin.lms.base.aim.user.LmsAdmin;
import mn.erin.lms.base.aim.user.LmsEmployee;
import mn.erin.lms.base.aim.user.LmsManager;
import mn.erin.lms.base.aim.user.LmsSupervisor;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.service.UseCaseResolver;
import mn.erin.lms.base.domain.usecase.UseCaseDelegator;
import mn.erin.lms.base.domain.usecase.course.GetAdminCourses;
import mn.erin.lms.base.domain.usecase.course.GetCourses;
import mn.erin.lms.base.domain.usecase.course.GetDepartmentSuggestedCourses;
import mn.erin.lms.base.domain.usecase.course.GetEnrolledCourses;
import mn.erin.lms.base.domain.usecase.course.GetEnrolledSuggestedCourses;
import mn.erin.lms.base.domain.usecase.course.GetSuggestedCourses;
import mn.erin.lms.base.domain.usecase.course.SearchCourse;
import mn.erin.lms.base.domain.usecase.course.SearchDepartmentCourses;
import mn.erin.lms.base.domain.usecase.course.SearchEnrolledCourses;
import mn.erin.lms.base.domain.usecase.course.dto.CourseDto;
import mn.erin.lms.base.domain.usecase.course.dto.GetCoursesInput;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class SetupLms
{
  public static final TenantId TENANT_JARVIS = TenantId.valueOf("jarvis");
  private static final String QUESTION_ROOT_GROUP_NAME = "root";
  private static final String EXAM_ROOT_GROUP_NAME = "root group exam";

  private final LmsRepositoryRegistry lmsRepositoryRegistry;
  private final LmsServiceRegistry lmsServiceRegistry;

  public SetupLms(LmsRepositoryRegistry lmsRepositoryRegistry, LmsServiceRegistry lmsServiceRegistry)
  {
    this.lmsRepositoryRegistry = lmsRepositoryRegistry;
    this.lmsServiceRegistry = lmsServiceRegistry;
  }

  public void execute()
  {
    setLoggersToWarn();
    setUseCaseDelegators();
    createRootQuestionGroup();
    createRootExamGroup();
    lmsServiceRegistry.getScheduledTaskInitializer().startPendingTasks();
  }

  private void createRootQuestionGroup()
  {
    OrganizationId organizationId = OrganizationId.valueOf(lmsServiceRegistry.getOrganizationIdProvider().getOrganizationId());

    if (lmsRepositoryRegistry.getQuestionGroupRepository().getAllByOrganizationId(organizationId).isEmpty())
    {
      lmsRepositoryRegistry.getQuestionGroupRepository().create(null, QUESTION_ROOT_GROUP_NAME, organizationId, "question root group");
    }
  }

  private void createRootExamGroup()
  {
    OrganizationId organizationId = OrganizationId.valueOf(lmsServiceRegistry.getOrganizationIdProvider().getOrganizationId());

    if (lmsRepositoryRegistry.getExamGroupRepository().findByOrganizationId(organizationId.getId()).isEmpty())
    {
      lmsRepositoryRegistry.getExamGroupRepository().create(null, EXAM_ROOT_GROUP_NAME, organizationId, "Root group");
    }
  }

  @SuppressWarnings("unchecked")
  private void setUseCaseDelegators()
  {
    UseCaseResolver useCaseResolver = lmsServiceRegistry.getUseCaseResolver();
    //Get course
    UseCaseDelegator<GetCoursesInput, List<CourseDto>> getCoursesDelegator =
        (UseCaseDelegator<GetCoursesInput, List<CourseDto>>) useCaseResolver.getUseCaseDelegator(GetCourses.class.getName());
    GetEnrolledCourses getEnrolledCourses = new GetEnrolledCourses(lmsRepositoryRegistry, lmsServiceRegistry);

    getCoursesDelegator.register(LmsAdmin.class, new GetAdminCourses(lmsRepositoryRegistry, lmsServiceRegistry));
    getCoursesDelegator.register(LmsEmployee.class, getEnrolledCourses);
    getCoursesDelegator.register(LmsSupervisor.class, getEnrolledCourses);
    getCoursesDelegator.register(LmsManager.class, getEnrolledCourses);

    //Get suggested course
    UseCaseDelegator<GetCoursesInput, List<CourseDto>> getSuggestedCoursesDelegator =
        (UseCaseDelegator<GetCoursesInput, List<CourseDto>>) useCaseResolver.getUseCaseDelegator(GetSuggestedCourses.class.getName());
    GetEnrolledSuggestedCourses getEnrolledSuggestedCourses = new GetEnrolledSuggestedCourses(lmsRepositoryRegistry, lmsServiceRegistry);

    getSuggestedCoursesDelegator.register(LmsAdmin.class, new GetDepartmentSuggestedCourses(lmsRepositoryRegistry, lmsServiceRegistry));
    getSuggestedCoursesDelegator.register(LmsEmployee.class, getEnrolledSuggestedCourses);
    getSuggestedCoursesDelegator.register(LmsSupervisor.class, getEnrolledSuggestedCourses);
    getSuggestedCoursesDelegator.register(LmsManager.class, getEnrolledSuggestedCourses);

    //Search course
    UseCaseDelegator<Map<String, Object>, List<CourseDto>> searchCourseDelegator =
        (UseCaseDelegator<Map<String, Object>, List<CourseDto>>) useCaseResolver.getUseCaseDelegator(SearchCourse.class.getName());

    SearchEnrolledCourses searchEnrolledCourses = new SearchEnrolledCourses(lmsRepositoryRegistry, lmsServiceRegistry);

    searchCourseDelegator.register(LmsAdmin.class, new SearchDepartmentCourses(lmsRepositoryRegistry, lmsServiceRegistry));
    searchCourseDelegator.register(LmsEmployee.class, searchEnrolledCourses);
    searchCourseDelegator.register(LmsSupervisor.class, searchEnrolledCourses);
    searchCourseDelegator.register(LmsManager.class, searchEnrolledCourses);
  }

  // Sets every logger level to WARN
  private void setLoggersToWarn()
  {
    ILoggerFactory loggerContext = LoggerFactory.getILoggerFactory();
    if (loggerContext instanceof LoggerContext)
    {
      for (Logger logger : ((LoggerContext) loggerContext).getLoggerList())
      {
        logger.setLevel(Level.ERROR);
      }
    }
  }
}

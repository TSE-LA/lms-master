package mn.erin.lms.unitel.domain;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;

import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.group.Group;
import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.domain.aim.model.role.RoleId;
import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.aim.repository.GroupRepository;
import mn.erin.domain.aim.repository.MembershipRepository;
import mn.erin.lms.base.aim.organization.OrganizationId;
import mn.erin.lms.base.aim.role.LmsRole;
import mn.erin.lms.base.domain.model.category.CourseCategory;
import mn.erin.lms.base.domain.model.category.CourseCategoryId;
import mn.erin.lms.base.domain.repository.CourseCategoryRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.repository.QuestionGroupRepository;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.service.UseCaseResolver;
import mn.erin.lms.base.domain.usecase.UseCaseDelegator;
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
import mn.erin.lms.unitel.domain.model.user.UnitelAdmin;
import mn.erin.lms.unitel.domain.model.user.UnitelEmployee;
import mn.erin.lms.unitel.domain.model.user.UnitelManager;
import mn.erin.lms.unitel.domain.model.user.UnitelSupervisor;
import mn.erin.lms.unitel.domain.usecase.GetAdminCourses;

import static mn.erin.lms.unitel.domain.Constants.CATEGORY_CLASSROOM_COURSE;
import static mn.erin.lms.unitel.domain.Constants.CATEGORY_ONLINE_COURSE;
import static mn.erin.lms.unitel.domain.Constants.ORGANIZATION;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class SetupLms
{
  private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(SetupLms.class);

  public static final TenantId TENANT_UNITEL = TenantId.valueOf("unitel");

  private static final String[] COURSE_CATEGORIES = {
      "Ерөнхий ур чадварын сургалт", "Мэргэжлийн ур чадварын сургалт", "Хувь хүний хөгжлийн сургалт", "Программын сургалт", "Нөхцөл, урамшууллын сургалт"
  };

  private static final String QUESTION_ROOT_GROUP_NAME = "root";
  private static final String EXAM_ROOT_GROUP_NAME = "root group exam";

  private final GroupRepository groupRepository;
  private final MembershipRepository membershipRepository;
  private final LmsRepositoryRegistry lmsRepositoryRegistry;
  private final LmsServiceRegistry lmsServiceRegistry;

  public SetupLms(GroupRepository groupRepository, MembershipRepository membershipRepository,
      LmsRepositoryRegistry lmsRepositoryRegistry, LmsServiceRegistry lmsServiceRegistry)
  {
    this.groupRepository = groupRepository;
    this.membershipRepository = membershipRepository;
    this.lmsRepositoryRegistry = lmsRepositoryRegistry;
    this.lmsServiceRegistry = lmsServiceRegistry;
  }

  public void execute()
  {
    setLoggersToWarn();
    createRootGroup();
    createOnlineCourseCategories();
    createClassroomCourseCategories();
    setUseCaseDelegators();
    createRootExamGroup();
    createRootQuestionGroup();
    lmsServiceRegistry.getScheduledTaskInitializer().startPendingTasks();
  }

  private void createRootGroup()
  {
    try
    {
      if (groupRepository.doesGroupExistByName("LMS"))
      {
        return;
      }
      Group newRootGroup = groupRepository.createGroup("LMS", null, "Root group", TENANT_UNITEL);
      createInitMembership(newRootGroup.getId().getId());
    }
    catch (AimRepositoryException e)
    {
      LOGGER.error("Error setting up root group", e);
    }
  }

  private void createInitMembership(String rootGroupId)
  {
    try
    {
      membershipRepository.create(
          "admin10",
        GroupId.valueOf(rootGroupId),
        RoleId.valueOf(LmsRole.LMS_ADMIN.name()),
        TENANT_UNITEL
      );
    }
    catch (AimRepositoryException e)
    {
      LOGGER.error("Error creating initial membership", e);
    }
  }

  private void createOnlineCourseCategories()
  {
    OrganizationId organizationId = OrganizationId.valueOf(ORGANIZATION);
    CourseCategoryId parentCategoryId = CourseCategoryId.valueOf(CATEGORY_ONLINE_COURSE);

    CourseCategoryRepository courseCategoryRepository = lmsRepositoryRegistry.getCourseCategoryRepository();
    Collection<CourseCategory> onlineCourseCategories = courseCategoryRepository.listAll(organizationId, parentCategoryId);

    try
    {
      if (onlineCourseCategories.isEmpty())
      {
        for (String categoryName : COURSE_CATEGORIES)
        {
          courseCategoryRepository.create(organizationId, parentCategoryId, categoryName, "", true);
        }
      }
    }
    catch (LmsRepositoryException e)
    {
      LOGGER.error(e.getMessage(), e);
    }
  }

  private void createClassroomCourseCategories()
  {
    OrganizationId organizationId = OrganizationId.valueOf(ORGANIZATION);
    CourseCategoryId parentCategoryId = CourseCategoryId.valueOf(CATEGORY_CLASSROOM_COURSE);

    CourseCategoryRepository courseCategoryRepository = lmsRepositoryRegistry.getCourseCategoryRepository();
    Collection<CourseCategory> onlineCourseCategories = courseCategoryRepository.listAll(organizationId, parentCategoryId);

    try
    {
      if (onlineCourseCategories.isEmpty())
      {
        for (String categoryName : COURSE_CATEGORIES)
        {
          courseCategoryRepository.create(organizationId, parentCategoryId, categoryName, "", false);
        }
      }
    }
    catch (LmsRepositoryException e)
    {
      LOGGER.error(e.getMessage(), e);
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

  private void createRootQuestionGroup()
  {
    QuestionGroupRepository questionGroupRepository = lmsRepositoryRegistry.getQuestionGroupRepository();
    OrganizationId organizationId = OrganizationId.valueOf(lmsServiceRegistry.getOrganizationIdProvider().getOrganizationId());

    if (questionGroupRepository.getAllByOrganizationId(organizationId).isEmpty())
    {
      questionGroupRepository.create(null, QUESTION_ROOT_GROUP_NAME, organizationId, "question root group");
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

    getCoursesDelegator.register(UnitelAdmin.class, new GetAdminCourses(lmsRepositoryRegistry, lmsServiceRegistry));
    getCoursesDelegator.register(UnitelEmployee.class, getEnrolledCourses);
    getCoursesDelegator.register(UnitelSupervisor.class, getEnrolledCourses);
    getCoursesDelegator.register(UnitelManager.class, getEnrolledCourses);

    //Get suggested course
    UseCaseDelegator<GetCoursesInput, List<CourseDto>> getSuggestedCoursesDelegator =
        (UseCaseDelegator<GetCoursesInput, List<CourseDto>>) useCaseResolver.getUseCaseDelegator(GetSuggestedCourses.class.getName());
    GetEnrolledSuggestedCourses getEnrolledSuggestedCourses = new GetEnrolledSuggestedCourses(lmsRepositoryRegistry, lmsServiceRegistry);

    getSuggestedCoursesDelegator.register(UnitelAdmin.class, new GetDepartmentSuggestedCourses(lmsRepositoryRegistry, lmsServiceRegistry));
    getSuggestedCoursesDelegator.register(UnitelEmployee.class, getEnrolledSuggestedCourses);
    getSuggestedCoursesDelegator.register(UnitelSupervisor.class, getEnrolledSuggestedCourses);
    getSuggestedCoursesDelegator.register(UnitelManager.class, getEnrolledSuggestedCourses);

    //Search course
    UseCaseDelegator<Map<String, Object>, List<CourseDto>> searchCourseDelegator =
        (UseCaseDelegator<Map<String, Object>, List<CourseDto>>) useCaseResolver.getUseCaseDelegator(SearchCourse.class.getName());

    SearchEnrolledCourses searchEnrolledCourses = new SearchEnrolledCourses(lmsRepositoryRegistry, lmsServiceRegistry);

    searchCourseDelegator.register(UnitelAdmin.class, new SearchDepartmentCourses(lmsRepositoryRegistry, lmsServiceRegistry));
    searchCourseDelegator.register(UnitelEmployee.class, searchEnrolledCourses);
    searchCourseDelegator.register(UnitelSupervisor.class, searchEnrolledCourses);
    searchCourseDelegator.register(UnitelManager.class, searchEnrolledCourses);
  }

  // Sets every logger level to WARN
  private void setLoggersToWarn()
  {
    ILoggerFactory loggerContext = LoggerFactory.getILoggerFactory();

    if (loggerContext instanceof LoggerContext)
    {
      for (Logger logger : ((LoggerContext) loggerContext).getLoggerList())
      {
        logger.setLevel(Level.WARN);
      }
    }
  }
}

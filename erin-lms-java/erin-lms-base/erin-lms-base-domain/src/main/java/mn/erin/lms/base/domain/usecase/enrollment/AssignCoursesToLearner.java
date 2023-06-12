package mn.erin.lms.base.domain.usecase.enrollment;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import mn.erin.domain.aim.repository.GroupRepository;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.organization.OrganizationId;
import mn.erin.lms.base.domain.model.category.CourseCategory;
import mn.erin.lms.base.domain.model.category.CourseCategoryId;
import mn.erin.lms.base.domain.model.course.PublishStatus;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.UseCaseDelegator;
import mn.erin.lms.base.domain.usecase.course.CourseUseCase;
import mn.erin.lms.base.domain.usecase.course.GetCourses;
import mn.erin.lms.base.domain.usecase.course.dto.CourseDto;
import mn.erin.lms.base.domain.usecase.course.dto.GetCoursesInput;
import mn.erin.lms.base.domain.usecase.enrollment.dto.AssignCoursesInput;
import mn.erin.lms.base.domain.usecase.enrollment.dto.CreateLearnerEnrollmentsInput;

/**
 * @author Munkh
 */
public class AssignCoursesToLearner extends CourseUseCase<AssignCoursesInput, Void>
{
  private final GroupRepository groupRepository;
  public AssignCoursesToLearner(LmsServiceRegistry lmsServiceRegistry, LmsRepositoryRegistry lmsRepositoryRegistry,
      GroupRepository groupRepository)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
    this.groupRepository = groupRepository;
  }

  @Override
  public Void execute(AssignCoursesInput input) throws UseCaseException
  {
    GetCourses getCourses = new GetCourses(lmsRepositoryRegistry, lmsServiceRegistry,
        (UseCaseDelegator<GetCoursesInput, List<CourseDto>>) lmsServiceRegistry.getUseCaseResolver().getUseCaseDelegator(GetCourses.class.getName()));

    String organizationId = lmsServiceRegistry.getOrganizationIdProvider().getOrganizationId();
    List<CourseCategory> categories = lmsRepositoryRegistry.getCourseCategoryRepository()
        .listAllByAutoEnrollment(OrganizationId.valueOf(organizationId), CourseCategoryId.valueOf("online-course"));

    CreateLearnerEnrollments createLearnerEnrollments = new CreateLearnerEnrollments(lmsRepositoryRegistry, lmsServiceRegistry);


    List<CourseDto> courses = new ArrayList<>();

    for (CourseCategory category: categories)
    {
      GetCoursesInput getCoursesInput = new GetCoursesInput(category.getCourseCategoryId().getId());
      getCoursesInput.setPublishStatus(PublishStatus.PUBLISHED.name());
      courses.addAll(getCourses.execute(getCoursesInput));
    }

    Set<String> parentGroups = groupRepository.getParentGroupIds(input.getDepartmentId());
    // Filter out other department courses
    courses.removeIf(course -> !course.getAssignedDepartments().contains(input.getDepartmentId()) &&
        course.getAssignedDepartments().stream().noneMatch(parentGroups::contains));

    if(!"LMS_MANAGER".equals(input.getRoleId()))
    {
      courses.removeIf(course -> "MANAGER".equals(course.getType()));
    }

    if(!"LMS_SUPERVISOR".equals(input.getRoleId()) && !"LMS_MANAGER".equals(input.getRoleId()))
    {
      courses.removeIf(course -> "SUPERVISOR".equals(course.getType()));
    }

    for (String userId: input.getUserIds())
    {
      Set<String> courseIds = courses.stream().map(CourseDto::getId).collect(Collectors.toSet());

      CreateLearnerEnrollmentsInput createLearnerEnrollmentsInput = new CreateLearnerEnrollmentsInput(userId, courseIds);
      createLearnerEnrollments.execute(createLearnerEnrollmentsInput);
    }
    return null;
  }
}

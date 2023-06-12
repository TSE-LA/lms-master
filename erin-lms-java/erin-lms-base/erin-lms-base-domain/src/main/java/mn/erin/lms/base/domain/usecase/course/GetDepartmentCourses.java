package mn.erin.lms.base.domain.usecase.course;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.base.domain.model.category.CourseCategoryId;
import mn.erin.lms.base.domain.model.course.Course;
import mn.erin.lms.base.domain.model.course.PublishStatus;
import mn.erin.lms.base.aim.organization.DepartmentId;
import mn.erin.lms.base.aim.user.Instructor;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.aim.LmsDepartmentService;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.content.GetCoursePublishStatus;
import mn.erin.lms.base.domain.usecase.course.dto.CourseDto;
import mn.erin.lms.base.domain.usecase.course.dto.GetCoursesInput;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Authorized(users = { Instructor.class })
public class GetDepartmentCourses extends CourseUseCase<GetCoursesInput, List<CourseDto>>
{
  private final LmsDepartmentService departmentService;

  public GetDepartmentCourses(LmsRepositoryRegistry lmsRepositoryRegistry,
      LmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
    this.departmentService = lmsServiceRegistry.getDepartmentService();
  }

  @Override
  public List<CourseDto> execute(GetCoursesInput input) throws UseCaseException
  {
    Validate.notNull(input);

    CourseCategoryId courseCategoryId = CourseCategoryId.valueOf(input.getCategoryId());
    Optional<String> publishStatus = Optional.ofNullable(input.getPublishStatus());
    GetCoursePublishStatus getCoursePublishStatus = new GetCoursePublishStatus(lmsRepositoryRegistry);

    List<Course> courses;
    if (publishStatus.isPresent())
    {
      courses = getCourses(courseCategoryId, input.getCourseType(), PublishStatus.valueOf(publishStatus.get().toUpperCase()));
    }
    else
    {
      courses = getCourses(courseCategoryId, input.getCourseType());
    }
    for(Course course: courses)
    {
      course.getCourseDetail().addProperty("isReadyToPublish", String.valueOf(getCoursePublishStatus.execute(course.getCourseId().getId())));
    }

    return courses.stream().map(this::toCourseDto).collect(Collectors.toList());
  }

  private List<Course> getCourses(CourseCategoryId courseCategoryId, String courseType) throws UseCaseException
  {
    Set<String> departments = departmentService.getSubDepartments(departmentService.getCurrentDepartmentId());

    List<Course> courses = new ArrayList<>();

    Optional<String> optionalCourseType = Optional.ofNullable(courseType);

    for (String department : departments)
    {
      List<Course> departmentCourses = optionalCourseType.isPresent() ?
          courseRepository.listAll(courseCategoryId, getCourseType(courseType), DepartmentId.valueOf(department)) :
          courseRepository.listAll(courseCategoryId, DepartmentId.valueOf(department));
      courses.addAll(departmentCourses);
    }
    Set<String> duplicateSet = new HashSet<>();
    List<Course> removedDuplicates = courses.stream().filter(e -> duplicateSet.add(e.getCourseId().getId())).collect(Collectors.toList());
    removedDuplicates = new ArrayList<>(removedDuplicates);
    return removedDuplicates;
  }

  private List<Course> getCourses(CourseCategoryId courseCategoryId, String courseType, PublishStatus publishStatus)
      throws UseCaseException
  {
    Set<String> departments = departmentService.getSubDepartments(departmentService.getCurrentDepartmentId());

    List<Course> courses = new ArrayList<>();

    Optional<String> optionalCourseType = Optional.ofNullable(courseType);

    for (String department : departments)
    {
      List<Course> departmentCourses = optionalCourseType.isPresent() ?
          courseRepository.listAll(courseCategoryId, publishStatus, getCourseType(courseType), DepartmentId.valueOf(department)) :
          courseRepository.listAll(courseCategoryId, publishStatus, DepartmentId.valueOf(department));
      courses.addAll(departmentCourses);
    }
    Set<String> duplicateSet = new HashSet<>();
    List<Course> removedDuplicates = courses.stream().filter(e -> duplicateSet.add(e.getCourseId().getId())).collect(Collectors.toList());
    removedDuplicates = new ArrayList<>(removedDuplicates);
    return removedDuplicates;
  }
}

package mn.erin.lms.base.domain.usecase.course;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.LmsDepartmentService;
import mn.erin.lms.base.aim.organization.DepartmentId;
import mn.erin.lms.base.aim.user.Instructor;
import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.base.domain.model.category.CourseCategoryId;
import mn.erin.lms.base.domain.model.content.CourseContent;
import mn.erin.lms.base.domain.model.course.Course;
import mn.erin.lms.base.domain.repository.CourseContentRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.course.dto.CourseDto;
import mn.erin.lms.base.domain.usecase.course.dto.GetCoursesInput;

/**
 * @author Erdenetulga
 */
@Authorized(users = { Instructor.class })
public class GetDepartmentSuggestedCourses extends CourseUseCase<GetCoursesInput, List<CourseDto>>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(GetDepartmentSuggestedCourses.class);
  public static final String FAILED_TO_GET_COURSE_MODULES_COUNT = "Failed to get course modules count of course with id: [{}]";

  private final LmsDepartmentService departmentService;
  private final CourseContentRepository courseContentRepository;

  public GetDepartmentSuggestedCourses(LmsRepositoryRegistry lmsRepositoryRegistry,
      LmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
    this.departmentService = lmsServiceRegistry.getDepartmentService();
    this.courseContentRepository = lmsRepositoryRegistry.getCourseContentRepository();
  }

  @Override
  public List<CourseDto> execute(GetCoursesInput input) throws UseCaseException
  {
    Validate.notNull(input);

    CourseCategoryId courseCategoryId = CourseCategoryId.valueOf(input.getCategoryId());
    List<Course> courses;

    courses = getCourses(courseCategoryId, input.getCourseCount());

    return toOutput(courses);
  }

  private List<CourseDto> toOutput(List<Course> courses)
  {
    List<CourseDto> result = new ArrayList<>();
    int modulesCount = 0;
    for (Course course : courses)
    {
      try
      {
        CourseContent courseContent = courseContentRepository.fetchById(course.getCourseId());
        modulesCount = courseContent.getModules().size();
      }
      catch (LmsRepositoryException e)
      {
        LOGGER.error(FAILED_TO_GET_COURSE_MODULES_COUNT, course.getCourseId().getId());
      }
      CourseDto courseDto = toAlternateCourseDto(course, modulesCount, null);
      result.add(courseDto);
    }

    return result;
  }

  private List<Course> getCourses(CourseCategoryId courseCategoryId, int count)
  {
    Set<String> departments = departmentService.getSubDepartments(departmentService.getCurrentDepartmentId());

    List<Course> courses = new ArrayList<>();

    for (String department : departments)
    {
      List<Course> departmentCourses = courseRepository.listAll(courseCategoryId, DepartmentId.valueOf(department));
      courses.addAll(departmentCourses);
    }

    Set<String> duplicateSet = new HashSet<>();
    List<Course> removedDuplicates = courses.stream()
        .filter(e -> duplicateSet.add(e.getCourseId().getId()))
        .sorted(Collections.reverseOrder())
        .collect(Collectors.toList());

    return removedDuplicates.size() - 1 >= count ? removedDuplicates.subList(0, count) : removedDuplicates;
  }
}

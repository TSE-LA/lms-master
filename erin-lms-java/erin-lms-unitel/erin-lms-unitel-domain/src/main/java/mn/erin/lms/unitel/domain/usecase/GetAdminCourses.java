package mn.erin.lms.unitel.domain.usecase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.domain.model.category.CourseCategory;
import mn.erin.lms.base.domain.model.category.CourseCategoryId;
import mn.erin.lms.base.domain.model.course.Course;
import mn.erin.lms.base.aim.organization.OrganizationId;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.service.OrganizationIdProvider;
import mn.erin.lms.base.domain.usecase.course.GetDepartmentCourses;
import mn.erin.lms.base.domain.usecase.course.dto.CourseDto;
import mn.erin.lms.base.domain.usecase.course.dto.GetCoursesInput;

/**
 * @author Munkh
 */
public class GetAdminCourses extends GetDepartmentCourses
{
  private final OrganizationIdProvider organizationIdProvider;

  public GetAdminCourses(LmsRepositoryRegistry lmsRepositoryRegistry,
      LmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
    organizationIdProvider = lmsServiceRegistry.getOrganizationIdProvider();
  }

  @Override
  public List<CourseDto> execute(GetCoursesInput input) throws UseCaseException
  {
    Validate.notNull(input);
    if (isClassroomCourseCategory(input.getCategoryId()))
    {
      List<CourseDto> teachingCourses = getTeachingCourses(CourseCategoryId.valueOf(input.getCategoryId()), input.getCourseType(), input.getLearnerId())
          .stream().map(this::toCourseDto).collect(Collectors.toList());
      List<CourseDto> courses = super.execute(input);
      courses.addAll(teachingCourses);

      Set<String> duplicateSet = new HashSet<>();
      List<CourseDto> removedDuplicates = courses.stream().filter(e -> duplicateSet.add(e.getId())).collect(Collectors.toList());
      removedDuplicates = new ArrayList<>(removedDuplicates);
      return removedDuplicates;
    }
    else
    {
      return super.execute(input);
    }
  }

  private boolean isClassroomCourseCategory(String category) {
    Collection<CourseCategory> classroomCategories = courseCategoryRepository.listAll(
        OrganizationId.valueOf(organizationIdProvider.getOrganizationId()),
        CourseCategoryId.valueOf("classroom-course")
    );
    return classroomCategories.stream().map(classroomCategory -> classroomCategory.getCourseCategoryId().getId())
        .anyMatch(classroomCategory -> classroomCategory.equals(category));
  }

  private List<Course> getTeachingCourses(CourseCategoryId categoryId, String courseType, String adminId) throws UseCaseException
  {
    List<Course> courses;
    if (!StringUtils.isBlank(courseType))
    {
      courses = courseRepository.listAll(categoryId, getCourseType(courseType));
    }
    else
    {
      courses = courseRepository.listAll(categoryId);
    }

    courses.removeIf(course -> !course.getCourseDetail().getProperties().get("teacher").equals(adminId));

    return courses;
  }
}

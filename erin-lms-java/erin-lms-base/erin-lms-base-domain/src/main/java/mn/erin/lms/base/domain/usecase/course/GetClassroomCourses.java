package mn.erin.lms.base.domain.usecase.course;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import mn.erin.lms.base.aim.LmsDepartmentService;
import mn.erin.lms.base.aim.organization.OrganizationId;
import mn.erin.lms.base.domain.model.category.CourseCategory;
import mn.erin.lms.base.domain.model.category.CourseCategoryId;
import mn.erin.lms.base.domain.model.course.Course;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.service.OrganizationIdProvider;
import mn.erin.lms.base.domain.usecase.course.dto.CourseDto;

/**
 * @author Byambajav
 */
public class GetClassroomCourses extends GetDepartmentCourses
{
  private final LmsDepartmentService departmentService;
  private final OrganizationIdProvider organizationIdProvider;

  public GetClassroomCourses(LmsRepositoryRegistry lmsRepositoryRegistry, LmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
    this.departmentService = lmsServiceRegistry.getDepartmentService();
    this.organizationIdProvider = lmsServiceRegistry.getOrganizationIdProvider();
  }

  public List<CourseDto> execute()
  {
    List<Course> courses = new ArrayList<>();
    List<Course> result = new ArrayList<>();
    Collection<CourseCategory> classroomCategories = courseCategoryRepository.listAll(
        OrganizationId.valueOf(organizationIdProvider.getOrganizationId()),
        CourseCategoryId.valueOf("classroom-course"));
    String departmentId = departmentService.getCurrentDepartmentId();
    Set<String> allInstructors = new HashSet<>();
    for(String subDepartment : departmentService.getSubDepartments(departmentId)){
        allInstructors.addAll(departmentService.getInstructors(subDepartment));
    }
    for (CourseCategory category : classroomCategories)
    {
        List<Course> departmentCourses = courseRepository.listAll(category.getCourseCategoryId());
        courses.addAll(departmentCourses);
    }
    for(Course course: courses)
    {
      if(allInstructors.contains(course.getCourseDetail().getProperties().get("teacher"))){
        result.add(course);
      }
    }
    Set<String> duplicateSet = new HashSet<>();
    List<Course> removedDuplicates = result.stream().filter(e -> duplicateSet.add(e.getCourseId().getId())).collect(Collectors.toList());
    removedDuplicates = new ArrayList<>(removedDuplicates);
    return removedDuplicates.stream().map(this::toCourseDto).collect(Collectors.toList());
  }

}

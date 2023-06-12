package mn.erin.lms.base.domain.usecase.course;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.base.domain.model.course.Course;
import mn.erin.lms.base.aim.user.Instructor;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.aim.LmsDepartmentService;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.course.dto.CourseDto;

/**
 * @author Erdenetulga
 */
@Authorized(users = { Instructor.class })
public class SearchDepartmentCourses extends CourseUseCase<Map<String, Object>, List<CourseDto>>
{
  private final LmsDepartmentService departmentService;

  public SearchDepartmentCourses(LmsRepositoryRegistry lmsRepositoryRegistry,
      LmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
    this.departmentService = lmsServiceRegistry.getDepartmentService();
  }

  @Override
  public List<CourseDto> execute(Map<String, Object> queryParameters) throws UseCaseException
  {
    Validate.notNull(queryParameters);
    List<Course> searchedCourses;
    searchedCourses = getCourses(queryParameters);

    return searchedCourses.stream().map(this::toCourseDto)
        .collect(Collectors.toList());
  }

  private List<Course> getCourses(Map<String, Object> queryParameters)
  {

    boolean searchByDescription = false;
    boolean searchByName = false;
    String text = "";
    for (Map.Entry<String, Object> entry : queryParameters.entrySet())
    {
      if (entry.getKey().equals("description"))
      {
        searchByDescription = Boolean.parseBoolean(entry.getValue().toString());
      }
      if (entry.getKey().equals("name"))
      {
        searchByName = Boolean.parseBoolean(entry.getValue().toString());
      }
      if (entry.getKey().equals("query"))
      {
        text = entry.getValue().toString();
      }
    }
    List<Course> courses;

    Set<String> departments = departmentService.getSubDepartments(departmentService.getCurrentDepartmentId());
    departments.add(departmentService.getCurrentDepartmentId());
    List<Course> departmentCourses = new ArrayList<>();
    for (String department : departments)
    {
      List<Course> search = courseRepository.search(text, searchByName, searchByDescription, department);
      departmentCourses.addAll(search);
    }
    if (departmentCourses.isEmpty())
    {
      return Collections.emptyList();
    }
    courses = new ArrayList<>(departmentCourses);
    Set<String> duplicateSet = new HashSet<>();
    List<Course> removedDuplicates = courses.stream().filter(e -> duplicateSet.add(e.getCourseId().getId())).collect(Collectors.toList());
    removedDuplicates = new ArrayList<>(removedDuplicates);
    return removedDuplicates;
  }
}

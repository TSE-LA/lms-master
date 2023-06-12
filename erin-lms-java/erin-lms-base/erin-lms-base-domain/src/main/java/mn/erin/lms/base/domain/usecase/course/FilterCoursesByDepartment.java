package mn.erin.lms.base.domain.usecase.course;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.base.aim.user.Instructor;
import mn.erin.lms.base.aim.LmsDepartmentService;
import mn.erin.lms.base.domain.usecase.course.dto.CourseDto;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Authorized(users = { Instructor.class })
public class FilterCoursesByDepartment implements UseCase<List<CourseDto>, List<CourseDto>>
{
  private final LmsDepartmentService departmentService;

  public FilterCoursesByDepartment(LmsDepartmentService departmentService)
  {
    this.departmentService = Objects.requireNonNull(departmentService);
  }

  @Override
  public List<CourseDto> execute(List<CourseDto> courses)
  {
    Validate.notNull(courses);

    String departmentId = departmentService.getCurrentDepartmentId();
    Set<String> departments = departmentService.getSubDepartments(departmentId);

    List<CourseDto> filteredResult = new ArrayList<>();

    for (CourseDto course : courses)
    {
      if (departments.contains(course.getBelongingDepartmentId()))
      {
        filteredResult.add(course);
      }
    }

    return filteredResult;
  }
}

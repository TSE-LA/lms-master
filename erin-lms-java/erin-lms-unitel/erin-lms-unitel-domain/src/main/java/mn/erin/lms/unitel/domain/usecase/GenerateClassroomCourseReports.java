package mn.erin.lms.unitel.domain.usecase;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.organization.DepartmentId;
import mn.erin.lms.base.aim.user.Author;
import mn.erin.lms.base.aim.user.Instructor;
import mn.erin.lms.base.aim.user.Manager;
import mn.erin.lms.base.aim.user.Supervisor;
import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.base.domain.model.category.CourseCategoryId;
import mn.erin.lms.base.domain.model.course.Course;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.usecase.course.CourseUseCase;
import mn.erin.lms.base.domain.usecase.course.dto.CourseDto;
import mn.erin.lms.unitel.domain.service.CourseReportService;
import mn.erin.lms.unitel.domain.service.UnitelLmsServiceRegistry;
import mn.erin.lms.unitel.domain.usecase.dto.ReportFilter;

/**
 * @author Erdenetulga
 */
@Authorized(users = { Author.class, Manager.class, Instructor.class, Supervisor.class })
public class GenerateClassroomCourseReports extends CourseUseCase<ReportFilter, List<CourseDto>>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(GenerateCourseReports.class);

  private final CourseReportService courseReportService;

  public GenerateClassroomCourseReports(LmsRepositoryRegistry lmsRepositoryRegistry,
      UnitelLmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
    this.courseReportService = lmsServiceRegistry.getCourseReportService();
  }

  @Override
  public List<CourseDto> execute(ReportFilter input) throws UseCaseException
  {
    Validate.notNull(input);
    DepartmentId departmentId = DepartmentId.valueOf(input.getDepartmentId());
    List<Course> courseReports;

    if (StringUtils.isBlank(input.getCategoryId()))
    {
      courseReports = courseReportService.generateCourseReportWithoutRuntimeData(departmentId, input.getStartDate(), input.getEndDate());
    }
    else
    {
      CourseCategoryId courseCategoryId = CourseCategoryId.valueOf(input.getCategoryId());
      courseReports = courseReportService
          .generateCourseReportsWithoutRuntimeData(departmentId, courseCategoryId, input.getStartDate(), input.getEndDate());
    }
    return toOutput(courseReports);
  }

  private List<CourseDto> toOutput(List<Course> courses)
  {
    List<CourseDto> result = new ArrayList<>();

    for (Course course : courses)
    {
      result.add(toCourseDto(course));
    }

    return result;
  }
}

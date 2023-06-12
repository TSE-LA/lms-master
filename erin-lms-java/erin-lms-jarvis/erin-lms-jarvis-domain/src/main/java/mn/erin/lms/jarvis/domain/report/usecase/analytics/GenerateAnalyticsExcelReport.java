package mn.erin.lms.jarvis.domain.report.usecase.analytics;

import java.util.Objects;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.organization.DepartmentId;
import mn.erin.lms.base.aim.user.Author;
import mn.erin.lms.base.aim.user.Instructor;
import mn.erin.lms.base.aim.user.Manager;
import mn.erin.lms.base.aim.user.Supervisor;
import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.base.domain.model.course.CourseId;
import mn.erin.lms.jarvis.domain.report.service.CourseAnalyticsService;
import mn.erin.lms.jarvis.domain.report.usecase.analytics.dto.GetCourseAnalyticsInput;

/**
 * @author Erdenetulga
 */
@Authorized(users = { Author.class, Manager.class, Instructor.class, Supervisor.class })
public class GenerateAnalyticsExcelReport implements UseCase<GetCourseAnalyticsInput, byte[]>
{
  private final CourseAnalyticsService courseAnalyticsService;

  public GenerateAnalyticsExcelReport(CourseAnalyticsService courseAnalyticsService)
  {
    this.courseAnalyticsService = Objects.requireNonNull(courseAnalyticsService);
  }

  @Override
  public byte[] execute(GetCourseAnalyticsInput input) throws UseCaseException
  {
    Validate.notNull(input);

    CourseId courseId = CourseId.valueOf(input.getCourseId());
    DepartmentId departmentId = DepartmentId.valueOf(input.getDepartmentId());

    return courseAnalyticsService.generateAnalyticsData(courseId, departmentId, input.getStartDate(), input.getEndDate());
  }
}

package mn.erin.lms.unitel.domain.usecase;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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
import mn.erin.lms.unitel.domain.model.analytics.CourseAnalytics;
import mn.erin.lms.unitel.domain.service.CourseAnalyticsService;
import mn.erin.lms.unitel.domain.usecase.dto.CourseAnalyticsDto;
import mn.erin.lms.unitel.domain.usecase.dto.GetCourseAnalyticsInput;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Authorized(users = { Author.class, Manager.class, Instructor.class, Supervisor.class })
public class GetCourseAnalytics implements UseCase<GetCourseAnalyticsInput, CourseAnalyticsDto>
{
  private final CourseAnalyticsService courseAnalyticsService;

  public GetCourseAnalytics(CourseAnalyticsService courseAnalyticsService)
  {
    this.courseAnalyticsService = Objects.requireNonNull(courseAnalyticsService);
  }

  @Override
  public CourseAnalyticsDto execute(GetCourseAnalyticsInput input) throws UseCaseException
  {
    Validate.notNull(input);

    CourseId courseId = CourseId.valueOf(input.getCourseId());
    DepartmentId departmentId = DepartmentId.valueOf(input.getDepartmentId());

    CourseAnalytics courseAnalytics = courseAnalyticsService.getCourseAnalytics(courseId, departmentId, input.getStartDate(), input.getEndDate());
    Map<String, Object> data = courseAnalytics.getAnalyticData().entrySet().stream().collect(Collectors.toMap(entry -> entry.getKey().getId(),
        Map.Entry::getValue));
    return new CourseAnalyticsDto(courseAnalytics.getCourseId().getId(), data);
  }
}

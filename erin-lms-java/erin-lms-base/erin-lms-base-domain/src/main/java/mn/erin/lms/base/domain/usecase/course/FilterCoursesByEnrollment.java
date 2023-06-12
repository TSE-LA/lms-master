package mn.erin.lms.base.domain.usecase.course;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.base.aim.user.Learner;
import mn.erin.lms.base.aim.user.LearnerId;
import mn.erin.lms.base.aim.user.LmsUser;
import mn.erin.lms.base.aim.user.Supervisor;
import mn.erin.lms.base.domain.repository.CourseEnrollmentRepository;
import mn.erin.lms.base.aim.LmsUserService;
import mn.erin.lms.base.domain.usecase.course.dto.CourseDto;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Authorized(users = { Supervisor.class, Learner.class })
public class FilterCoursesByEnrollment implements UseCase<List<CourseDto>, List<CourseDto>>
{
  private final CourseEnrollmentRepository courseEnrollmentRepository;
  private final LmsUserService lmsUserService;

  public FilterCoursesByEnrollment(CourseEnrollmentRepository courseEnrollmentRepository, LmsUserService lmsUserService)
  {
    this.courseEnrollmentRepository = Objects.requireNonNull(courseEnrollmentRepository);
    this.lmsUserService = Objects.requireNonNull(lmsUserService);
  }

  @Override
  public List<CourseDto> execute(List<CourseDto> courses)
  {
    Validate.notNull(courses);

    LmsUser currentUser = lmsUserService.getCurrentUser();
    Set<String> enrolledCourses = courseEnrollmentRepository.listAll(LearnerId.valueOf(currentUser.getId().getId()))
        .stream().map(enrollment -> enrollment.getCourseId().getId()).collect(Collectors.toSet());

    List<CourseDto> filteredResult = new ArrayList<>();
    for (CourseDto course : courses)
    {
      if (enrolledCourses.contains(course.getId()))
      {
        filteredResult.add(course);
      }
    }

    return filteredResult;
  }
}

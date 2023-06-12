package mn.erin.lms.legacy.domain.lms.usecase.audit;

import mn.erin.domain.aim.repository.UserRepository;
import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.legacy.domain.lms.model.audit.CourseAudit;
import mn.erin.lms.legacy.domain.lms.model.course.Course;
import mn.erin.lms.legacy.domain.lms.model.course.CourseId;
import mn.erin.lms.legacy.domain.lms.model.enrollment.LearnerId;
import mn.erin.lms.legacy.domain.lms.repository.CourseAuditRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseRepository;
import mn.erin.lms.legacy.domain.lms.repository.LMSRepositoryException;
import mn.erin.lms.legacy.domain.lms.usecase.audit.dto.CreateCourseAuditInput;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class CreateCourseAudit implements UseCase<CreateCourseAuditInput, String>
{
  private final CourseAuditRepository courseAuditRepository;
  private final CourseRepository courseRepository;
  private final UserRepository userRepository;

  public CreateCourseAudit(CourseAuditRepository courseAuditRepository, CourseRepository courseRepository,
      UserRepository userRepository)
  {
    this.courseAuditRepository = courseAuditRepository;
    this.courseRepository = courseRepository;
    this.userRepository = userRepository;
  }

  @Override
  public String execute(CreateCourseAuditInput input) throws UseCaseException
  {
    Course course;
    try
    {
      course = courseRepository.getCourse(new CourseId(input.getCourseId()));
    }
    catch (LMSRepositoryException e)
    {
      throw new UseCaseException("The course with the ID:[ " + input.getCourseId() + "] does not exist!");
    }

    CourseAudit courseAudit = courseAuditRepository.create(course.getCourseId(), new LearnerId(input.getLearnerId()));
    return courseAudit.getId().getId();
  }
}

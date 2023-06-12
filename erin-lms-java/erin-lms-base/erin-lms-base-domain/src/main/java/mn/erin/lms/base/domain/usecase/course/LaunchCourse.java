package mn.erin.lms.base.domain.usecase.course;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.base.domain.model.course.Course;
import mn.erin.lms.base.domain.model.course.CourseId;
import mn.erin.lms.base.aim.user.Instructor;
import mn.erin.lms.base.aim.user.Learner;
import mn.erin.lms.base.aim.user.Supervisor;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.CourseLauncher;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;

/**
 * @author Bat-Erdene Tsogoo.
 */
@SuppressWarnings("unchecked")
@Authorized(users = { Instructor.class, Supervisor.class, Learner.class })
public class LaunchCourse<O> extends CourseUseCase<String, O>
{
  private final CourseLauncher<O> courseLauncher;

  public LaunchCourse(LmsRepositoryRegistry lmsRepositoryRegistry, LmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
    this.courseLauncher = (CourseLauncher<O>) lmsServiceRegistry.getCourseLauncher();
  }

  @Override
  public O execute(String input) throws UseCaseException
  {
    CourseId courseId = CourseId.valueOf(input);
    Course course = getCourse(courseId);
    return courseLauncher.launch(courseId.getId(), course.getCourseContentId().getId());
  }
}

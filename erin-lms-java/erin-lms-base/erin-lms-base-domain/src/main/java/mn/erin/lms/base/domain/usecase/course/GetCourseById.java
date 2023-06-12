package mn.erin.lms.base.domain.usecase.course;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.base.domain.model.course.Course;
import mn.erin.lms.base.domain.model.course.CourseId;
import mn.erin.lms.base.aim.user.Author;
import mn.erin.lms.base.aim.user.Instructor;
import mn.erin.lms.base.aim.user.Learner;
import mn.erin.lms.base.aim.user.Manager;
import mn.erin.lms.base.aim.user.Supervisor;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.course.dto.CourseDto;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Authorized(users = { Author.class, Manager.class, Instructor.class, Supervisor.class, Learner.class })
public class GetCourseById extends CourseUseCase<String, CourseDto>
{
  public GetCourseById(LmsRepositoryRegistry lmsRepositoryRegistry, LmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
  }

  @Override
  public CourseDto execute(String courseId) throws UseCaseException
  {
    Validate.notBlank(courseId);

    Course course = getCourse(CourseId.valueOf(courseId));
    return toCourseDto(course);
  }
}

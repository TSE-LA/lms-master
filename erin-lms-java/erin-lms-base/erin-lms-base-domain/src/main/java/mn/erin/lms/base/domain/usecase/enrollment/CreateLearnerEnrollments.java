package mn.erin.lms.base.domain.usecase.enrollment;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.base.domain.model.course.Course;
import mn.erin.lms.base.domain.model.course.CourseDepartmentRelation;
import mn.erin.lms.base.domain.model.course.CourseId;
import mn.erin.lms.base.domain.model.enrollment.CourseEnrollment;
import mn.erin.lms.base.domain.model.enrollment.EnrollmentState;
import mn.erin.lms.base.aim.user.Instructor;
import mn.erin.lms.base.aim.user.LearnerId;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.course.CourseUseCase;
import mn.erin.lms.base.domain.usecase.enrollment.dto.CreateLearnerEnrollmentsInput;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Authorized(users = { Instructor.class })
public class CreateLearnerEnrollments extends CourseUseCase<CreateLearnerEnrollmentsInput, Void>
{

  public CreateLearnerEnrollments(LmsRepositoryRegistry lmsRepositoryRegistry,
      LmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
  }

  @Override
  public Void execute(CreateLearnerEnrollmentsInput input) throws UseCaseException
  {
    Validate.notNull(input);

    Optional<String> enrollmentState = Optional.ofNullable(input.getEnrollmentState());

    if (enrollmentState.isPresent())
    {
      createEnrollments(input.getCourses(), input.getLearnerId(), enrollmentState.get());
    }
    else
    {
      createEnrollments(input.getCourses(), input.getLearnerId());
    }

    return null;
  }

  private void createEnrollments(Set<String> courses, String learnerId) throws UseCaseException
  {
    LearnerId learner = LearnerId.valueOf(learnerId);
    for (String course : courses)
    {
      CourseId courseId = CourseId.valueOf(course);
      saveEnrollment(learner, courseId);
    }
  }

  private void createEnrollments(Set<String> courses, String learnerId, String enrollmentState) throws UseCaseException
  {
    LearnerId learner = LearnerId.valueOf(learnerId);
    EnrollmentState state = EnrollmentState.valueOf(enrollmentState);
    for (String course : courses)
    {
      CourseId courseId = CourseId.valueOf(course);
      saveEnrollment(learner, courseId, state);
    }
  }

  private void saveEnrollment(LearnerId learnerId, CourseId courseId, EnrollmentState enrollmentState)
      throws UseCaseException
  {
    validateCourse(courseId);
    CourseEnrollment courseEnrollment = new CourseEnrollment(courseId, learnerId);
    courseEnrollment.changeEnrollmentState(enrollmentState);
    courseEnrollmentRepository.save(courseEnrollment);
    updateCourseEnrollments(learnerId, courseId);
  }

  private void saveEnrollment(LearnerId learnerId, CourseId courseId) throws UseCaseException
  {
    validateCourse(courseId);
    CourseEnrollment courseEnrollment = new CourseEnrollment(courseId, learnerId);
    courseEnrollmentRepository.save(courseEnrollment);
    updateCourseEnrollments(learnerId, courseId);
  }

  private void updateCourseEnrollments(LearnerId learnerId, CourseId courseId) throws UseCaseException
  {
    Course course;
    try
    {
      course = courseRepository.fetchById(courseId);
    }
    catch (LmsRepositoryException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }

    CourseDepartmentRelation courseDepartmentRelation = course.getCourseDepartmentRelation();
    Set<LearnerId> assignedLearners = courseDepartmentRelation.getAssignedLearners();
    Set<LearnerId> addedAssignedLearners = new HashSet<>(assignedLearners);
    addedAssignedLearners.add(learnerId);
    courseDepartmentRelation.setAssignedLearners(addedAssignedLearners);

    try
    {
      courseRepository.update(course.getCourseId(), courseDepartmentRelation);
    }
    catch (LmsRepositoryException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }
  }
}

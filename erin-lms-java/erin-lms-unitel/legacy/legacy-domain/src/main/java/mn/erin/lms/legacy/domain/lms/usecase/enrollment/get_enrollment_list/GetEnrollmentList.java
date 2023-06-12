package mn.erin.lms.legacy.domain.lms.usecase.enrollment.get_enrollment_list;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.lms.legacy.domain.lms.model.course.CourseId;
import mn.erin.lms.legacy.domain.lms.model.enrollment.CourseEnrollment;
import mn.erin.lms.legacy.domain.lms.repository.CourseEnrollmentRepository;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class GetEnrollmentList implements UseCase<GetEnrollmentListInput, List<GetEnrollmentOutput>>
{
  private final CourseEnrollmentRepository courseEnrollmentRepository;

  public GetEnrollmentList(CourseEnrollmentRepository courseEnrollmentRepository)
  {
    this.courseEnrollmentRepository = Objects.requireNonNull(courseEnrollmentRepository, "CourseEnrollmentRepository cannot be null!");
  }

  @Override
  public List<GetEnrollmentOutput> execute(GetEnrollmentListInput input)
  {
    Validate.notNull(input, "Input cannot be null!");

    List<CourseEnrollment> enrollments = courseEnrollmentRepository.getEnrollmentList(new CourseId(input.getCourseId()));

    List<GetEnrollmentOutput> output = new ArrayList<>();

    for (CourseEnrollment enrollment : enrollments)
    {
      output.add(toOutput(enrollment));
    }

    return output;
  }

  private GetEnrollmentOutput toOutput(CourseEnrollment courseEnrollment)
  {
    String enrollmentId = courseEnrollment.getId().getId();
    String learnerId = courseEnrollment.getLearnerId().getId();
    String enrollmentState = courseEnrollment.getState().name();

    return new GetEnrollmentOutput(enrollmentId, learnerId, enrollmentState, courseEnrollment.getEnrolledDate());
  }
}

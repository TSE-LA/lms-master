package mn.erin.lms.base.domain.usecase.enrollment;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.base.domain.model.enrollment.EnrollmentState;
import mn.erin.lms.base.aim.user.Instructor;
import mn.erin.lms.base.aim.user.LearnerId;
import mn.erin.lms.base.domain.repository.CourseEnrollmentRepository;
import mn.erin.lms.base.domain.usecase.enrollment.dto.DeleteLearnerEnrollmentsInput;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Authorized(users = { Instructor.class })
public class DeleteLearnerEnrollments implements UseCase<DeleteLearnerEnrollmentsInput, Boolean>
{
  private final CourseEnrollmentRepository courseEnrollmentRepository;

  public DeleteLearnerEnrollments(CourseEnrollmentRepository courseEnrollmentRepository)
  {
    this.courseEnrollmentRepository = Objects.requireNonNull(courseEnrollmentRepository);
  }

  @Override
  public Boolean execute(DeleteLearnerEnrollmentsInput input) throws UseCaseException
  {
    Validate.notNull(input);

    if (StringUtils.isBlank(input.getEnrollmentState()))
    {
      return courseEnrollmentRepository.deleteAll(LearnerId.valueOf(input.getLearnerId()));
    }
    else
    {
      return courseEnrollmentRepository.deleteAll(LearnerId.valueOf(input.getLearnerId()),
          EnrollmentState.valueOf(input.getEnrollmentState().toUpperCase()));
    }
  }
}

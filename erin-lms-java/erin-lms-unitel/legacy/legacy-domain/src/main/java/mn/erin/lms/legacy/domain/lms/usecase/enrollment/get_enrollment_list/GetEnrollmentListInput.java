package mn.erin.lms.legacy.domain.lms.usecase.enrollment.get_enrollment_list;

import java.util.Objects;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class GetEnrollmentListInput
{
  private final String courseId;

  public GetEnrollmentListInput(String courseId)
  {
    this.courseId = Objects.requireNonNull(courseId, "CourseId cannot be null!");
  }

  public String getCourseId()
  {
    return courseId;
  }
}

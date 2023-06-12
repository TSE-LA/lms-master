package mn.erin.lms.base.domain.usecase.course.classroom.dto;

/**
 * @author Munkh
 */
public class UpdateEnrollmentCountInput
{
  private final String courseId;
  private final String enrollmentCount;

  public UpdateEnrollmentCountInput(String courseId, String enrollmentCount)
  {
    this.courseId = courseId;
    this.enrollmentCount = enrollmentCount;
  }

  public String getCourseId()
  {
    return courseId;
  }

  public String getEnrollmentCount()
  {
    return enrollmentCount;
  }
}

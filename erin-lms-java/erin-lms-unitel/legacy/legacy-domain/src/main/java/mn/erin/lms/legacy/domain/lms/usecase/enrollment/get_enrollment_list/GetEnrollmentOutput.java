package mn.erin.lms.legacy.domain.lms.usecase.enrollment.get_enrollment_list;

import java.util.Date;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class GetEnrollmentOutput
{
  private final String courseEnrollmentId;
  private final String learnerId;
  private final String enrollmentState;
  private Date enrolledDate;

  public GetEnrollmentOutput(String courseEnrollmentId, String learnerId, String enrollmentState, Date enrolledDate)
  {
    this.courseEnrollmentId = courseEnrollmentId;
    this.learnerId = learnerId;
    this.enrollmentState = enrollmentState;
    this.enrolledDate = enrolledDate;
  }

  public String getCourseEnrollmentId()
  {
    return courseEnrollmentId;
  }

  public String getLearnerId()
  {
    return learnerId;
  }

  public String getEnrollmentState()
  {
    return enrollmentState;
  }

  public Date getEnrolledDate()
  {
    return enrolledDate;
  }
}

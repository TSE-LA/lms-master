package mn.erin.lms.base.domain.usecase.course.classroom.dto;

/**
 * @author Munkh
 */
public class GetAttendanceInput
{
  private String courseId;
  private String learnerId;

  public GetAttendanceInput(String courseId, String learnerId)
  {
    this.courseId = courseId;
    this.learnerId = learnerId;
  }

  public String getCourseId()
  {
    return courseId;
  }

  public void setCourseId(String courseId)
  {
    this.courseId = courseId;
  }

  public String getLearnerId()
  {
    return learnerId;
  }

  public void setLearnerId(String learnerId)
  {
    this.learnerId = learnerId;
  }
}

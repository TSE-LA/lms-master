package mn.erin.lms.base.domain.usecase.course.dto;

/**
 * @author Oyungerel Chuluunsukh.
 */
public class DeleteClassroomAttendanceInput
{
  private final String courseId;
  private final String learnerId;
  public DeleteClassroomAttendanceInput(String courseId, String learnerId)
  {
    this.courseId = courseId;
    this.learnerId = learnerId;
  }

  public String getCourseId()
  {
    return courseId;
  }

  public String getLearnerId()
  {
    return learnerId;
  }
}

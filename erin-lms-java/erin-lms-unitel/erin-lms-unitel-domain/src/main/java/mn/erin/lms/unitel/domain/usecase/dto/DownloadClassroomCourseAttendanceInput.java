package mn.erin.lms.unitel.domain.usecase.dto;

import java.util.List;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class DownloadClassroomCourseAttendanceInput
{
  private String courseId;
  private List<AttendanceDto> attendances;

  public DownloadClassroomCourseAttendanceInput(String courseId, List<AttendanceDto> attendances)
  {
    this.courseId = courseId;
    this.attendances = attendances;
  }

  public String getCourseId()
  {
    return courseId;
  }

  public List<AttendanceDto> getAttendances()
  {
    return attendances;
  }
}

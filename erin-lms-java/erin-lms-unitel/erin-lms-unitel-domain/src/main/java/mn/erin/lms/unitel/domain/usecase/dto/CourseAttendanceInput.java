package mn.erin.lms.unitel.domain.usecase.dto;

import java.util.List;

/**
 * @author Munkh
 */
public class CourseAttendanceInput
{
  private final String courseId;
  private final List<LearnerAttendanceInput> attendances;

  public CourseAttendanceInput(String courseId, List<LearnerAttendanceInput> attendances)
  {
    this.courseId = courseId;
    this.attendances = attendances;
  }

  public String getCourseId()
  {
    return courseId;
  }

  public List<LearnerAttendanceInput> getAttendances()
  {
    return attendances;
  }
}

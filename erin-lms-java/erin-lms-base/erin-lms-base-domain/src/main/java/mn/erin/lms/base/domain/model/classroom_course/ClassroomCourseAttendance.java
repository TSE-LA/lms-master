package mn.erin.lms.base.domain.model.classroom_course;

import java.util.List;

import mn.erin.lms.base.domain.model.course.CourseId;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class ClassroomCourseAttendance
{
  private final CourseId courseId;
  private final List<Attendance> attendances;

  public ClassroomCourseAttendance(CourseId courseId, List<Attendance> attendances)
  {
    this.courseId = courseId;
    this.attendances = attendances;
  }

  public CourseId getCourseId()
  {
    return courseId;
  }

  public List<Attendance> getAttendances()
  {
    return attendances;
  }
}

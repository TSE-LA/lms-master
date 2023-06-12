package mn.erin.lms.unitel.rest.api.model;

import java.util.List;

/**
 * @author Munkh
 */
public class RestCourseAttendance
{
  private String courseId;
  private List<RestAttendance> attendances;

  public RestCourseAttendance()
  {
  }

  public RestCourseAttendance(List<RestAttendance> attendances)
  {
    this.attendances = attendances;
  }

  public RestCourseAttendance(String courseId, List<RestAttendance> attendances)
  {
    this.courseId = courseId;
    this.attendances = attendances;
  }

  public List<RestAttendance> getAttendances()
  {
    return attendances;
  }

  public void setAttendances(List<RestAttendance> attendances)
  {
    this.attendances = attendances;
  }

  public String getCourseId()
  {
    return courseId;
  }

  public void setCourseId(String courseId)
  {
    this.courseId = courseId;
  }
}

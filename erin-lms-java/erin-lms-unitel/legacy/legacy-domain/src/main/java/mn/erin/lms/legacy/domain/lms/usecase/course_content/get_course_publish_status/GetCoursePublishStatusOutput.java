package mn.erin.lms.legacy.domain.lms.usecase.course_content.get_course_publish_status;

/**
 * @author Erdenetulga
 */
public class GetCoursePublishStatusOutput
{
  private String courseId;
  private String status;

  GetCoursePublishStatusOutput(String courseId, String status)
  {
    this.courseId = courseId;
    this.status = status;
  }

  public String getCourseId()
  {
    return courseId;
  }

  public void setCourseId(String courseId)
  {
    this.courseId = courseId;
  }

  public String getStatus()
  {
    return status;
  }

  public void setStatus(String status)
  {
    this.status = status;
  }
}

package mn.erin.lms.legacy.domain.lms.model.content;

import mn.erin.lms.legacy.domain.lms.model.course.CourseId;

/**
 * @author Erdenetulga
 */
public class CourseContentPublishStatus
{
  private final CourseId courseId;
  private String status;

  public CourseContentPublishStatus(CourseId courseId, String status)
  {
    this.courseId = courseId;
    this.status = status;
  }

  public CourseId getCourseId()
  {
    return courseId;
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

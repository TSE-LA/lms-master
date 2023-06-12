package mn.erin.lms.legacy.domain.lms.usecase.audit.dto;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class GetCourseAuditOutput
{
  private final String id;
  private final String courseId;

  public GetCourseAuditOutput(String id, String courseId)
  {
    this.id = id;
    this.courseId = courseId;
  }

  public String getId()
  {
    return id;
  }

  public String getCourseId()
  {
    return courseId;
  }
}

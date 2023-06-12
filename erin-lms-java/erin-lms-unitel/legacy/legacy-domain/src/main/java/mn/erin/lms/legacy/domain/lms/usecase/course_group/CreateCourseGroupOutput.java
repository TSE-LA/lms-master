package mn.erin.lms.legacy.domain.lms.usecase.course_group;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class CreateCourseGroupOutput
{
  private final String courseGroupId;

  public CreateCourseGroupOutput(String courseGroupId)
  {
    this.courseGroupId = courseGroupId;
  }

  public String getCourseGroupId()
  {
    return courseGroupId;
  }
}

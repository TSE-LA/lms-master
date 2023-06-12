package mn.erin.lms.legacy.domain.lms.usecase.course_group;

import org.apache.commons.lang3.Validate;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class CreateCourseGroupInput
{
  private final String courseId;

  public CreateCourseGroupInput(String courseId)
  {
    this.courseId = Validate.notBlank(courseId, "Course ID cannot be null or blank!");
  }

  public String getCourseId()
  {
    return courseId;
  }
}

package mn.erin.lms.base.domain.usecase.course.dto;

import org.apache.commons.lang3.Validate;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class CreateCourseOutput
{
  private final String courseId;

  public CreateCourseOutput(String courseId)
  {
    this.courseId = Validate.notBlank(courseId);
  }

  public String getCourseId()
  {
    return courseId;
  }
}

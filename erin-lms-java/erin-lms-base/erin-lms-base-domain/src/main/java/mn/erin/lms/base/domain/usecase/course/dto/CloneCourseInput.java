package mn.erin.lms.base.domain.usecase.course.dto;

import org.apache.commons.lang3.Validate;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class CloneCourseInput
{
  private final String courseId;
  private final CreateCourseInput createCourseInput;

  public CloneCourseInput(String courseId, CreateCourseInput createCourseInput)
  {
    this.courseId = Validate.notBlank(courseId);
    this.createCourseInput = Validate.notNull(createCourseInput);
  }

  public String getCourseId()
  {
    return courseId;
  }

  public CreateCourseInput getCreateCourseInput()
  {
    return createCourseInput;
  }
}

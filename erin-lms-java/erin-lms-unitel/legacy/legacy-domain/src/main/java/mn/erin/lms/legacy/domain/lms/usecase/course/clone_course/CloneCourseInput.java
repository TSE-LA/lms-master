package mn.erin.lms.legacy.domain.lms.usecase.course.clone_course;

import org.apache.commons.lang3.Validate;

import mn.erin.lms.legacy.domain.lms.usecase.course.create_course.CreateCourseInput;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class CloneCourseInput
{
  private final String courseId;
  private final CreateCourseInput newCourseDetail;

  public CloneCourseInput(String courseId, CreateCourseInput newCourseDetail)
  {
    this.courseId = Validate.notBlank(courseId, "Course ID cannot be null or blank!");
    this.newCourseDetail = Validate.notNull(newCourseDetail, "New CourseDetail cannot be null!");
  }

  public String getCourseId()
  {
    return courseId;
  }

  public CreateCourseInput getNewCourseDetail()
  {
    return newCourseDetail;
  }
}

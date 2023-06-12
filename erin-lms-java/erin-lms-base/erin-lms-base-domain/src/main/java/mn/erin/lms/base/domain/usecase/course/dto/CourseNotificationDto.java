package mn.erin.lms.base.domain.usecase.course.dto;

import org.apache.commons.lang3.Validate;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class CourseNotificationDto
{
  private final String categoryId;
  private int newCourses = 0;

  public CourseNotificationDto(String categoryId)
  {
    this.categoryId = Validate.notBlank(categoryId);
  }

  public CourseNotificationDto(String categoryId, int newCourses)
  {
    this.categoryId = categoryId;
    this.newCourses = newCourses;
  }

  public void setNewCourses(int newCourses)
  {
    this.newCourses = newCourses;
  }

  public String getCategoryId()
  {
    return categoryId;
  }

  public int getNewCourses()
  {
    return newCourses;
  }
}

package mn.erin.lms.base.domain.model.classroom_course;

import java.util.List;

import mn.erin.lms.base.domain.model.course.CourseId;

/**
 * @author Erdenetulga
 */
public class CourseSuggestedUsers
{
  private CourseId courseId;
  private List<String> savedUsers;

  public CourseSuggestedUsers(CourseId courseId, List<String> savedUsers)
  {
    this.courseId = courseId;
    this.savedUsers = savedUsers;
  }

  public CourseId getCourseId()
  {
    return courseId;
  }

  public void setCourseId(CourseId courseId)
  {
    this.courseId = courseId;
  }

  public List<String> getSavedUsers()
  {
    return savedUsers;
  }

  public void setSavedUsers(List<String> savedUsers)
  {
    this.savedUsers = savedUsers;
  }
}

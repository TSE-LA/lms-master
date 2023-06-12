package mn.erin.lms.base.domain.usecase.course.classroom.dto;

import java.util.Set;

/**
 * @author Erdenetulga
 */
public class CourseSuggestedUsersDto
{
  private String courseId;
  private Set<String> users;

  public CourseSuggestedUsersDto(String courseId, Set<String> users)
  {
    this.courseId = courseId;
    this.users = users;
  }

  public CourseSuggestedUsersDto(String courseId)
  {
    this.courseId = courseId;
  }

  public String getCourseId()
  {
    return courseId;
  }

  public Set<String> getUsers()
  {
    return users;
  }

  public void setCourseId(String courseId)
  {
    this.courseId = courseId;
  }

  public void setUsers(Set<String> users)
  {
    this.users = users;
  }
}

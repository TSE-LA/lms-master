package mn.erin.lms.legacy.domain.lms.usecase.update_course_enrollment.dto;

import java.util.Objects;

import org.apache.commons.lang3.Validate;

import mn.erin.lms.legacy.domain.lms.usecase.course.update_course.UserGroupsInput;

/**
 * @author Oyungerel Chuluunsukh.
 */
public class UpdateCourseEnrollmentInput
{
  private final String courseId;
  private UserGroupsInput userGroups;

  public UpdateCourseEnrollmentInput(String courseId, UserGroupsInput userGroups)
  {
    this.courseId =  Validate.notBlank(courseId, "Course ID cannot be null or blank!");
    this.userGroups =  Objects.requireNonNull(userGroups, "User Groups cannot be null!");
  }

  public String getCourseId()
  {
    return courseId;
  }

  public UserGroupsInput getUserGroups()
  {
    return userGroups;
  }

  public void setUserGroups(UserGroupsInput userGroups)
  {
    this.userGroups = userGroups;
  }
}

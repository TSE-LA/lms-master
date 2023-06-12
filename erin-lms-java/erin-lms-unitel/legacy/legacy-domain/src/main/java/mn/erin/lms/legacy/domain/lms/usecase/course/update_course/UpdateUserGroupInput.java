/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.lms.usecase.course.update_course;

import java.util.Objects;

import org.apache.commons.lang3.Validate;

/**
 * @author Oyungerel Chuluunsukh.
 */
public class UpdateUserGroupInput
{
  private final String courseId;
  private UserGroupsInput userGroups = new UserGroupsInput();

  public UpdateUserGroupInput(String courseId)
  {
    this.courseId =  Validate.notBlank(courseId, "Course ID cannot be null or blank!");
  }

  public UpdateUserGroupInput(String courseId, UserGroupsInput userGroups)
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

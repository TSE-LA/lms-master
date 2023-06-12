/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.lms.usecase.course.publish_course;

import org.apache.commons.lang3.Validate;

import mn.erin.lms.legacy.domain.lms.usecase.course.update_course.UserGroupsInput;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class PublishCourseInput
{
  private final String courseId;
  private final String courseContentId;
  private UserGroupsInput userGroups = new UserGroupsInput();

  public PublishCourseInput(String courseId, String courseContentId)
  {
    this.courseId = Validate.notBlank(courseId, "Course ID cannot be null or blank!");
    this.courseContentId = Validate.notBlank(courseContentId, "Course content ID cannot be null or blank!");
  }

  public String getCourseId()
  {
    return courseId;
  }

  public String getCourseContentId()
  {
    return courseContentId;
  }

  public UserGroupsInput getUserGroups()
  {
    return userGroups;
  }

  public void setUserGroups(UserGroupsInput userGroups)
  {
    this.userGroups = Validate.notNull(userGroups, "UserGroupsInput cannot be null or empty!");
  }
}

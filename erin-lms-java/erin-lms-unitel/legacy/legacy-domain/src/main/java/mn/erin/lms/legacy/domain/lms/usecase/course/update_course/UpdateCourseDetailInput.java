/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.lms.usecase.course.update_course;

import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.Validate;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class UpdateCourseDetailInput
{
  private final String courseId;
  private final String title;
  private final Map<String, Object> properties;
  private String description;
  private String note;
  private UserGroupsInput userGroupsInput;

  public UpdateCourseDetailInput(String courseId, String title, Map<String, Object> properties)
  {
    this.courseId = Validate.notBlank(courseId, "Course ID cannot be null or blank!");
    this.title = Validate.notBlank(title, "Title cannot be null or blank!");
    this.properties = Objects.requireNonNull(properties, "Properties cannot be null!");
  }

  public String getCourseId()
  {
    return courseId;
  }

  public String getTitle()
  {
    return title;
  }

  public Map<String, Object> getProperties()
  {
    return properties;
  }

  public String getDescription()
  {
    return description;
  }

  public String getNote()
  {
    return note;
  }

  public void setDescription(String description)
  {
    this.description = description;
  }

  public void setNote(String note)
  {
    this.note = note;
  }

  public UserGroupsInput getUserGroupsInput()
  {
    return userGroupsInput;
  }

  public void setUserGroupsInput(UserGroupsInput userGroupsInput)
  {
    this.userGroupsInput = userGroupsInput;
  }
}

/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.lms.usecase.course.update_course;

import java.util.Collections;
import java.util.Map;

import org.apache.commons.lang3.Validate;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class UpdateCoursePropertiesInput
{
  private final String courseId;
  private final Map<String, Object> courseProperties;

  public UpdateCoursePropertiesInput(String courseId, Map<String, Object> courseProperties)
  {
    this.courseId = Validate.notBlank(courseId, "Course ID cannot be null or blank!");
    this.courseProperties = Validate.notEmpty(courseProperties, "Course properties cannot be null or empty!");
  }

  public String getCourseId()
  {
    return courseId;
  }

  public Map<String, Object> getCourseProperties()
  {
    return Collections.unmodifiableMap(courseProperties);
  }
}

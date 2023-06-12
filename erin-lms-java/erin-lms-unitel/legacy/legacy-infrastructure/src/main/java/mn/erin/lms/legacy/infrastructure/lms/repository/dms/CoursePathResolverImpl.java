/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.infrastructure.lms.repository.dms;

import org.apache.commons.lang3.Validate;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class CoursePathResolverImpl implements CoursePathResolver
{
  private String courseFolderPath;

  @Override
  public void setCourseFolderPath(String courseId)
  {
    this.courseFolderPath = "Courses/" + Validate.notBlank(courseId, "Course ID cannot be null or blank");
  }

  @Override
  public String getCourseFolderPath()
  {
    return this.courseFolderPath;
  }
}

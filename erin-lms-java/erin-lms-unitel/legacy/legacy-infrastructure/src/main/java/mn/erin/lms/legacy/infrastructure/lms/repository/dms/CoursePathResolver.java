/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.infrastructure.lms.repository.dms;

/**
 * @author Bat-Erdene Tsogoo.
 */
public interface CoursePathResolver
{
  void setCourseFolderPath(String courseId);

  String getCourseFolderPath();
}

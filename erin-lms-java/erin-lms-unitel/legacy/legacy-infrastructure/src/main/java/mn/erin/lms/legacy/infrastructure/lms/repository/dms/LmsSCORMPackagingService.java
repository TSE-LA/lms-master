/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.infrastructure.lms.repository.dms;

import mn.erin.lms.legacy.infrastructure.scorm.base.service.SCORMPackagingServiceImpl;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class LmsSCORMPackagingService extends SCORMPackagingServiceImpl
{
  private final CoursePathResolver coursePathResolver;

  public LmsSCORMPackagingService(CoursePathResolver coursePathResolver)
  {
    this.coursePathResolver = coursePathResolver;
  }

  @Override
  public String resolvePackagePath(String scoPathComponent)
  {
    return coursePathResolver.getCourseFolderPath() + "/SCORM/" + scoPathComponent;
  }
}

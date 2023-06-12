/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.infrastructure.scorm.base.service;

/**
 * @author Bat-Erdene Tsogoo.
 */
public interface SCORMPackagingService
{
  void setPackageId(String packageId);

  String getPackageId();

  String resolvePackagePath(String scoPathComponent);
}

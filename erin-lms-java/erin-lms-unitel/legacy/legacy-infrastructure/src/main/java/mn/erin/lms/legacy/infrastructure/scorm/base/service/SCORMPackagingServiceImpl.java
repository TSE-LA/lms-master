/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.infrastructure.scorm.base.service;

import org.apache.commons.lang3.Validate;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class SCORMPackagingServiceImpl implements SCORMPackagingService
{
  private String packageId;

  @Override
  public void setPackageId(String packageId)
  {
    this.packageId = Validate.notBlank(packageId, "Package Id cannot be null!");
  }

  @Override
  public String getPackageId()
  {
    return packageId;
  }

  @Override
  public String resolvePackagePath(String scoPathComponent)
  {
    throw new UnsupportedOperationException();
  }
}

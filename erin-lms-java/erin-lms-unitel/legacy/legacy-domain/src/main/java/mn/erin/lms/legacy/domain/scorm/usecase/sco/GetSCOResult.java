/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.scorm.usecase.sco;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class GetSCOResult
{
  private String path;
  private String name;

  GetSCOResult(String path, String name)
  {
    this.path = path;
    this.name = name;
  }

  public String getPath()
  {
    return path;
  }

  public String getName()
  {
    return name;
  }
}

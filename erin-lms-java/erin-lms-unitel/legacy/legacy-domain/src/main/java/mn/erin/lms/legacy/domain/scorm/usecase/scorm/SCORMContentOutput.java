/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.scorm.usecase.scorm;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class SCORMContentOutput
{
  private final String scormContentId;
  private final String scormContentName;

  SCORMContentOutput(String scormContentId, String name)
  {
    this.scormContentId = scormContentId;
    this.scormContentName = name;
  }

  public String getScormContentId()
  {
    return scormContentId;
  }

  public String getName()
  {
    return scormContentName;
  }
}

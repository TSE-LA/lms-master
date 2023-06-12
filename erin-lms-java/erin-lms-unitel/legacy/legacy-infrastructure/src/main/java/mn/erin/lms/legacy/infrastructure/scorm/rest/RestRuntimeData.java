/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.infrastructure.scorm.rest;

import java.util.Map;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class RestRuntimeData
{
  private String scoName;
  private Map<String, Object> data;

  public String getScoName()
  {
    return scoName;
  }

  public Map<String, Object> getData()
  {
    return data;
  }
}

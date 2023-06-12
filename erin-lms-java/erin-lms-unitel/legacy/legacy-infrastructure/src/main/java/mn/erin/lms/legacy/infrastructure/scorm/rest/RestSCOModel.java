/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.infrastructure.scorm.rest;

import java.util.Map;

import mn.erin.lms.legacy.domain.scorm.usecase.runtime.RuntimeDataInfo;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class RestSCOModel
{
  private String name;
  private String path;
  private Map<String, RuntimeDataInfo> runtimeData;

  public RestSCOModel(String name, String path, Map<String, RuntimeDataInfo> runtimeData)
  {
    this.name = name;
    this.path = path;
    this.runtimeData = runtimeData;
  }

  public String getName()
  {
    return name;
  }

  public String getPath()
  {
    return path;
  }

  public Map<String, RuntimeDataInfo> getRuntimeData()
  {
    return runtimeData;
  }
}

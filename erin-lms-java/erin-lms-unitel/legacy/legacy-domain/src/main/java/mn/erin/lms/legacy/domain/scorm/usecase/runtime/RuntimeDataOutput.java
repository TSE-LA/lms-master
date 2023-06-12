/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.scorm.usecase.runtime;

import java.util.Collections;
import java.util.Map;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class RuntimeDataOutput
{
  private final String runtimeDataId;
  private final String scormContentId;
  private final String scoName;
  private final Map<String, RuntimeDataInfo> runtimeData;

  RuntimeDataOutput(String runtimeDataId, String scormContentId, String scoName,
      Map<String, RuntimeDataInfo> runtimeData)
  {
    this.runtimeDataId = runtimeDataId;
    this.scormContentId = scormContentId;
    this.scoName = scoName;
    this.runtimeData = runtimeData;
  }

  public String getRuntimeDataId()
  {
    return runtimeDataId;
  }

  public String getScormContentId()
  {
    return scormContentId;
  }

  public String getScoName()
  {
    return scoName;
  }

  public Map<String, RuntimeDataInfo> getRuntimeData()
  {
    return Collections.unmodifiableMap(runtimeData);
  }
}

/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.scorm.usecase.runtime;

import java.util.Map;

import org.apache.commons.lang3.Validate;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class SaveRuntimeDataInput
{
  private final String scormContentId;
  private final String scoName;
  private final Map<String, Object> data;

  public SaveRuntimeDataInput(String scormContentId, String scoName, Map<String, Object> data)
  {
    this.scormContentId = Validate.notBlank(scormContentId, "SCORM content ID cannot be null or blank!");
    this.scoName = Validate.notBlank(scoName, "SCO name cannot be null or blank!");
    this.data = Validate.notEmpty(data, "Runtime data cannot be null or empty!");
  }

  public String getScormContentId()
  {
    return scormContentId;
  }

  public String getScoName()
  {
    return scoName;
  }

  public Map<String, Object> getData()
  {
    return data;
  }
}

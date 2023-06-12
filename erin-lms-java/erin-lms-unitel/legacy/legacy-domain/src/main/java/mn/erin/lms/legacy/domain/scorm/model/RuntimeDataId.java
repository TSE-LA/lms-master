/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.scorm.model;

import mn.erin.domain.base.model.EntityId;

/**
 * Represents the unique ID of a Runtime Data
 *
 * @author Bat-Erdene Tsogoo.
 */
public class RuntimeDataId extends EntityId
{
  public RuntimeDataId(String id)
  {
    super(id);
  }

  public static RuntimeDataId valueOf(String id)
  {
    return new RuntimeDataId(id);
  }
}

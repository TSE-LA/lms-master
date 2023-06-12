/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.base.scorm.model;

import mn.erin.domain.base.model.EntityId;

/**
 * Represents the asset ID.
 *
 * @author Bat-Erdene Tsogoo.
 */
public class AssetId extends EntityId
{
  private AssetId(String id)
  {
    super(id);
  }

  public static AssetId valueOf(String id)
  {
    return new AssetId(id);
  }
}

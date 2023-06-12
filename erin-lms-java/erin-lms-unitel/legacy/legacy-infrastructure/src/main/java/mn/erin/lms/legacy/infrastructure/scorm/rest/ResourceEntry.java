/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.infrastructure.scorm.rest;

import java.io.Serializable;
import java.util.List;

import org.springframework.lang.Nullable;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class ResourceEntry implements Serializable
{
  private static final long serialVersionUID = -4951751722618973590L;

  private String assetId;

  @Nullable
  private List<String> dependencyIds;

  public String getAssetId()
  {
    return assetId;
  }

  public List<String> getDependencies()
  {
    return dependencyIds;
  }
}

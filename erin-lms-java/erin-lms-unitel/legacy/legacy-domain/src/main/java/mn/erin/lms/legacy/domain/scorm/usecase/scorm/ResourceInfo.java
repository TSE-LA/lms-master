/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.scorm.usecase.scorm;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.Validate;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class ResourceInfo
{
  private final String assetId;

  private Set<String> dependencyIds = new HashSet<>();

  public ResourceInfo(String assetId)
  {
    this.assetId = Validate.notBlank(assetId, "Asset ID cannot be null or blank!");
  }

  String getAssetId()
  {
    return assetId;
  }

  Set<String> getDependencies()
  {
    return Collections.unmodifiableSet(dependencyIds);
  }

  public void addDependency(String dependency)
  {
    Validate.notBlank(dependency, "Dependency cannot be null or blank!");
    this.dependencyIds.add(dependency);
  }
}

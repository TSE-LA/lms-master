/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.base.scorm.model;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.model.Aggregate;

/**
 * Shareable Content Object (SCO):
 * <p>
 * At the heart of a SCORM content is a SCO, the instructional part of the content that
 * communicates with LMS through "Run-Time" calls {@link RuntimeData}. A SCO is composed of one or more assets
 * {@link Asset}, and it must specify a pointer/path to which the LMS should redirect the learner.
 *
 * @author Bat-Erdene Tsogoo.
 */
public class SCO implements Aggregate<SCORMContent>
{
  private final SCORMContent scormContent;
  private final String name;

  private String path;

  private Set<AssetId> assets = new LinkedHashSet<>();

  public SCO(SCORMContent scormContent, String name, String path)
  {
    this.scormContent = Objects.requireNonNull(scormContent, "SCORM content cannot be null!");
    this.name = Validate.notBlank(name, "SCO name cannot be null or blank!");
    this.path = Validate.notBlank(path, "SCO path cannot be null or blank!");
  }

  public SCO(SCORMContent scormContent, String name)
  {
    this.scormContent = Objects.requireNonNull(scormContent, "SCORM content cannot be null!");
    this.name = Validate.notBlank(name, "SCO name cannot be null or blank!");
  }

  @Override
  public SCORMContent getRootEntity()
  {
    return this.scormContent;
  }

  public void addAsset(AssetId assetId)
  {
    if (assetId != null)
    {
      this.assets.add(assetId);
    }
  }

  public void changePath(String newPath)
  {
    Validate.notBlank(newPath, "New SCO path cannot be null or blank!");
    this.path = newPath;
  }

  public String getPath()
  {
    return path;
  }

  public Set<AssetId> getAssets()
  {
    return Collections.unmodifiableSet(assets);
  }

  public String getName()
  {
    return name;
  }
}

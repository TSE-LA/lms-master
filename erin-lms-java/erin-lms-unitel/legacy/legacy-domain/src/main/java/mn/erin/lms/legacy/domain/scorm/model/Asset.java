/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.scorm.model;

import java.util.Objects;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.model.Content;
import mn.erin.domain.base.model.Entity;

/**
 * A SCORM content {@link SCORMContent} in its most basic form is composed of assets
 * An asset in the SCORM domain is a static file that constitutes the logical
 * unit of a SCORM content. An asset could be any of the web supported files,
 * such as images, videos, audio and HTML pages etc.
 *
 * @author Bat-Erdene Tsogoo.
 */
public class Asset implements Entity<Asset>
{
  private final AssetId id;
  private final String name;
  private final Content assetContent;

  public Asset(AssetId id, String name, Content assetContent)
  {
    this.id = Objects.requireNonNull(id, "Asset Id cannot be null!");
    this.name = Validate.notBlank(name, "Asset name cannot be null or blank!");
    this.assetContent = Objects.requireNonNull(assetContent, "Asset content cannot be null!");
  }

  @Override
  public boolean sameIdentityAs(Asset other)
  {
    return other != null && id.sameValueAs(other.id);
  }

  public AssetId getAssetId()
  {
    return id;
  }

  public String getName()
  {
    return name;
  }

  public Content getAssetContent()
  {
    return assetContent;
  }
}

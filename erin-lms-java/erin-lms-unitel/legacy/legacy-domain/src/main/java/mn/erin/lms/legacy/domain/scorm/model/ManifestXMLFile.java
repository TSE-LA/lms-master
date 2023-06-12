/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.scorm.model;

import java.util.Objects;

import mn.erin.domain.base.model.Aggregate;
import mn.erin.domain.base.model.Content;

/**
 * Represents SCORM manifest XML file ("imsmanifest.xml") as a type of Asset.
 * The only difference is that manifest XML file must belong to a SCORM content.
 *
 * @author Bat-Erdene Tsogoo.
 */
public class ManifestXMLFile extends Asset implements Aggregate<SCORMContent>
{
  private final SCORMContent scormContent;

  public ManifestXMLFile(AssetId id, String name, Content assetContent, SCORMContent scormContent)
  {
    super(id, name, assetContent);
    this.scormContent = Objects.requireNonNull(scormContent, "SCORM content is required for a manifest XML file");
  }

  @Override
  public SCORMContent getRootEntity()
  {
    return this.scormContent;
  }
}

/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.base.scorm.model;

import java.util.Objects;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.model.Entity;

/**
 * Shareable Content Object Reference Model (SCORM) is an eLearning standard that defines a specific
 * way to create training contents so that they can be shared and reused across SCORM-compliant
 * Learning Management Systems.
 *
 * For more information, please refer to https://scorm.com/scorm-explained/
 *
 * @author Bat-Erdene Tsogoo.
 */
public class SCORMContent implements Entity<SCORMContent>
{
  private final SCORMContentId id;
  private final String name;

  public SCORMContent(SCORMContentId id, String name)
  {
    this.id = Objects.requireNonNull(id, "SCORM content id cannot be null!");
    this.name = Validate.notBlank(name, "SCORM content name cannot be null or blank!");
  }

  @Override
  public boolean sameIdentityAs(SCORMContent other)
  {
    return other != null && this.id.sameValueAs(other.id);
  }

  public SCORMContentId getScormContentId()
  {
    return id;
  }

  public String getName()
  {
    return name;
  }
}

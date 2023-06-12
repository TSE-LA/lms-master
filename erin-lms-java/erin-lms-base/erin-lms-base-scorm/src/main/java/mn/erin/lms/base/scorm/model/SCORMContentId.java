/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.base.scorm.model;

import mn.erin.domain.base.model.EntityId;

/**
 * Represents the unique ID of a SCORM content.
 *
 * @author Bat-Erdene Tsogoo.
 */
public class SCORMContentId extends EntityId
{
  private SCORMContentId(String id)
  {
    super(id);
  }

  public static SCORMContentId valueOf(String id)
  {
    return new SCORMContentId(id);
  }
}

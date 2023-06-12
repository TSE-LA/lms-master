/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.infrastructure.scorm.repository.mongo;

import java.io.Serializable;

import mn.erin.lms.legacy.domain.scorm.model.DataModelConstraint;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class RuntimeDataInfo implements Serializable
{
  private static final long serialVersionUID = -6680942547695067298L;

  private DataModelConstraint constraint;
  private String value;

  public RuntimeDataInfo(DataModelConstraint constraint, String value)
  {
    this.constraint = constraint;
    this.value = value;
  }

  public String getValue()
  {
    return value;
  }

  public DataModelConstraint getConstraint()
  {
    return constraint;
  }
}

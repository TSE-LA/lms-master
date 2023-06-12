/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.base.scorm;

import java.util.Objects;

import mn.erin.lms.base.scorm.model.DataModelConstraint;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class RuntimeDataInfo
{
  private final String data;
  private final DataModelConstraint constraint;

  private RuntimeDataInfo(String data, DataModelConstraint constraint)
  {
    this.data = Objects.requireNonNull(data, "Data cannot be null!");
    this.constraint = Objects.requireNonNull(constraint, "Data model constraint cannot be null!");
  }

  public static RuntimeDataInfo of(String data, DataModelConstraint constraint)
  {
    return new RuntimeDataInfo(data, constraint);
  }

  public String getData()
  {
    return data;
  }

  public DataModelConstraint getConstraint()
  {
    return constraint;
  }
}

/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.scorm.usecase.runtime;

import java.util.Objects;

import mn.erin.lms.legacy.domain.scorm.model.DataModelConstraint;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class RuntimeDataInfo
{
  private final Object data;
  private final DataModelConstraint constraint;

  private RuntimeDataInfo(Object data, DataModelConstraint constraint)
  {
    this.data = Objects.requireNonNull(data, "Data cannot be null!");
    this.constraint = Objects.requireNonNull(constraint, "Data model constraint cannot be null!");
  }

  public static RuntimeDataInfo of(Object data, DataModelConstraint constraint)
  {
    return new RuntimeDataInfo(data, constraint);
  }

  public Object getData()
  {
    return data;
  }

  public DataModelConstraint getConstraint()
  {
    return constraint;
  }
}

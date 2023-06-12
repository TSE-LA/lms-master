/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.base.scorm.model;

import java.util.Objects;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.model.ValueObject;

/**
 * SCORM data models or run-time elements are used to track a learner's data in a SCORM content.
 * These elements are usually strings with a prefix 'cmi' or 'adl', followed by a data model element.
 * Examples include: "cmi.total_time", "cmi.success_status", 'cmi.objectives.n.score.raw' etc.
 * The data models must also specify whether they are read-only, write-only or read-write data models.
 *
 * @author Bat-Erdene Tsogoo.
 */
public class DataModel implements ValueObject<DataModel>
{
  private final String name;
  private final DataModelConstraint constraint;

  private String defaultValue;

  public DataModel(String name, DataModelConstraint constraint)
  {
    this.name = validate(name);
    this.constraint = Objects.requireNonNull(constraint, "Constraint must be specified for a data model!");
  }

  public String getName()
  {
    return name;
  }

  public DataModelConstraint getConstraint()
  {
    return constraint;
  }

  /**
   * The comprehensive list of available data models can be found on:
   * https://scorm.com/scorm-explained/technical-scorm/run-time/run-time-reference/
   * <p>
   * The majority of the data models -- at least the ones that are used extensively -- contain a prefix 'cmi',
   * followed by parts or elements separated by dots. Currently, there is no known data model having more than 5 parts
   * and less than one part. Meaning, "cmi.part.part.part.part.part" and "cmi." would be considered illegal.
   *
   * @param dataModel Data model name
   * @return Unmodified data model name
   */
  private static String validate(String dataModel)
  {
    dataModel = Validate.notBlank(dataModel, "Data model name cannot be null or blank!");

    String[] parts = dataModel.split("\\.");
    if ((parts.length > 5) || (parts.length == 1))
    {
      throw new IllegalArgumentException("Invalid data model [" + dataModel + "] was received!");
    }

    return dataModel;
  }

  @Override
  public boolean sameValueAs(DataModel other)
  {
    return other != null && this.name.equals(other.name);
  }

  @Override
  public int hashCode()
  {
    return Objects.hash(name);
  }

  @Override
  public boolean equals(Object obj)
  {
    if (obj instanceof DataModel)
    {
      return sameValueAs((DataModel) obj);
    }

    return false;
  }

  public void setDefaultValue(String value)
  {
    this.defaultValue = value;
  }

  public String getDefaultValue()
  {
    return defaultValue;
  }
}

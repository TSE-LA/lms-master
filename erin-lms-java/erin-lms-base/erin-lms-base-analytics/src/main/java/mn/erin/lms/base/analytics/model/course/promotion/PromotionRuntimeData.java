package mn.erin.lms.base.analytics.model.course.promotion;

import java.util.Objects;

import mn.erin.lms.base.scorm.model.DataModelConstraint;

/**
 * @author Oyungerel Chuluunsukh
 **/
public class PromotionRuntimeData
{
  private final String value;
  private final DataModelConstraint constraint;

  private PromotionRuntimeData(String data, DataModelConstraint constraint)
  {
    this.value = Objects.requireNonNull(data, "Value cannot be null!");
    this.constraint = Objects.requireNonNull(constraint, "Data model constraint cannot be null!");
  }

  public static PromotionRuntimeData of(String data, DataModelConstraint constraint)
  {
    return new PromotionRuntimeData(data, constraint);
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


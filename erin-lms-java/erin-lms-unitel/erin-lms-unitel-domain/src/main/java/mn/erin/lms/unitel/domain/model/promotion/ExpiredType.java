package mn.erin.lms.unitel.domain.model.promotion;

import mn.erin.lms.base.domain.model.course.CourseType;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class ExpiredType implements CourseType
{
  @Override
  public String getType()
  {
    return PromotionType.EXPIRED.name();
  }
}

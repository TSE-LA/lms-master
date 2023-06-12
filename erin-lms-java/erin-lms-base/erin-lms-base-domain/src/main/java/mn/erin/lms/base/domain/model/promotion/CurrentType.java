package mn.erin.lms.base.domain.model.promotion;

import mn.erin.lms.base.domain.model.course.CourseType;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class CurrentType implements CourseType
{
  @Override
  public String getType()
  {
    return PromotionType.CURRENT.name();
  }
}

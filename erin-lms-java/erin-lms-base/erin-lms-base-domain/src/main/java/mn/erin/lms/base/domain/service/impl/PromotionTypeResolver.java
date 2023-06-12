package mn.erin.lms.base.domain.service.impl;

import org.apache.commons.lang3.Validate;

import mn.erin.lms.base.domain.model.course.CourseType;
import mn.erin.lms.base.domain.service.CourseTypeResolver;
import mn.erin.lms.base.domain.service.UnknownCourseTypeException;
import mn.erin.lms.base.domain.model.promotion.CurrentType;
import mn.erin.lms.base.domain.model.promotion.ExpiredType;
import mn.erin.lms.base.domain.model.promotion.MainType;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class PromotionTypeResolver implements CourseTypeResolver
{
  private static final CurrentType CURRENT = new CurrentType();
  private static final MainType MAIN = new MainType();
  private static final ExpiredType EXPIRED = new ExpiredType();

  @Override
  public CourseType resolve(String type) throws UnknownCourseTypeException
  {
    Validate.notBlank(type, "Type cannot be null or blank!");

    if (type.equalsIgnoreCase(CURRENT.getType()))
    {
      return CURRENT;
    }
    else if (type.equalsIgnoreCase(MAIN.getType()))
    {
      return MAIN;
    }
    else if (type.equalsIgnoreCase(EXPIRED.getType()))
    {
      return EXPIRED;
    }
    else
    {
      throw new UnknownCourseTypeException("Unknown course type");
    }
  }
}

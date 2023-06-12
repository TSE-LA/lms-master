package mn.erin.lms.unitel.domain.service;

import mn.erin.lms.base.domain.model.course.CourseType;
import mn.erin.lms.base.domain.service.CourseTypeResolver;
import mn.erin.lms.base.domain.service.UnknownCourseTypeException;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class MainCourseTypeResolver implements CourseTypeResolver
{
  private static final PromotionTypeResolver PROMOTION = new PromotionTypeResolver();
  private static final OnlineCourseTypeResolver ONLINE_COURSE = new OnlineCourseTypeResolver();

  // TODO REFACTOR ME
  @Override
  public CourseType resolve(String type) throws UnknownCourseTypeException
  {
    try
    {
      return PROMOTION.resolve(type);
    }
    catch (UnknownCourseTypeException e)
    {
      return ONLINE_COURSE.resolve(type);
    }
  }
}

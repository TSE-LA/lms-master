package mn.erin.lms.legacy.domain.unitel.usecase.promotion;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.lms.legacy.domain.lms.usecase.course.get_course.GetCourseOutput;
import mn.erin.lms.legacy.domain.unitel.PromotionConstants;
import mn.erin.lms.legacy.domain.unitel.model.PromotionState;
import mn.erin.lms.legacy.domain.unitel.model.PromotionType;

/**
 * @author Bat-Erdene Tsogoo.
 */
public abstract class PromotionUseCase<I, O> implements UseCase<I, O>
{
  protected Map<String, Object> mapToRequiredProperties(PromotionDetail promotionDetail)
  {
    PromotionType promotionType = PromotionType.valueOf(promotionDetail.getType());
    PromotionState promotionState = PromotionState.valueOf(promotionDetail.getPromotionState());

    Map<String, Object> promotionProperties = new HashMap<>();

    promotionProperties.put(PromotionConstants.PROPERTY_CODE, promotionDetail.getCode());
    promotionProperties.put(PromotionConstants.PROPERTY_KEYWORD, promotionDetail.getKeyword());
    promotionProperties.put(PromotionConstants.PROPERTY_START_DATE, promotionDetail.getStartDate());
    promotionProperties.put(PromotionConstants.PROPERTY_END_DATE, promotionDetail.getEndDate());
    promotionProperties.put(PromotionConstants.PROPERTY_TYPE, promotionType.getType());
    promotionProperties.put(PromotionConstants.PROPERTY_STATE, promotionState.getStateName());

    return promotionProperties;
  }

  protected PromotionDetail getPromotionDetail(GetCourseOutput course)
  {
    Map<String, Object> courseProperties = course.getProperties();

    return new PromotionDetail.Builder(course.getId())
        .createdBy(course.getAuthorId())
        .withName(course.getTitle())
        .withDescription(course.getDescription())
        .withNote(course.getNote())
        .withStatus(course.getPublishStatus())
        .modifiedBy((String) courseProperties.get(PromotionConstants.PROPERTY_MODIFIED_USER))
        .withCode((String) courseProperties.get(PromotionConstants.PROPERTY_CODE))
        .withType((String) courseProperties.get(PromotionConstants.PROPERTY_TYPE))
        .withState((String) courseProperties.get(PromotionConstants.PROPERTY_STATE))
        .withKeyword((String) courseProperties.get(PromotionConstants.PROPERTY_KEYWORD))
        .starts((Date) courseProperties.get(PromotionConstants.PROPERTY_START_DATE))
        .ends((Date) courseProperties.get(PromotionConstants.PROPERTY_END_DATE))
        .build();
  }
}

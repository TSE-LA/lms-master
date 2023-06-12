package mn.erin.lms.legacy.domain.unitel.service;

import java.util.Set;

import mn.erin.lms.legacy.domain.lms.model.enrollment.LearnerId;
import mn.erin.lms.legacy.domain.unitel.model.PromotionAnalyticData;

/**
 * @author Bat-Erdene Tsogoo.
 */
public interface PromotionAnalytics
{
  Set<PromotionAnalyticData> getPromotionAnalyticData(String promotionContentId);

  Set<PromotionAnalyticData> getPromotionAnalyticData(LearnerId learnerId);
}

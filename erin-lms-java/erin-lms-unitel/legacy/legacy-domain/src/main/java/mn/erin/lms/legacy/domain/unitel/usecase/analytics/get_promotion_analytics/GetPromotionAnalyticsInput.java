package mn.erin.lms.legacy.domain.unitel.usecase.analytics.get_promotion_analytics;

import org.apache.commons.lang3.Validate;

import mn.erin.lms.legacy.domain.unitel.usecase.analytics.AnalyticsFilter;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class GetPromotionAnalyticsInput
{
  private final String promotionId;
  private final AnalyticsFilter analyticsFilter;

  public GetPromotionAnalyticsInput(String promotionId, AnalyticsFilter analyticsFilter)
  {
    this.promotionId = Validate.notBlank(promotionId, "Promotion ID cannot be null or blank!");
    this.analyticsFilter = Validate.notNull(analyticsFilter, "Filter cannot be null!");
  }

  public String getPromotionId()
  {
    return promotionId;
  }

  public AnalyticsFilter getAnalyticsFilter()
  {
    return analyticsFilter;
  }
}

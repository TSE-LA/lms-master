package mn.erin.lms.legacy.domain.unitel.usecase.analytics.get_promotion_count;

import java.util.Set;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class PromotionCount
{
  private final String promotionState;
  private final Set<PromotionCountByCategory> promotionCountByCategories;

  public PromotionCount(String promotionState, Set<PromotionCountByCategory> promotionCountByCategories)
  {
    this.promotionState = promotionState;
    this.promotionCountByCategories = promotionCountByCategories;
  }

  public String getPromotionState()
  {
    return promotionState;
  }

  public Set<PromotionCountByCategory> getPromotionCountByCategories()
  {
    return promotionCountByCategories;
  }
}

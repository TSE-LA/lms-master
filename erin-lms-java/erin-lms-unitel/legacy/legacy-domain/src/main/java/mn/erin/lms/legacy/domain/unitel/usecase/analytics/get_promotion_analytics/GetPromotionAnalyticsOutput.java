package mn.erin.lms.legacy.domain.unitel.usecase.analytics.get_promotion_analytics;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class GetPromotionAnalyticsOutput
{
  private Set<PromotionAnalyticsOutput> analyticData = new HashSet<>();

  public void addAnalyticData(PromotionAnalyticsOutput analyticData)
  {
    if (analyticData != null)
    {
      this.analyticData.add(analyticData);
    }
  }

  public Set<PromotionAnalyticsOutput> getAnalyticData()
  {
    return Collections.unmodifiableSet(analyticData);
  }
}

package mn.erin.lms.legacy.domain.unitel.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.Validate;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class EmployeePromotionPoints
{
  private final String userName;
  private final Integer promoPoints;

  private List<String> launchedPromotionIds = new ArrayList<>();

  public EmployeePromotionPoints(String userName, Integer averagePoint)
  {
    this.userName = Validate.notEmpty(userName, "Employee username cannot be null or blank!");
    this.promoPoints = Validate.notNull(averagePoint, "Employee average point cannot be null!");
  }

  public EmployeePromotionPoints(String userName, Integer averagePoint, List<String> launchedPromotionIds)
  {
    this.userName = Validate.notEmpty(userName, "Employee username cannot be null or blank!");
    this.promoPoints = Validate.notNull(averagePoint, "Employee average point cannot be null!");
    this.launchedPromotionIds = Validate.notNull(launchedPromotionIds, "Launched promotion IDs cannot be null!");

    if (launchedPromotionIds.isEmpty() && averagePoint > 0)
    {
      throw new IllegalStateException("An employee cannot have an average point greater than 0 without any launched promo");
    }
  }

  public String getUserName()
  {
    return userName;
  }

  public Integer getPromoPoints()
  {
    return promoPoints;
  }

  public List<String> getLaunchedPromotionIds()
  {
    return launchedPromotionIds;
  }
}

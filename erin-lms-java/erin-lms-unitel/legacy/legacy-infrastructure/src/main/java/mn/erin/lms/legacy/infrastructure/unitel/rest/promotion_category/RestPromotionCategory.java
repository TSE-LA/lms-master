package mn.erin.lms.legacy.infrastructure.unitel.rest.promotion_category;

import org.springframework.lang.Nullable;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class RestPromotionCategory
{
  private String categoryName;

  @Nullable
  private String description;

  public String getCategoryName()
  {
    return categoryName;
  }

  public String getDescription()
  {
    return description;
  }
}

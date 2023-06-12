package mn.erin.lms.legacy.domain.unitel.usecase.analytics.get_promotion_count;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class PromotionCountByCategory
{
  private final String categoryName;
  private final String categoryId;
  private final Integer publishedPromotionCount;
  private final Integer unpublishedPromotionCount;

  public PromotionCountByCategory(String categoryName, String categoryId,Integer publishedPromotionCount, Integer unpublishedPromotionCount)
  {
    this.categoryName = categoryName;
    this.categoryId = categoryId;
    this.publishedPromotionCount = publishedPromotionCount;
    this.unpublishedPromotionCount = unpublishedPromotionCount;
  }

  public String getCategoryName()
  {
    return categoryName;
  }

  public Integer getPublishedPromotionCount()
  {
    return publishedPromotionCount;
  }

  public Integer getUnpublishedPromotionCount()
  {
    return unpublishedPromotionCount;
  }

  public String getCategoryId() { return categoryId; }
}

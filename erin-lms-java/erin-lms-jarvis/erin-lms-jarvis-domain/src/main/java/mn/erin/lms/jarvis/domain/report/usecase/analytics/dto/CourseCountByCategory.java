package mn.erin.lms.jarvis.domain.report.usecase.analytics.dto;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class CourseCountByCategory
{
  private final String categoryName;
  private final String categoryId;
  private final Integer unpublishedCount;
  private final Integer publishedCount;

  public CourseCountByCategory(String categoryName,  String categoryId, Integer unpublishedCount, Integer publishedCount)
  {
    this.categoryName = categoryName;
    this.categoryId = categoryId;
    this.unpublishedCount = unpublishedCount;
    this.publishedCount = publishedCount;
  }

  public String getCategoryName()
  {
    return categoryName;
  }

  public Integer getUnpublishedCount()
  {
    return unpublishedCount;
  }

  public Integer getPublishedCount()
  {
    return publishedCount;
  }

  public String getCategoryId() { return  categoryId;}
}

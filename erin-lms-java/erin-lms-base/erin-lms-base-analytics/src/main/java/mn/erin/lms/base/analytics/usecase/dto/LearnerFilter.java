package mn.erin.lms.base.analytics.usecase.dto;

/**
 * @author Munkh.
 */
public class LearnerFilter
{
  private final String learnerId;
  private final DateFilter dateFilter;
  private final String categoryId;
  private final String groupId;

  public LearnerFilter(String learnerId, DateFilter dateFilter, String categoryId, String groupId)
  {
    this.learnerId = learnerId;
    this.dateFilter = dateFilter;
    this.categoryId = categoryId;
    this.groupId = groupId;
  }

  public String getLearnerId()
  {
    return learnerId;
  }

  public DateFilter getDateFilter()
  {
    return dateFilter;
  }

  public String getCategoryId()
  {
    return categoryId;
  }

  public String getGroupId() {return groupId; }
}

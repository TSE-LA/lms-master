package mn.erin.lms.base.analytics.usecase.dto;


/**
 * @author Byambajav
 */
public class ExamFilter
{
  private final DateFilter dateFilter;
  private String categoryId;
  private String type;
  private String groupId;
  private String status;

  public ExamFilter(DateFilter dateFilter)
  {
    this.dateFilter = dateFilter;
  }

  public DateFilter getDateFilter()
  {
    return dateFilter;
  }

  public String getCategoryId()
  {
    return categoryId;
  }

  public void setCategoryId(String categoryId)
  {
    this.categoryId = categoryId;
  }

  public String getType()
  {
    return type;
  }

  public void setType(String type)
  {
    this.type = type;
  }

  public String getGroupId()
  {
    return groupId;
  }

  public void setGroupId(String groupId)
  {
    this.groupId = groupId;
  }

  public String getStatus()
  {
    return status;
  }

  public void setStatus(String status)
  {
    this.status = status;
  }
}

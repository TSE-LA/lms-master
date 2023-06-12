package mn.erin.lms.base.analytics.usecase.dto;

/**
 * @author Munkh
 */
public class CourseFilter
{
  private final String groupId;
  private final DateFilter dateFilter;
  private String categoryId;
  private String courseType;
  private String categoryName;

  public CourseFilter(String groupId, DateFilter dateFilter)
  {
    this.groupId = groupId;
    this.dateFilter = dateFilter;
  }

  public String getGroupId()
  {
    return groupId;
  }

  public String getCategoryId()
  {
    return categoryId;
  }

  public void setCategoryId(String categoryId)
  {
    this.categoryId = categoryId;
  }

  public String getCourseType()
  {
    return courseType;
  }

  public void setCourseType(String courseType)
  {
    this.courseType = courseType;
  }

  public DateFilter getDateFilter()
  {
    return dateFilter;
  }

  public String getCategoryName()
  {
    return categoryName;
  }

  public void setCategoryName(String categoryName)
  {
    this.categoryName = categoryName;
  }
}

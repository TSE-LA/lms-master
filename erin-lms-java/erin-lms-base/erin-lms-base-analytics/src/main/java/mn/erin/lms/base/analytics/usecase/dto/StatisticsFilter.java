package mn.erin.lms.base.analytics.usecase.dto;

/**
 * @author Munkh
 */
public class StatisticsFilter
{
  private final String groupId;
  private final String courseId;
  private final DateFilter dateFilter;

  public StatisticsFilter(String groupId, String courseId, DateFilter dateFilter)
  {
    this.groupId = groupId;
    this.courseId = courseId;
    this.dateFilter = dateFilter;
  }

  public String getGroupId()
  {
    return groupId;
  }

  public String getCourseId()
  {
    return courseId;
  }

  public DateFilter getDateFilter()
  {
    return dateFilter;
  }
}

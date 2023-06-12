package mn.erin.lms.legacy.domain.lms.usecase.report;

import java.util.Date;
import java.util.Map;

import mn.erin.lms.legacy.domain.lms.usecase.course.get_course_analytics.Filter;

public class ReportFilter extends Filter
{
  private String groupId;
  private String category;
  private Map<String, Object> coursePropertyFilter;
  private String learnerId;

  public ReportFilter(Date startDate, Date endDate)
  {
    super(startDate, endDate);
  }

  public String getCategory()
  {
    return category;
  }

  public void setCategory(String category)
  {
    this.category = category;
  }

  public Map<String, Object> getCoursePropertyFilter()
  {
    return coursePropertyFilter;
  }

  public void setProperties(Map<String, Object> coursePropertyFilter)
  {
    this.coursePropertyFilter = coursePropertyFilter;
  }

  public String getGroupId()
  {
    return groupId;
  }

  public void setGroupId(String groupId)
  {
    this.groupId = groupId;
  }

  public void setCoursePropertyFilter(Map<String, Object> coursePropertyFilter)
  {
    this.coursePropertyFilter = coursePropertyFilter;
  }

  public String getLearnerId()
  {
    return learnerId;
  }

  public void setLearnerId(String learnerId)
  {
    this.learnerId = learnerId;
  }
}

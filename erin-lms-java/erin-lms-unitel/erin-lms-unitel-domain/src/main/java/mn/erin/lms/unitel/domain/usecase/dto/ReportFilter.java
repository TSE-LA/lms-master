package mn.erin.lms.unitel.domain.usecase.dto;

import java.time.LocalDate;
import java.util.Map;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class ReportFilter
{
  private final String departmentId;
  private final LocalDate startDate;
  private final LocalDate endDate;
  private String categoryId;
  private String courseType;
  private String reportType;
  private String learnerId;
  private Map<String, String> durationList;

  public ReportFilter(String departmentId, LocalDate startDate, LocalDate endDate)
  {
    this.departmentId = departmentId;
    this.startDate = startDate;
    this.endDate = endDate;
  }

  public void setCategoryId(String categoryId)
  {
    this.categoryId = categoryId;
  }

  public void setCourseType(String courseType)
  {
    this.courseType = courseType;
  }

  public void setReportType(String reportType)
  {
    this.reportType = reportType;
  }

  public void setLearnerId(String learnerId)
  {
    this.learnerId = learnerId;
  }

  public String getDepartmentId()
  {
    return departmentId;
  }

  public LocalDate getStartDate()
  {
    return startDate;
  }

  public LocalDate getEndDate()
  {
    return endDate;
  }

  public String getCategoryId()
  {
    return categoryId;
  }

  public String getCourseType()
  {
    return courseType;
  }

  public String getReportType()
  {
    return reportType;
  }

  public String getLearnerId()
  {
    return learnerId;
  }

  public Map<String, String> getDurationList()
  {
    return durationList;
  }

  public void setDurationList(Map<String, String> durationList)
  {
    this.durationList = durationList;
  }
}

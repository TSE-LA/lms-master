package mn.erin.lms.jarvis.domain.report.usecase.dto;

import java.util.List;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class UserActivityDataDto
{
  private Integer employeeCount;
  private Integer perfectEmployeeCount;
  private Integer perfectSupersCount;
  private String averageTime;

  private List<OtherActivityData> otherActivityDataList;

  public Integer getEmployeeCount()
  {
    return employeeCount;
  }

  public void setEmployeeCount(Integer employeeCount)
  {
    this.employeeCount = employeeCount;
  }

  public Integer getPerfectEmployeeCount()
  {
    return perfectEmployeeCount;
  }

  public void setPerfectEmployeeCount(Integer perfectEmployeeCount)
  {
    this.perfectEmployeeCount = perfectEmployeeCount;
  }

  public Integer getPerfectSupersCount()
  {
    return perfectSupersCount;
  }

  public void setPerfectSupersCount(Integer perfectSupersCount)
  {
    this.perfectSupersCount = perfectSupersCount;
  }

  public String getAverageTime()
  {
    return averageTime;
  }

  public void setAverageTime(String averageTime)
  {
    this.averageTime = averageTime;
  }

  public List<OtherActivityData> getOtherActivityDataList()
  {
    return otherActivityDataList;
  }

  public void setOtherActivityDataList(List<OtherActivityData> otherActivityDataList)
  {
    this.otherActivityDataList = otherActivityDataList;
  }
}

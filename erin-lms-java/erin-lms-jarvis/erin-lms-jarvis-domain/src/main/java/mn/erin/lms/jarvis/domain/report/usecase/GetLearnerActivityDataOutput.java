package mn.erin.lms.jarvis.domain.report.usecase;

public class GetLearnerActivityDataOutput
{
  private Integer employeeCount;
  private Integer perfectEmployeeCount;
  private Integer perfectSupersCount;
  private String averageTime;

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
}

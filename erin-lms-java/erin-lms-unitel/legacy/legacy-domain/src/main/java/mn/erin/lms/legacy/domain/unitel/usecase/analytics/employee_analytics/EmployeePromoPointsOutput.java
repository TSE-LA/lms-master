package mn.erin.lms.legacy.domain.unitel.usecase.analytics.employee_analytics;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class EmployeePromoPointsOutput
{
  private Integer currentEmployeeScore = 0;
  private List<Integer> otherEmployeesScore = new ArrayList<>();

  public Integer getCurrentEmployeeScore()
  {
    return currentEmployeeScore;
  }

  public List<Integer> getOtherEmployeesScore()
  {
    return otherEmployeesScore;
  }

  public void setCurrentEmployeeScore(Integer currentEmployeeScore)
  {
    this.currentEmployeeScore = currentEmployeeScore;
  }

  public void addOtherEmployeeScore(Integer score)
  {
    if (score != null)
    {
      this.otherEmployeesScore.add(score);
    }
  }
}

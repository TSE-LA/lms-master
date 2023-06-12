package mn.erin.lms.legacy.infrastructure.unitel.rest.analytics;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class RestEmployeeScoreStats
{
  private Integer currentEmployeeScore;
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

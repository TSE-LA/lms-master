package mn.erin.lms.legacy.domain.unitel.usecase.analytics.employee_analytics;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.legacy.domain.lms.model.enrollment.LearnerId;
import mn.erin.lms.legacy.domain.unitel.model.EmployeeAnalyticData;
import mn.erin.lms.legacy.domain.unitel.service.EmployeeAnalytics;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class GetEmployeeAnalyticData implements UseCase<GetEmployeeAnalyticDataInput, List<EmployeeAnalyticDataOutput>>
{
  private final EmployeeAnalytics analytics;

  public GetEmployeeAnalyticData(EmployeeAnalytics analytics)
  {
    this.analytics = Objects.requireNonNull(analytics, "Analytics cannot be null!");
  }

  @Override
  public List<EmployeeAnalyticDataOutput> execute(GetEmployeeAnalyticDataInput input) throws UseCaseException
  {
    if (input == null)
    {
      throw new UseCaseException("Input cannot be null!");
    }

    LearnerId learnerId = new LearnerId(input.getEmployeeId());
    Set<EmployeeAnalyticData> analyticData = analytics.getEmployeeAnalyticData(learnerId);

    if (analyticData.isEmpty())
    {
      return new ArrayList<>();
    }

    boolean areAllDistinct = analyticData.stream().map(EmployeeAnalyticData::getMonth).distinct().count() == analyticData.size();

    if (!areAllDistinct)
    {
      throw new UseCaseException("Duplicated months are found in employee [" + input.getEmployeeId() + "]'s data");
    }

    return toOutput(analyticData);
  }

  private List<EmployeeAnalyticDataOutput> toOutput(Set<EmployeeAnalyticData> analyticData)
  {
    List<EmployeeAnalyticDataOutput> result = new ArrayList<>();

    for (EmployeeAnalyticData datum : analyticData)
    {
      EmployeeAnalyticDataOutput output = new EmployeeAnalyticDataOutput();
      output.setLaunchPercentage(datum.getLaunchPercentage());
      output.setAvgTestScoreByPercentage(datum.getAvgTestScoreByPercentage());
      output.setMonth(datum.getMonth());
      result.add(output);
    }

    return result;
  }
}

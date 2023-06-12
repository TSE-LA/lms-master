package mn.erin.lms.jarvis.domain.report.usecase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.user.LearnerId;
import mn.erin.lms.jarvis.domain.report.model.analytics.EmployeeAnalyticData;
import mn.erin.lms.jarvis.domain.report.service.EmployeeAnalyticsService;
import mn.erin.lms.jarvis.domain.report.usecase.dto.EmployeeAnalyticsDto;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class GetEmployeeAnalyticData implements UseCase<String, List<EmployeeAnalyticsDto>>
{
  private final EmployeeAnalyticsService employeeAnalyticsService;

  public GetEmployeeAnalyticData(EmployeeAnalyticsService employeeAnalyticsService)
  {
    this.employeeAnalyticsService = employeeAnalyticsService;
  }

  @Override
  public List<EmployeeAnalyticsDto> execute(String employeeId) throws UseCaseException
  {
    LearnerId learnerId = LearnerId.valueOf(employeeId);
    Set<EmployeeAnalyticData> analyticData = employeeAnalyticsService.getEmployeeAnalyticData(learnerId);

    if (analyticData.isEmpty())
    {
      return Collections.emptyList();
    }

    boolean isAllDistinct = analyticData.stream().map(EmployeeAnalyticData::getMonth).distinct().count() == analyticData.size();

    if (!isAllDistinct)
    {
      throw new UseCaseException("Duplicated months are found in employee [" + employeeId + "]'s data");
    }

    return toOutput(analyticData);
  }

  private List<EmployeeAnalyticsDto> toOutput(Set<EmployeeAnalyticData> analyticData)
  {
    List<EmployeeAnalyticsDto> result = new ArrayList<>();

    for (EmployeeAnalyticData datum : analyticData)
    {
      EmployeeAnalyticsDto dto = new EmployeeAnalyticsDto();
      dto.setLaunchPercentage(datum.getLaunchPercentage());
      dto.setAvgTestScoreByPercentage(datum.getAvgTestScoreByPercentage());
      dto.setMonth(datum.getMonth());
      result.add(dto);
    }

    return result;
  }
}

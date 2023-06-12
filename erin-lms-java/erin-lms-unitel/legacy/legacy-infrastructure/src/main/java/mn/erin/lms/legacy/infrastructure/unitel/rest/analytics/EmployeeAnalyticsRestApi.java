package mn.erin.lms.legacy.infrastructure.unitel.rest.analytics;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.inject.Inject;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import mn.erin.domain.aim.repository.GroupRepository;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.infrastucture.rest.common.response.RestResponse;
import mn.erin.lms.base.aim.AccessIdentityManagement;
import mn.erin.lms.legacy.domain.unitel.service.EmployeeAnalytics;
import mn.erin.lms.legacy.domain.unitel.usecase.analytics.employee_analytics.EmployeeAnalyticDataOutput;
import mn.erin.lms.legacy.domain.unitel.usecase.analytics.employee_analytics.EmployeePromoPointsOutput;
import mn.erin.lms.legacy.domain.unitel.usecase.analytics.employee_analytics.GetEmployeeAnalyticData;
import mn.erin.lms.legacy.domain.unitel.usecase.analytics.employee_analytics.GetEmployeeAnalyticDataInput;
import mn.erin.lms.legacy.domain.unitel.usecase.analytics.employee_analytics.GetEmployeePromoPoints;
import mn.erin.lms.legacy.domain.unitel.usecase.analytics.employee_analytics.GetEmployeeTotalScore;
import mn.erin.lms.legacy.domain.unitel.usecase.analytics.user_activities.GetUserActivityData;
import mn.erin.lms.legacy.domain.unitel.usecase.analytics.user_activities.GetUserActivityDataOutput;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Api("Employee Analytics")
@RequestMapping(value = "/legacy/lms/employee-analytics", name = "Provides analytical data for 'UNITEL' employees")
public class EmployeeAnalyticsRestApi
{
  @Inject
  private AccessIdentityManagement accessIdentityManagement;

  @Inject
  private GroupRepository groupRepository;

  private final EmployeeAnalytics employeeAnalytics;

  public EmployeeAnalyticsRestApi(EmployeeAnalytics employeeAnalytics)
  {
    this.employeeAnalytics = Objects.requireNonNull(employeeAnalytics, "EmployeeAnalytics service cannot be null!");
  }

  @ApiOperation("Get employee scores in relation to other employees in the same branch")
  @GetMapping("/employee-score")
  public ResponseEntity read()
  {
    GetEmployeePromoPoints getEmployeePromoPoints = new GetEmployeePromoPoints(accessIdentityManagement, employeeAnalytics);
    EmployeePromoPointsOutput output = getEmployeePromoPoints.execute(null);
    return RestResponse.success(output);
  }

  @ApiOperation("Get employee scores in relation to other employees in the same branch")
  @GetMapping("/employee-score/{learnerId:.+}")
  public ResponseEntity read(@PathVariable String learnerId)
  {
    GetEmployeeTotalScore getEmployeeTotalScore = new GetEmployeeTotalScore(employeeAnalytics);
    int output = getEmployeeTotalScore.execute(learnerId);
    return RestResponse.success(output);
  }

  @ApiOperation("Get employee analytic data")
  @GetMapping("/employee-analytic-data")
  public ResponseEntity readEmployeeAnalyticData()
  {
    GetEmployeeAnalyticData getEmployeeAnalyticData = new GetEmployeeAnalyticData(employeeAnalytics);
    GetEmployeeAnalyticDataInput input = new GetEmployeeAnalyticDataInput(accessIdentityManagement.getCurrentUsername());

    try
    {
      List<EmployeeAnalyticDataOutput> output = getEmployeeAnalyticData.execute(input);
      return RestResponse.success(output);
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Get user activities data")
  @PostMapping("/user-activities")
  public ResponseEntity readUserActivitiesData(@RequestBody List<String> body)
  {
    GetUserActivityData getUserActivityData = new GetUserActivityData(employeeAnalytics, accessIdentityManagement, groupRepository);
    GetUserActivityDataOutput output = getUserActivityData.execute(body);
    return RestResponse.success(output);
  }

  @ApiOperation("Get all group users")
  @GetMapping("/users")
  public ResponseEntity getAllGroupUsers(@RequestParam String groupId)
  {
    Set<String> learners = accessIdentityManagement.getLearners(groupId);
    return RestResponse.success(learners);
  }
}

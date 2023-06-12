package mn.erin.lms.unitel.rest.api;

import java.util.List;
import java.util.Objects;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.infrastucture.rest.common.response.RestResponse;
import mn.erin.infrastucture.rest.common.response.RestResult;
import mn.erin.lms.base.aim.AccessIdentityManagement;
import mn.erin.lms.base.aim.user.LmsUser;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.rest.api.BaseLmsRestApi;
import mn.erin.lms.unitel.domain.service.EmployeeAnalyticsService;
import mn.erin.lms.unitel.domain.usecase.GetEmployeeAnalyticData;
import mn.erin.lms.unitel.domain.usecase.GetEmployeePromoPoints;
import mn.erin.lms.unitel.domain.usecase.GetUserActivityData;
import mn.erin.lms.unitel.domain.usecase.dto.EmployeeAnalyticsDto;
import mn.erin.lms.unitel.domain.usecase.dto.EmployeePromoPointsDto;
import mn.erin.lms.unitel.domain.usecase.dto.UserActivityDataDto;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Api("Employee Analytics REST API")
@RequestMapping(value = "/lms/employee-analytics", name = "Provides Unitel's employee analytics features")
@RestController
public class EmployeeAnalyticsRestApi extends BaseLmsRestApi
{
  private final EmployeeAnalyticsService employeeAnalyticsService;
  private final AccessIdentityManagement aimService;

  public EmployeeAnalyticsRestApi(LmsRepositoryRegistry lmsRepositoryRegistry, LmsServiceRegistry lmsServiceRegistry,
      EmployeeAnalyticsService employeeAnalyticsService, AccessIdentityManagement aimService)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
    this.employeeAnalyticsService = employeeAnalyticsService;
    this.aimService = aimService;
  }

  @ApiOperation("Fetches employee scores in relation to other employees in the same department")
  @GetMapping("/employee-score")
  public ResponseEntity<RestResult> readEmployeeScore()
  {
    GetEmployeePromoPoints getEmployeePromoPoints = new GetEmployeePromoPoints(aimService, employeeAnalyticsService, lmsServiceRegistry);
    EmployeePromoPointsDto dto = getEmployeePromoPoints.execute(null);
    return RestResponse.success(dto);
  }

  @ApiOperation("Fetches employee analytic data")
  @GetMapping
  public ResponseEntity<RestResult> readEmployeeAnalyticData()
  {
    GetEmployeeAnalyticData getEmployeeAnalyticData = new GetEmployeeAnalyticData(employeeAnalyticsService);

    try
    {
      LmsUser currentUser = Objects.requireNonNull(lmsServiceRegistry.getLmsUserService().getCurrentUser(), "Current user cannot be null!");
      List<EmployeeAnalyticsDto> dto = getEmployeeAnalyticData.execute(currentUser.getId().getId());
      return RestResponse.success(dto);
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Fetches user activity data")
  @GetMapping("/user-activities")
  public ResponseEntity<RestResult> readUserActivityData(@RequestParam String departmentId)
  {
    GetUserActivityData getUserActivityData = new GetUserActivityData(employeeAnalyticsService, aimService);
    UserActivityDataDto dto = getUserActivityData.execute(departmentId);
    return RestResponse.success(dto);
  }
}

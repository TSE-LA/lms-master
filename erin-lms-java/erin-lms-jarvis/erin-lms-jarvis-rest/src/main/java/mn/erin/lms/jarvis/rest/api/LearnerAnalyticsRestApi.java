package mn.erin.lms.jarvis.rest.api;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.inject.Inject;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import mn.erin.infrastucture.rest.common.response.RestResponse;
import mn.erin.infrastucture.rest.common.response.RestResult;
import mn.erin.lms.base.aim.AccessIdentityManagement;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.rest.api.BaseLmsRestApi;
import mn.erin.lms.jarvis.domain.report.service.EmployeeAnalyticsService;
import mn.erin.lms.jarvis.domain.report.usecase.GetAllUsersByGroup;
import mn.erin.lms.jarvis.domain.report.usecase.GetLearnerActivityData;
import mn.erin.lms.jarvis.domain.service.JarvisLmsServiceRegistry;

@Api("Learner's analytics")
@RequestMapping(value = "/lms/users", name = "Provides LMS learner's analytic's features")
@RestController
public class LearnerAnalyticsRestApi extends BaseLmsRestApi
{
  @Inject
  private AccessIdentityManagement accessIdentityManagement;
  private final EmployeeAnalyticsService employeeAnalyticsService;

  public LearnerAnalyticsRestApi(
      EmployeeAnalyticsService employeeAnalyticsService,
      LmsRepositoryRegistry lmsRepositoryRegistry,
      JarvisLmsServiceRegistry jarvisLmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, jarvisLmsServiceRegistry);
    this.employeeAnalyticsService = Objects.requireNonNull(employeeAnalyticsService, "EmployeeAnalytics service cannot be null!");
  }

  @ApiOperation("Gets activity data of learners")
  @PostMapping("/learner-activities")
  public ResponseEntity<RestResult> getOnlineCourseLearnerAnalytics(@RequestBody List<String> body)
  {
    GetLearnerActivityData getLearnerActivityData = new GetLearnerActivityData(employeeAnalyticsService, lmsRepositoryRegistry);
    return RestResponse.success(getLearnerActivityData.execute(body));
  }

  @ApiOperation("Gets all parent and sub group users")
  @GetMapping("/group-users")
  public ResponseEntity<RestResult> getAllGroupUsers(@RequestParam String groupId)
  {
    GetAllUsersByGroup getAllUsersByGroup = new GetAllUsersByGroup(accessIdentityManagement, lmsServiceRegistry);
    return RestResponse.success(getAllUsersByGroup.execute(groupId));
  }

  @ApiOperation("Gets users of chosen group only")
  @GetMapping("/chosen-group-users")
  public ResponseEntity<RestResult> getChosenGroupUsers(@RequestParam String groupId)
  {
    Set<String> learners = accessIdentityManagement.getLearners(groupId);
    return RestResponse.success(learners);
  }
}

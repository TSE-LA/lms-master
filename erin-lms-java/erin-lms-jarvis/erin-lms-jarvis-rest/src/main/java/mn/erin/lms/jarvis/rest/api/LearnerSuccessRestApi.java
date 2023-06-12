package mn.erin.lms.jarvis.rest.api;

import io.swagger.annotations.Api;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import mn.erin.domain.aim.repository.GroupRepository;
import mn.erin.domain.aim.repository.MembershipRepository;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.infrastucture.rest.common.response.RestResponse;
import mn.erin.infrastucture.rest.common.response.RestResult;
import mn.erin.lms.base.aim.user.LmsUser;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.rest.api.BaseLmsRestApi;
import mn.erin.lms.jarvis.domain.report.repository.LearnerSuccessRepository;
import mn.erin.lms.jarvis.domain.report.usecase.GetLearnerSuccess;
import mn.erin.lms.jarvis.domain.report.usecase.dto.DateType;
import mn.erin.lms.jarvis.domain.report.usecase.dto.GetLearnerSuccessInput;
import mn.erin.lms.jarvis.domain.report.usecase.dto.GroupType;

/**
 * @author Galsan Bayart
 */

@Api("Learner's activity REST API")
@RequestMapping(value = "/lms/learner-success", name = "Provides LMS learner's own success")
@RestController
public class LearnerSuccessRestApi extends BaseLmsRestApi
{
  private final LearnerSuccessRepository learnerSuccessRepository;
  private final MembershipRepository membershipRepository;
  private final GroupRepository groupRepository;

  public LearnerSuccessRestApi(LmsRepositoryRegistry lmsRepositoryRegistry, LmsServiceRegistry lmsServiceRegistry,
      LearnerSuccessRepository learnerSuccessRepository, MembershipRepository membershipRepository,
      GroupRepository groupRepository)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
    this.learnerSuccessRepository = learnerSuccessRepository;
    this.membershipRepository = membershipRepository;
    this.groupRepository = groupRepository;
  }

  @GetMapping("/online")
  public ResponseEntity<RestResult> getLearnerSuccess(@RequestParam String year, @RequestParam String dateType, @RequestParam String groupType)
  {
    LmsUser currentUser = lmsServiceRegistry.getLmsUserService().getCurrentUser();
    if (currentUser == null || currentUser.getId() == null)
    {
      return RestResponse.unauthorized("No user logged in.");
    }

    GetLearnerSuccess getLearnerSuccess = new GetLearnerSuccess(learnerSuccessRepository, lmsServiceRegistry, membershipRepository,
        groupRepository);
    GetLearnerSuccessInput learnerSuccessInput = new GetLearnerSuccessInput(currentUser.getId().getId(), Integer.parseInt(year),
        DateType.valueOf(dateType), GroupType.valueOf(groupType));

    try
    {
      return RestResponse.success(getLearnerSuccess.execute(learnerSuccessInput));
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }
}

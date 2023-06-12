package mn.erin.lms.unitel.rest.api;

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
import mn.erin.lms.unitel.domain.repository.LearnerSuccessRepository;
import mn.erin.lms.unitel.domain.usecase.GetLearnerSuccess;
import mn.erin.lms.unitel.domain.usecase.dto.DateType;
import mn.erin.lms.unitel.domain.usecase.dto.GroupType;
import mn.erin.lms.unitel.domain.usecase.dto.LearnerSuccessInput;

/**
 * @author Byambajav
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
    return getCourseLearnerSuccess(year, dateType, groupType, "online-course");
  }

  @GetMapping("/promotion")
  public ResponseEntity<RestResult> getPromotionLearnerSuccess(@RequestParam String year, @RequestParam String dateType, @RequestParam String groupType)
  {
    return getCourseLearnerSuccess(year, dateType, groupType, "promotion");
  }

  private ResponseEntity<RestResult> getCourseLearnerSuccess(String year,
      String dateType, String groupType, String courseType)
  {
    LmsUser currentUser = lmsServiceRegistry.getLmsUserService().getCurrentUser();
    if (currentUser == null || currentUser.getId() == null)
    {
      return RestResponse.unauthorized("No user logged in.");
    }

    GetLearnerSuccess getLearnerSuccess = new GetLearnerSuccess(learnerSuccessRepository, lmsServiceRegistry, membershipRepository,
        groupRepository);
    LearnerSuccessInput learnerSuccessInput = new LearnerSuccessInput(currentUser.getId().getId(), courseType, Integer.parseInt(year),
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


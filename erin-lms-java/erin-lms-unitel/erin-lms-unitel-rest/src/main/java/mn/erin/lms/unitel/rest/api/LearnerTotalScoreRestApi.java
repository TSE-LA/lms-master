package mn.erin.lms.unitel.rest.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.infrastucture.rest.common.response.RestResponse;
import mn.erin.infrastucture.rest.common.response.RestResult;
import mn.erin.lms.base.domain.repository.ClassroomCourseAttendanceRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.rest.api.BaseLmsRestApi;
import mn.erin.lms.unitel.domain.service.UnitelLmsServiceRegistry;
import mn.erin.lms.unitel.domain.usecase.GetClassroomCourseTotalScore;
import mn.erin.lms.unitel.domain.usecase.GetOnlineCourseTotalScore;

/**
 * @author Munkh
 */
@Api("Learner's total score REST API")
@RequestMapping(value = "/lms/users", name = "Provides LMS learner's enrollment features")
@RestController
public class LearnerTotalScoreRestApi extends BaseLmsRestApi
{
  private final ClassroomCourseAttendanceRepository classroomCourseAttendanceRepository;

  LearnerTotalScoreRestApi(LmsRepositoryRegistry lmsRepositoryRegistry,
      LmsServiceRegistry lmsServiceRegistry, ClassroomCourseAttendanceRepository classroomCourseAttendanceRepository)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
    this.classroomCourseAttendanceRepository = classroomCourseAttendanceRepository;
  }

  @ApiOperation("Gets learner total score")
  @GetMapping("{learnerId:.+}/total-score/online-course")
  public ResponseEntity<RestResult> getOnlineCourseTotalScore(@PathVariable String learnerId)
  {
    GetOnlineCourseTotalScore getOnlineCourseTotalScore = new GetOnlineCourseTotalScore(lmsRepositoryRegistry, (UnitelLmsServiceRegistry) lmsServiceRegistry);
    try
    {
      return RestResponse.success(getOnlineCourseTotalScore.execute(learnerId));
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Gets learner total score")
  @GetMapping("{learnerId:.+}/total-score/classroom-course")
  public ResponseEntity<RestResult> getClassroomCourseTotalScore(@PathVariable String learnerId)
  {
    GetClassroomCourseTotalScore getClassroomCourseTotalScore = new GetClassroomCourseTotalScore(classroomCourseAttendanceRepository);
    try
    {
      return RestResponse.success(getClassroomCourseTotalScore.execute(learnerId));
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }
}

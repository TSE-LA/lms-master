package mn.erin.lms.base.rest.api;

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
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.course.GetLearnerCourseHistory;
import mn.erin.lms.base.domain.usecase.course.GetLearnerCourseHistoryByDate;
import mn.erin.lms.base.domain.usecase.course.GetLearnerCredit;
import mn.erin.lms.base.domain.usecase.course.dto.CourseHistoryInput;

/**
 * @author Temuulen Naranbold
 */
@Api("Course history REST API")
@RequestMapping(value = "/lms/course-history", name = "Provides base LMS course history")
@RestController
public class CourseHistoryRestApi extends BaseLmsRestApi
{
  protected CourseHistoryRestApi(LmsRepositoryRegistry lmsRepositoryRegistry,
      LmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
  }

  @ApiOperation("Get learner course history")
  @GetMapping("/get-learner-history")
  public ResponseEntity<RestResult> getLearnerCourseHistory(@RequestParam (required = false) String username)
  {
    try
    {
      GetLearnerCourseHistory getLearnerCourseHistory = new GetLearnerCourseHistory(lmsRepositoryRegistry, lmsServiceRegistry);
      return RestResponse.success(getLearnerCourseHistory.execute(username));
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Get learner course history by year")
  @GetMapping("/get-learner-history-by-year")
  public ResponseEntity<RestResult> getLearnerCourseHistoryByYear(@RequestParam String year, @RequestParam (required = false) String username)
  {
    try
    {
      GetLearnerCourseHistoryByDate getLearnerCourseHistoryByDate = new GetLearnerCourseHistoryByDate(lmsRepositoryRegistry, lmsServiceRegistry);
      CourseHistoryInput input = new CourseHistoryInput();
      input.setYear(year);
      input.setUsername(username);
      return RestResponse.success(getLearnerCourseHistoryByDate.execute(input));
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Get learner credit")
  @GetMapping("/get-learner-credit-by-year")
  public ResponseEntity<RestResult> getLearnerCredit(@RequestParam String year, @RequestParam (required = false) String username)
  {
    try
    {
      GetLearnerCredit getLearnerCredit = new GetLearnerCredit(lmsRepositoryRegistry, lmsServiceRegistry);
      CourseHistoryInput input = new CourseHistoryInput();
      input.setYear(year);
      input.setUsername(username);
      return RestResponse.success(getLearnerCredit.execute(input));
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }
}

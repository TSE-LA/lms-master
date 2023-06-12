package mn.erin.lms.base.rest.api;

import java.util.Map;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.infrastucture.rest.common.response.RestResponse;
import mn.erin.infrastucture.rest.common.response.RestResult;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.tracking.SaveLearnerProgress;
import mn.erin.lms.base.domain.usecase.tracking.dto.SaveLearnerProgressInput;
import mn.erin.lms.base.rest.model.RestLearnerProgress;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Api("Course progress tracking REST API")
@RequestMapping(value = "/lms/courses", name = "Provides LMS course tracking features")
@RestController
public class CourseTrackingRestApi extends BaseLmsRestApi
{
  public CourseTrackingRestApi(LmsRepositoryRegistry lmsRepositoryRegistry,
      LmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
  }

  @ApiOperation("Saves a learner's progress of a course")
  @PutMapping("/{courseId}/progress")
  public ResponseEntity<RestResult> save(@PathVariable String courseId,
     @RequestBody RestLearnerProgress body)
  {
    SaveLearnerProgress saveLearnerProgress = new SaveLearnerProgress(lmsRepositoryRegistry, lmsServiceRegistry);
    SaveLearnerProgressInput input = new SaveLearnerProgressInput(courseId, body.getModuleName(), body.getData());

    try
    {
      Map<String, Object> output = saveLearnerProgress.execute(input);
      return RestResponse.success(output);
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }
}

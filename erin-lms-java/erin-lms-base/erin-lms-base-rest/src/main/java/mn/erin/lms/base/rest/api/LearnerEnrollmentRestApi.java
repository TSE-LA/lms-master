package mn.erin.lms.base.rest.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.infrastucture.rest.common.response.RestResponse;
import mn.erin.infrastucture.rest.common.response.RestResult;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.enrollment.DeleteLearnerEnrollments;
import mn.erin.lms.base.domain.usecase.enrollment.dto.DeleteLearnerEnrollmentsInput;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Api("Learner's enrollmenet REST API")
@RequestMapping(value = "/lms/users", name = "Provides LMS learner's enrollment features")
@RestController
public class LearnerEnrollmentRestApi extends BaseLmsRestApi
{
  LearnerEnrollmentRestApi(LmsRepositoryRegistry lmsRepositoryRegistry,
      LmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
  }

  @ApiOperation("Deletes learner enrollments")
  @DeleteMapping("/{learnerId:.+}/enrollment")
  public ResponseEntity<RestResult> delete(@PathVariable String learnerId,
      @RequestParam(required = false) String enrollmentState)
  {
    DeleteLearnerEnrollments deleteLearnerEnrollments = new DeleteLearnerEnrollments(lmsRepositoryRegistry.getCourseEnrollmentRepository());
    DeleteLearnerEnrollmentsInput input = new DeleteLearnerEnrollmentsInput(learnerId);
    input.setEnrollmentState(enrollmentState);

    try
    {
      boolean isDeleted = deleteLearnerEnrollments.execute(input);
      return RestResponse.success(isDeleted);
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }
}

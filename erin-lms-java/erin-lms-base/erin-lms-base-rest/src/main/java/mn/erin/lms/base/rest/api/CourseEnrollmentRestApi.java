package mn.erin.lms.base.rest.api;

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
import mn.erin.lms.base.domain.usecase.enrollment.UpdateCourseEnrollments;
import mn.erin.lms.base.domain.usecase.enrollment.dto.UpdateCourseEnrollmentsInput;
import mn.erin.lms.base.rest.model.RestCourseEnrollment;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Api("Course enrollment REST API")
@RequestMapping(value = "/lms/courses", name = "Provides LMS course enrollment features")
@RestController
public class CourseEnrollmentRestApi extends BaseLmsRestApi
{
  public CourseEnrollmentRestApi(LmsRepositoryRegistry lmsRepositoryRegistry,
      LmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
  }

  @ApiOperation("Updates course enrollment")
  @PutMapping("/{courseId}/enrollment")
  public ResponseEntity<RestResult> update(@PathVariable String courseId,
      @RequestBody RestCourseEnrollment body)
  {
    UpdateCourseEnrollments updateCourseEnrollments = new UpdateCourseEnrollments(lmsRepositoryRegistry, lmsServiceRegistry);
    UpdateCourseEnrollmentsInput input = new UpdateCourseEnrollmentsInput(courseId, body.getDepartments(), body.getLearners());
    input.setSendNotification(body.isSendNotification());
    input.setAutoChildDepartmentEnroll(body.hasAutoChildDepartmentEnroll());

    try
    {
      updateCourseEnrollments.execute(input);
      return RestResponse.success();
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Updates course enrollment and update course type")
  @PutMapping("/{courseId}/enrollment-coursetype")
  public ResponseEntity<RestResult> updateEnrollmentAndCourseType(@PathVariable String courseId,
      @RequestBody RestCourseEnrollment body)
  {
    UpdateCourseEnrollments updateCourseEnrollments = new UpdateCourseEnrollments(lmsRepositoryRegistry, lmsServiceRegistry);
    UpdateCourseEnrollmentsInput input = new UpdateCourseEnrollmentsInput(courseId, body.getDepartments(), body.getLearners(), body.getCourseType());
    input.setSendNotification(body.isSendNotification());
    input.setAutoChildDepartmentEnroll(body.hasAutoChildDepartmentEnroll());

    try
    {
      updateCourseEnrollments.updateEnrollmentAndCourseType(input);
      return RestResponse.success();
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }
}

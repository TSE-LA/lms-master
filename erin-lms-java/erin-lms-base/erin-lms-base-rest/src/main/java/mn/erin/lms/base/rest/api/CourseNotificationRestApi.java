package mn.erin.lms.base.rest.api;

import java.util.List;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mn.erin.infrastucture.rest.common.response.RestResponse;
import mn.erin.infrastucture.rest.common.response.RestResult;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.course.GetCourseNotifications;
import mn.erin.lms.base.domain.usecase.course.dto.CourseNotificationDto;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Api("Course Notification REST API")
@RequestMapping(value = "/lms/courses", name = "Provides course notification features")
@RestController
public class CourseNotificationRestApi extends BaseLmsRestApi
{
  public CourseNotificationRestApi(LmsRepositoryRegistry lmsRepositoryRegistry,
      LmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
  }

  @ApiOperation("Get course notifications")
  @GetMapping("/course-notifications")
  public ResponseEntity<RestResult> readAll()
  {
    GetCourseNotifications getCourseNotifications = new GetCourseNotifications(lmsRepositoryRegistry, lmsServiceRegistry);
    List<CourseNotificationDto> result = getCourseNotifications.execute(null);
    return RestResponse.success(result);
  }
}

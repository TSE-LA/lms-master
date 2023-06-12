package mn.erin.lms.base.rest.api;

import java.util.List;
import java.util.Map;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.infrastucture.rest.common.response.RestEntity;
import mn.erin.infrastucture.rest.common.response.RestResponse;
import mn.erin.infrastucture.rest.common.response.RestResult;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.service.UseCaseResolver;
import mn.erin.lms.base.domain.usecase.UseCaseDelegator;
import mn.erin.lms.base.domain.usecase.course.CloneCourse;
import mn.erin.lms.base.domain.usecase.course.CreateCourse;
import mn.erin.lms.base.domain.usecase.course.DeleteCourse;
import mn.erin.lms.base.domain.usecase.course.DeleteCourseRelations;
import mn.erin.lms.base.domain.usecase.course.GetClassroomCourses;
import mn.erin.lms.base.domain.usecase.course.GetCoursesForCalendar;
import mn.erin.lms.base.domain.usecase.course.GetCourseById;
import mn.erin.lms.base.domain.usecase.course.GetCourses;
import mn.erin.lms.base.domain.usecase.course.GetCoursesWithSurvey;
import mn.erin.lms.base.domain.usecase.course.GetSelectedDepartmentTree;
import mn.erin.lms.base.domain.usecase.course.GetSuggestedCourses;
import mn.erin.lms.base.domain.usecase.course.HideCourse;
import mn.erin.lms.base.domain.usecase.course.LaunchCourse;
import mn.erin.lms.base.domain.usecase.course.PublishCourse;
import mn.erin.lms.base.domain.usecase.course.SearchCourse;
import mn.erin.lms.base.domain.usecase.course.UpdateAndSendNotification;
import mn.erin.lms.base.domain.usecase.course.UpdateCourse;
import mn.erin.lms.base.domain.usecase.course.dto.CloneCourseInput;
import mn.erin.lms.base.domain.usecase.course.dto.CourseCalendarDto;
import mn.erin.lms.base.domain.usecase.course.dto.CourseCalendarInput;
import mn.erin.lms.base.domain.usecase.course.dto.CourseDto;
import mn.erin.lms.base.domain.usecase.course.dto.CreateCourseInput;
import mn.erin.lms.base.domain.usecase.course.dto.CreateCourseOutput;
import mn.erin.lms.base.domain.usecase.course.dto.GetCoursesInput;
import mn.erin.lms.base.domain.usecase.course.dto.LaunchResult;
import mn.erin.lms.base.domain.usecase.course.dto.NotificationInput;
import mn.erin.lms.base.domain.usecase.course.dto.PublishCourseInput;
import mn.erin.lms.base.domain.usecase.course.dto.UpdateCourseInput;
import mn.erin.lms.base.rest.model.RestCourse;
import mn.erin.lms.base.rest.model.RestPublishOption;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Api("Course REST API")
@RequestMapping(value = "/lms/courses", name = "Provides base LMS course features")
@RestController
public class CourseRestApi extends BaseLmsRestApi
{
  private static final Logger LOGGER = LoggerFactory.getLogger(CourseRestApi.class);

  @SuppressWarnings("unchecked")
  CourseRestApi(LmsRepositoryRegistry lmsRepositoryRegistry, LmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
  }

  @ApiOperation("Creates a new a course")
  @PostMapping
  public ResponseEntity<RestResult> create(@RequestBody RestCourse body)
  {
    CreateCourse createCourse = new CreateCourse(lmsRepositoryRegistry, lmsServiceRegistry);
    CreateCourseInput input = new CreateCourseInput(body.getTitle(), body.getCategoryId(), body.getProperties());
    input.setDescription(body.getDescription());
    input.setCourseType(body.getType());
    input.setAssessmentId(body.getAssessmentId());
    input.setCertificateId(body.getCertificateId());
    input.setCredit(body.getCredit());

    try
    {
      CreateCourseOutput output = createCourse.execute(input);
      return RestResponse.success(output);
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Gets selected departments")
  @GetMapping("/group-enrollment/{courseId}")
  public ResponseEntity<RestResult> getGroupEnrollment(@PathVariable String courseId)
  {
    try
    {
      GetSelectedDepartmentTree getSelectedDepartmentTree = new GetSelectedDepartmentTree(lmsRepositoryRegistry, lmsServiceRegistry);
      return RestResponse.success(getSelectedDepartmentTree.execute(courseId));
    }
    catch (UseCaseException e)
    {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(RestEntity.of(e.getMessage()));
    }
  }

  @ApiOperation("Lists courses by query param values")
  @GetMapping
  @SuppressWarnings("unchecked")
  public ResponseEntity<RestResult> readAll(
      @RequestParam String categoryId,
      @RequestParam(required = false) String courseType,
      @RequestParam(required = false) String publishStatus,
      @RequestParam(required = false) String publishState,
      @RequestParam(required = false) String userRole
  )
  {
    GetCoursesInput input = new GetCoursesInput(categoryId);
    input.setPublishStatus(publishStatus);
    input.setPublishState(publishState);
    input.setCourseType(courseType);
    input.setRole(userRole);

    UseCaseResolver resolver = lmsServiceRegistry.getUseCaseResolver();
    GetCourses getCourses = new GetCourses(lmsRepositoryRegistry, lmsServiceRegistry,
        (UseCaseDelegator<GetCoursesInput, List<CourseDto>>) resolver.getUseCaseDelegator(GetCourses.class.getName()));

    try
    {
      List<CourseDto> result = getCourses.execute(input);
      return RestResponse.success(result);
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Lists suggested courses by query param values")
  @GetMapping("/suggestions")
  @SuppressWarnings("unchecked")
  public ResponseEntity<RestResult> readAllSuggestedCourses(
      @RequestParam String categoryId,
      @RequestParam String courseCount,
      @RequestParam(required = false) String courseType,
      @RequestParam(required = false) String publishStatus,
      @RequestParam(required = false) String userRole
  )
  {
    GetCoursesInput input = new GetCoursesInput(categoryId);
    input.setPublishStatus(publishStatus);
    input.setCourseType(courseType);
    input.setRole(userRole);

    int courseCountLimit = Integer.parseInt(courseCount);
    input.setCourseCount(courseCountLimit);
    UseCaseResolver resolver = lmsServiceRegistry.getUseCaseResolver();
    GetSuggestedCourses getSuggestedCourses = new GetSuggestedCourses(lmsRepositoryRegistry, lmsServiceRegistry,
        (UseCaseDelegator<GetCoursesInput, List<CourseDto>>) resolver.getUseCaseDelegator(GetSuggestedCourses.class.getName()));

    try
    {
      List<CourseDto> suggestedCourses = getSuggestedCourses.execute(input);
      return RestResponse.success(suggestedCourses);
    }
    catch (UseCaseException e)
    {
      LOGGER.error(e.getMessage(), e);
      return RestResponse.internalError("failed to get suggested courses");
    }
  }

  @ApiOperation("Fetches a course by ID")
  @GetMapping("/{courseId}")
  public ResponseEntity<RestResult> readById(@PathVariable String courseId)
  {
    GetCourseById getCourseById = new GetCourseById(lmsRepositoryRegistry, lmsServiceRegistry);

    try
    {
      CourseDto course = getCourseById.execute(courseId);
      return RestResponse.success(course);
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Updates a course")
  @PutMapping("/{courseId}")
  public ResponseEntity<RestResult> update(@PathVariable String courseId,
      @RequestBody RestCourse body)
  {
    UpdateCourseInput input = new UpdateCourseInput(courseId, body.getTitle(), body.getCategoryId(), body.getProperties());
    input.setDescription(body.getDescription());
    input.setType(body.getType());
    input.setHasFeedback(body.isHasFeedBack());
    input.setHasQuiz(body.isHasQuiz());
    input.setHasAssessment(body.isHasAssessment());
    input.setAssessmentId(body.getAssessmentId());
    input.setCertificateId(body.getCertificateId());
    input.setHasCertificate(body.isHasCertificate());
    input.setCredit(body.getCredit());

    UpdateCourse updateCourse = new UpdateCourse(lmsRepositoryRegistry, lmsServiceRegistry);

    try
    {
      CourseDto updatedCourse = updateCourse.execute(input);

      return RestResponse.success(updatedCourse);
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Updates a course and sends notifications")
  @PutMapping("/{courseId}/update-and-notify")
  public ResponseEntity<RestResult> updateAndNotify(@PathVariable String courseId,
      @RequestBody RestCourse body)
  {
    UpdateCourseInput input = new UpdateCourseInput(courseId, body.getTitle(), body.getCategoryId(), body.getProperties());
    input.setDescription(body.getDescription());
    input.setType(body.getType());
    input.setEmailSubject(body.getEmailSubject());
    input.setTemplateName(body.getTemplateName());
    input.setAssessmentId(body.getAssessmentId());
    input.setCertificateId(body.getCertificateId());
    input.setHasAssessment(body.isHasAssessment());
    input.setHasCertificate(body.isHasCertificate());
    input.setWithThumbnail(body.isWithThumbnail());
    input.setHasQuiz(body.isHasQuiz());
    input.setCredit(body.getCredit());

    UpdateAndSendNotification updateAndSendNotification = new UpdateAndSendNotification(lmsRepositoryRegistry, lmsServiceRegistry);
    try
    {
      CourseDto courseDto = updateAndSendNotification.execute(input);
      return RestResponse.success(courseDto);
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Publishes a course")
  @PostMapping("/{courseId}/publish")
  public ResponseEntity<RestResult> publish(@PathVariable String courseId,
      @RequestBody RestPublishOption body)
  {
    PublishCourseInput input = new PublishCourseInput(courseId, body.getAssignedDepartments(), body.getAssignedLearners());
    input.setNotificationInput(new NotificationInput(body.isSendEmail(), body.isSendSms(), body.getNote()));
    input.setAutoChildDepartmentEnroll(body.isAutoChildDepartmentEnroll());
    if (body.getPublishDate() != null)
    {
      input.setPublishDate(body.getPublishDate());
    }

    PublishCourse publishCourse = new PublishCourse(lmsRepositoryRegistry, lmsServiceRegistry);
    try
    {
      CourseDto courseDto = publishCourse.execute(input);
      return RestResponse.success(courseDto);
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Launches a course")
  @GetMapping("/{courseId}/launch")
  public ResponseEntity<RestResult> launch(@PathVariable String courseId)
  {
    LaunchCourse<LaunchResult> launchCourse = new LaunchCourse<>(lmsRepositoryRegistry, lmsServiceRegistry);

    try
    {
      return RestResponse.success(launchCourse.execute(courseId));
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Searches from all courses by key-value pair")
  @GetMapping("/search")
  @SuppressWarnings("unchecked")
  public ResponseEntity<RestResult> search(@RequestParam Map<String, Object> queryParameters)
  {
    UseCaseResolver resolver = lmsServiceRegistry.getUseCaseResolver();
    SearchCourse searchCourse = new SearchCourse(lmsRepositoryRegistry, lmsServiceRegistry,
        (UseCaseDelegator<Map<String, Object>, List<CourseDto>>) resolver.getUseCaseDelegator(SearchCourse.class.getName()));

    try
    {
      List<CourseDto> courses = searchCourse.execute(queryParameters);
      return RestResponse.success(courses);
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Clones a course")
  @PostMapping("/{courseId}/clone")
  public ResponseEntity<RestResult> clone(@PathVariable String courseId,
      @RequestBody RestCourse body)
  {
    CreateCourseInput createCourseInput = new CreateCourseInput(body.getTitle(), body.getCategoryId(), body.getProperties());
    createCourseInput.setCredit(body.getCredit());
    CloneCourseInput input = new CloneCourseInput(courseId, createCourseInput);

    CloneCourse cloneCourse = new CloneCourse(lmsRepositoryRegistry, lmsServiceRegistry);

    try
    {
      CourseDto clonedCourse = cloneCourse.execute(input);
      return RestResponse.success(clonedCourse);
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Hides a course")
  @PatchMapping("/{courseId}/hide")
  public ResponseEntity<RestResult> hide(@PathVariable String courseId)
  {
    HideCourse hideCourse = new HideCourse(lmsRepositoryRegistry, lmsServiceRegistry);
    try
    {
      Boolean isHidden = hideCourse.execute(courseId);
      return RestResponse.success(isHidden);
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError("Could not hide course:" + e.getMessage());
    }
  }

  @ApiOperation("Deletes a course")
  @DeleteMapping("/{courseId}")
  public ResponseEntity<RestResult> delete(@PathVariable String courseId)
  {
    DeleteCourse deleteCourse = new DeleteCourse(lmsRepositoryRegistry, lmsServiceRegistry);

    try
    {
      boolean isDeleted = deleteCourse.execute(courseId);
      return RestResponse.success(isDeleted);
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Fetches courses with surveys")
  @GetMapping("/with-survey/{assessmentId}")
  public ResponseEntity<RestResult> readAll(@PathVariable String assessmentId)
  {
    GetCoursesWithSurvey getCoursesWithSurvey = new GetCoursesWithSurvey(lmsRepositoryRegistry, lmsServiceRegistry);

    try
    {
      List<CourseDto> dtos = getCoursesWithSurvey.execute(assessmentId);
      return RestResponse.success(dtos);
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("deletes course relations")
  @DeleteMapping("/course-relations/{groupId}")
  public ResponseEntity<RestResult> deleteRelations(@PathVariable String groupId)
  {
    DeleteCourseRelations deleteCourseRelations = new DeleteCourseRelations(lmsRepositoryRegistry, lmsServiceRegistry);
    try
    {
      deleteCourseRelations.execute(groupId);
      return RestResponse.success();
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Lists classroom courses")
  @GetMapping("/classroom-course")
  public ResponseEntity<RestResult> readAll()
  {
    GetClassroomCourses getClassroomCourses = new GetClassroomCourses(lmsRepositoryRegistry, lmsServiceRegistry);
    List<CourseDto> result = getClassroomCourses.execute();
    return RestResponse.success(result);
  }

  @ApiOperation("Gets courses for calendar")
  @GetMapping("/courses-for-calendar")
  public ResponseEntity<RestResult> getClassroomCourses(
      @RequestParam String date) throws UseCaseException
  {
    CourseCalendarInput courseCalendarInput = new CourseCalendarInput(date);
    GetCoursesForCalendar getCoursesForCalendar = new GetCoursesForCalendar(lmsRepositoryRegistry, lmsServiceRegistry);
    CourseCalendarDto result = getCoursesForCalendar.execute(courseCalendarInput);
    return RestResponse.success(result);
  }
}

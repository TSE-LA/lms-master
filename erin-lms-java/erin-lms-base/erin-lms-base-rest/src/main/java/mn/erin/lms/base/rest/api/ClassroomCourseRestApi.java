package mn.erin.lms.base.rest.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import mn.erin.domain.aim.repository.AimRepositoryRegistry;
import mn.erin.domain.aim.repository.MembershipRepository;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.infrastucture.rest.common.response.RestResponse;
import mn.erin.infrastucture.rest.common.response.RestResult;
import mn.erin.lms.base.domain.model.classroom_course.Attendance;
import mn.erin.lms.base.domain.model.classroom_course.ClassroomCourseAttendance;
import mn.erin.lms.base.domain.model.classroom_course.LearnerCourseAssessment;
import mn.erin.lms.base.domain.repository.ClassroomCourseAttendanceRepository;
import mn.erin.lms.base.domain.repository.LearnerCourseAssessmentRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.CourseContentCreator;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.assessment.GetLearnerAssessmentStatus;
import mn.erin.lms.base.domain.usecase.assessment.UpdateLearnerAssessmentStatus;
import mn.erin.lms.base.domain.usecase.assessment.dto.LearnerAssessmentStatusInput;
import mn.erin.lms.base.domain.usecase.certificate.GetReceivedCertificates;
import mn.erin.lms.base.domain.usecase.course.ConfirmCourse;
import mn.erin.lms.base.domain.usecase.course.GetCourseById;
import mn.erin.lms.base.domain.usecase.course.UpdateCourseState;
import mn.erin.lms.base.domain.usecase.course.UpdateStateAndSendNotification;
import mn.erin.lms.base.domain.usecase.course.classroom.CloseClassroomCourse;
import mn.erin.lms.base.domain.usecase.course.classroom.DeleteClassroomAllAttendance;
import mn.erin.lms.base.domain.usecase.course.classroom.DeleteClassroomCourseAttendance;
import mn.erin.lms.base.domain.usecase.course.classroom.DownloadClassroomCourseAttendance;
import mn.erin.lms.base.domain.usecase.course.classroom.GetClassroomAttendance;
import mn.erin.lms.base.domain.usecase.course.classroom.GetClassroomCourseAttendance;
import mn.erin.lms.base.domain.usecase.course.classroom.GetClassroomLearnerAttendance;
import mn.erin.lms.base.domain.usecase.course.classroom.SaveClassroomCourseSuggestedUsers;
import mn.erin.lms.base.domain.usecase.course.classroom.UpdateClassroomCourseAttendance;
import mn.erin.lms.base.domain.usecase.course.classroom.dto.AttendanceDto;
import mn.erin.lms.base.domain.usecase.course.classroom.dto.CloseClassroomCourseInput;
import mn.erin.lms.base.domain.usecase.course.classroom.dto.CourseSuggestedUsersDto;
import mn.erin.lms.base.domain.usecase.course.classroom.dto.DownloadClassroomCourseAttendanceInput;
import mn.erin.lms.base.domain.usecase.course.classroom.dto.GetAttendanceInput;
import mn.erin.lms.base.domain.usecase.course.dto.CourseAttendanceInput;
import mn.erin.lms.base.domain.usecase.course.dto.CourseDto;
import mn.erin.lms.base.domain.usecase.course.dto.DeleteClassroomAttendanceInput;
import mn.erin.lms.base.domain.usecase.course.dto.LearnerAttendanceInput;
import mn.erin.lms.base.domain.usecase.course.dto.UpdateCourseInput;
import mn.erin.lms.base.domain.usecase.course.dto.UpdateStateInput;
import mn.erin.lms.base.domain.usecase.notification.SendAssessmentLinkNotification;
import mn.erin.lms.base.domain.usecase.notification.SendUpdateNotification;
import mn.erin.lms.base.domain.usecase.notification.dto.SendNotificationInput;
import mn.erin.lms.base.rest.model.RestAttendance;
import mn.erin.lms.base.rest.model.RestCourse;
import mn.erin.lms.base.rest.model.RestCourseAttendance;
import mn.erin.lms.base.rest.model.RestCourseState;
import mn.erin.lms.base.rest.model.RestDownloadAttendance;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Api("Classroom Course REST API")
@RequestMapping(value = "/lms/classroom-courses")
@RestController
public class ClassroomCourseRestApi extends BaseLmsRestApi
{
  private final ClassroomCourseAttendanceRepository classroomCourseAttendanceRepository;
  private final MembershipRepository membershipRepository;
  private CourseContentCreator courseContentCreator;
  private LearnerCourseAssessmentRepository learnerCourseAssessmentRepository;
  private final AimRepositoryRegistry aimRepositoryRegistry;

  public ClassroomCourseRestApi(LmsRepositoryRegistry lmsRepositoryRegistry, MembershipRepository membershipRepository,
      LmsServiceRegistry lmsServiceRegistry, ClassroomCourseAttendanceRepository classroomCourseAttendanceRepository,
      AimRepositoryRegistry aimRepositoryRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
    this.classroomCourseAttendanceRepository = classroomCourseAttendanceRepository;
    this.membershipRepository = membershipRepository;
    this.aimRepositoryRegistry = aimRepositoryRegistry;
  }

  @Inject
  public void setLearnerCourseAssessmentRepository(LearnerCourseAssessmentRepository learnerCourseAssessmentRepository)
  {
    this.learnerCourseAssessmentRepository = learnerCourseAssessmentRepository;
  }

  @Inject
  public void setCourseContentCreator(CourseContentCreator courseContentCreator)
  {
    this.courseContentCreator = courseContentCreator;
  }

  @ApiOperation("Save a course state and suggested groups")
  @PostMapping("/{courseId}/update-suggested-groups")
  public ResponseEntity<RestResult> saveSuggestedGroupsAndState(@PathVariable String courseId,
      @RequestBody Set<String> users)
  {
    SaveClassroomCourseSuggestedUsers saveClassroomCourseSuggestedUsers = new SaveClassroomCourseSuggestedUsers(lmsRepositoryRegistry, lmsServiceRegistry);
    CourseSuggestedUsersDto input = new CourseSuggestedUsersDto(courseId, users);

    Boolean isSaved = saveClassroomCourseSuggestedUsers.saveGroups(input);
    return RestResponse.success(isSaved);
  }

  @ApiOperation("Update a course state and suggested groups")
  @PostMapping("/{courseId}/update-suggested-groups-confirmation")
  public ResponseEntity<RestResult> updateSuggestedGroupsAndState(@PathVariable String courseId,
      @RequestBody Set<String> users)
  {
    SaveClassroomCourseSuggestedUsers saveClassroomCourseSuggestedUsers = new SaveClassroomCourseSuggestedUsers(lmsRepositoryRegistry, lmsServiceRegistry);
    CourseSuggestedUsersDto input = new CourseSuggestedUsersDto(courseId, users);

    Boolean isSaved = saveClassroomCourseSuggestedUsers.updateGroups(input);
    return RestResponse.success(isSaved);
  }

  @ApiOperation("Save a course state and suggested users")
  @PostMapping("/{courseId}/update-suggested-users")
  public ResponseEntity<RestResult> saveSuggestedUsersAndState(@PathVariable String courseId,
      @RequestBody Set<String> users)
  {
    SaveClassroomCourseSuggestedUsers saveClassroomCourseSuggestedUsers = new SaveClassroomCourseSuggestedUsers(lmsRepositoryRegistry, lmsServiceRegistry);
    CourseSuggestedUsersDto input = new CourseSuggestedUsersDto(courseId, users);
    try
    {
      Boolean isSaved = saveClassroomCourseSuggestedUsers.execute(input);
      return RestResponse.success(isSaved);
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Update a course state and suggested users")
  @PostMapping("/{courseId}/update-suggested-users-confirmation")
  public ResponseEntity<RestResult> updateSuggestedUsersAndState(@PathVariable String courseId,
      @RequestBody Set<String> users)
  {
    SaveClassroomCourseSuggestedUsers saveClassroomCourseSuggestedUsers = new SaveClassroomCourseSuggestedUsers(lmsRepositoryRegistry, lmsServiceRegistry);
    CourseSuggestedUsersDto input = new CourseSuggestedUsersDto(courseId, users);

    Boolean isSaved = saveClassroomCourseSuggestedUsers.updateUsers(input);
    return RestResponse.success(isSaved);
  }

  @ApiOperation("fetch a course state and suggested users")
  @GetMapping("/{courseId}/get-suggested-users")
  public ResponseEntity<RestResult> fetchSuggestedUsersAndState(@PathVariable String courseId)
  {
    SaveClassroomCourseSuggestedUsers saveClassroomCourseSuggestedUsers = new SaveClassroomCourseSuggestedUsers(lmsRepositoryRegistry, lmsServiceRegistry);

    Map<String, Set<String>> result = saveClassroomCourseSuggestedUsers.fetchAll(courseId);
    return RestResponse.success(result);
  }

  @ApiOperation("Updates a course state and sends notifications")
  @PutMapping("/{courseId}/update-state")
  public ResponseEntity<RestResult> updateState(@PathVariable String courseId,
      @RequestBody RestCourseState state)
  {
    UpdateCourseState updateCourseState = new UpdateCourseState(lmsRepositoryRegistry, lmsServiceRegistry, classroomCourseAttendanceRepository);
    try
    {
      CourseDto courseDto = updateCourseState.execute(new UpdateStateInput(courseId, state.getState(), state.isRollback(), state.getReason()));
      return RestResponse.success(courseDto);
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Updates a course and sends notifications")
  @PutMapping("/{courseId}/update-state-and-notify")
  public ResponseEntity<RestResult> updateStateAndNotify(@PathVariable String courseId,
      @RequestBody RestCourse body)
  {
    UpdateCourseInput input = new UpdateCourseInput(courseId, body.getTitle(), body.getCategoryId(), body.getProperties());
    input.setDescription(body.getDescription());
    input.setNote(body.getNote());
    input.setType(body.getType());
    input.setEmailSubject(body.getEmailSubject());
    input.setTemplateName(body.getTemplateName());
    input.setAssessmentId(body.getAssessmentId());
    input.setCertificateId(body.getCertificateId());

    UpdateStateAndSendNotification updateStateAndSendNotification = new UpdateStateAndSendNotification(lmsRepositoryRegistry, lmsServiceRegistry);
    try
    {
      CourseDto courseDto = updateStateAndSendNotification.execute(input);
      return RestResponse.success(courseDto);
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Confirms a course learners and groups")
  @PutMapping("/{courseId}/confirm")
  public ResponseEntity<RestResult> confirmCourse(@PathVariable String courseId)
  {
    ConfirmCourse confirmCourse = new ConfirmCourse(lmsRepositoryRegistry, lmsServiceRegistry, membershipRepository,
        classroomCourseAttendanceRepository, aimRepositoryRegistry);
    try
    {
      CourseDto courseDto = confirmCourse.execute(courseId);
      return RestResponse.success(courseDto);
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Get students attendance of a classroom course")
  @GetMapping("/{courseId}/attendance")
  public ResponseEntity<RestResult> find(@PathVariable String courseId)
  {
    GetClassroomCourseAttendance getClassroomCourseAttendance = new GetClassroomCourseAttendance(lmsRepositoryRegistry, lmsServiceRegistry,
        classroomCourseAttendanceRepository);
    try
    {
      return RestResponse.success(toRestCourseAttendance(getClassroomCourseAttendance.execute(courseId).getAttendances()));
    }
    catch (UseCaseException e)
    {
      return RestResponse.badRequest(e.getMessage());
    }
  }

  @ApiOperation("Download attendance of a classroom course as EXCEL")
  @PostMapping("/{courseId}/attendance/download")
  public ResponseEntity download(@PathVariable String courseId, @RequestBody List<RestDownloadAttendance> body)
  {
    DownloadClassroomCourseAttendance downloadClassroomCourseAttendance = new DownloadClassroomCourseAttendance(lmsRepositoryRegistry, lmsServiceRegistry);

    List<AttendanceDto> attendanceDtos = new ArrayList<>();
    for (RestDownloadAttendance attendance : body)
    {
      AttendanceDto dto = new AttendanceDto();
      dto.setEmployeeName(attendance.getEmployeeName());
      dto.setDepartment(attendance.getDepartment());
      dto.setPresent(attendance.isPresent());
      dto.setSupervisor(attendance.getSupervisor());
      dto.setGrade1(attendance.getGrade1());
      dto.setGrade2(attendance.getGrade2());
      dto.setGrade3(attendance.getGrade3());
      attendanceDtos.add(dto);
    }

    try
    {
      DownloadClassroomCourseAttendanceInput input = new DownloadClassroomCourseAttendanceInput(courseId, attendanceDtos);
      byte[] excelData = downloadClassroomCourseAttendance.execute(input);
      ByteArrayResource resource = new ByteArrayResource(excelData);
      return ResponseEntity.ok()
        .contentLength(resource.contentLength())
        .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
        .header("Content-Disposition", "attachment; filename=\"Course-attendance" + ".xlsx\"")
        .body(resource);
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Sends a classroom cancel notification")
  @GetMapping("/send-update-notification")
  public ResponseEntity<RestResult> sendUpdateNotification(@RequestParam String courseId)
  {
    SendNotificationInput input = new SendNotificationInput(courseId);
    SendUpdateNotification sendUpdateNotification = new SendUpdateNotification(lmsRepositoryRegistry, lmsServiceRegistry);

    try
    {
      return RestResponse.success(sendUpdateNotification.execute(input));
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Sends an assessment link of classroom course")
  @GetMapping("/send-assessment-link")
  public ResponseEntity<RestResult> sendAssessmentLinkNotification(@RequestParam String courseId)
  {
    SendAssessmentLinkNotification sendAssessmentLinkNotification = new SendAssessmentLinkNotification(lmsRepositoryRegistry, lmsServiceRegistry,
        classroomCourseAttendanceRepository);
    try
    {
      return RestResponse.success(sendAssessmentLinkNotification.execute(courseId));
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Update learners attendance of a classroom course")
  @PutMapping("/{courseId}/attendance")
  public ResponseEntity<RestResult> update(@PathVariable String courseId, @RequestBody List<RestAttendance> body)
  {
    UpdateClassroomCourseAttendance updateClassroomCourseAttendance = new UpdateClassroomCourseAttendance(lmsRepositoryRegistry, lmsServiceRegistry,
        aimRepositoryRegistry);
    CourseAttendanceInput input = new CourseAttendanceInput(courseId, body.stream()
        .map(restAttendance -> new LearnerAttendanceInput(restAttendance.getLearnerId(), restAttendance.isPresent(), restAttendance.getGrades(),
            restAttendance.getGroupName(), restAttendance.getSupervisorId(), restAttendance.isInvited()))
        .collect(Collectors.toList()));
    try
    {
      return RestResponse.success(toRestCourseAttendance(updateClassroomCourseAttendance.execute(input).getAttendances()));
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }


  @ApiOperation("Deletes a learner from a classroom attendance")
  @DeleteMapping("/{courseId}/remove-attendance")
  public ResponseEntity<RestResult> delete(@PathVariable String courseId, @RequestParam String learnerId)
  {
    DeleteClassroomCourseAttendance deleteClassroomCourseAttendance = new DeleteClassroomCourseAttendance(lmsRepositoryRegistry, lmsServiceRegistry, classroomCourseAttendanceRepository);
    try
    {
      deleteClassroomCourseAttendance.execute(new DeleteClassroomAttendanceInput(courseId, learnerId));
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
    return RestResponse.success();
  }

  @ApiOperation("Close classroom course")
  @PutMapping("/{courseId}/close")
  public ResponseEntity<RestResult> complete(@PathVariable String courseId, @RequestBody List<RestAttendance> body)
  {
    CloseClassroomCourse closeClassroomCourse = new CloseClassroomCourse(lmsRepositoryRegistry, lmsServiceRegistry, classroomCourseAttendanceRepository, courseContentCreator);
    UpdateClassroomCourseAttendance updateClassroomCourseAttendance = new UpdateClassroomCourseAttendance(lmsRepositoryRegistry, lmsServiceRegistry, aimRepositoryRegistry);

    CloseClassroomCourseInput closeClassroomCourseInput = new CloseClassroomCourseInput(courseId, body.stream()
        .map(restAttendance -> new LearnerAttendanceInput(restAttendance.getLearnerId(), restAttendance.isPresent(), restAttendance.getGrades(),
            restAttendance.getGroupName(), restAttendance.getSupervisorId(), restAttendance.isInvited()))
        .collect(Collectors.toList()));
    CourseAttendanceInput courseAttendanceInput = new CourseAttendanceInput(courseId, body.stream()
        .map(restAttendance -> new LearnerAttendanceInput(restAttendance.getLearnerId(), restAttendance.isPresent(), restAttendance.getGrades(),
            restAttendance.getGroupName(), restAttendance.getSupervisorId(), restAttendance.isInvited()))
        .collect(Collectors.toList()));

    try
    {
      updateClassroomCourseAttendance.execute(courseAttendanceInput);

      closeClassroomCourse.execute(closeClassroomCourseInput);
      return RestResponse.success();
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError("failed to close classroom");
    }
  }

  @ApiOperation("Gets students attendance of a classroom course")
  @GetMapping("/{learnerId:.+}/attendances")
  public ResponseEntity<RestResult> getAllByLearnerId(@PathVariable String learnerId)
  {
    GetClassroomLearnerAttendance getClassroomLearnerAttendance = new GetClassroomLearnerAttendance(lmsRepositoryRegistry, lmsServiceRegistry,
        classroomCourseAttendanceRepository);
    try
    {
      return RestResponse.success(toRestListCourseAttendance(getClassroomLearnerAttendance.execute(learnerId)));
    }
    catch (UseCaseException e)
    {
      return RestResponse.badRequest(e.getMessage());
    }
  }

  @ApiOperation("Get courses by attendance")
  @GetMapping("/{learnerId:.+}/courses")
  public ResponseEntity<RestResult> getAllCourseByLearnerId(@PathVariable String learnerId)
  {
    GetClassroomLearnerAttendance getClassroomLearnerAttendance = new GetClassroomLearnerAttendance(lmsRepositoryRegistry, lmsServiceRegistry,
        classroomCourseAttendanceRepository);
    try
    {
      List<RestCourseAttendance> courseAttendances = toRestListCourseAttendance(getClassroomLearnerAttendance.execute(learnerId));
      GetCourseById getCourseById = new GetCourseById(lmsRepositoryRegistry, lmsServiceRegistry);
      List<CourseDto> enrolledCourses = new ArrayList<>();
      for (RestCourseAttendance courseAttendance : courseAttendances)
      {
        enrolledCourses.add(getCourseById.execute(courseAttendance.getCourseId()));
      }
      return RestResponse.success(enrolledCourses);
    }
    catch (UseCaseException e)
    {
      return RestResponse.badRequest(e.getMessage());
    }
  }

  @ApiOperation("Gets students attendance of a classroom course")
  @GetMapping("/{courseId}/attendance/{learnerId:.+}")
  public ResponseEntity<RestResult> getAttendance(@PathVariable String courseId, @PathVariable String learnerId)
  {
    GetClassroomAttendance getClassroomLearnerAttendance = new GetClassroomAttendance(lmsRepositoryRegistry, lmsServiceRegistry,
        classroomCourseAttendanceRepository);
    try
    {
      RestAttendance restAttendance = toRestAttendance(getClassroomLearnerAttendance.execute(new GetAttendanceInput(courseId, learnerId)));
      return RestResponse.success(restAttendance);
    }
    catch (UseCaseException e)
    {
      return RestResponse.badRequest(e.getMessage());
    }
  }

  @ApiOperation("Deletes students attendance of a classroom course")
  @DeleteMapping("/{courseId}/attendance")
  public ResponseEntity<RestResult> getAttendance(@PathVariable String courseId)
  {
    DeleteClassroomAllAttendance deleteClassroomAllAttendance = new DeleteClassroomAllAttendance(lmsRepositoryRegistry, lmsServiceRegistry,
        classroomCourseAttendanceRepository);
    try
    {
      return RestResponse.success(deleteClassroomAllAttendance.execute(courseId));
    }
    catch (UseCaseException e)
    {
      return RestResponse.badRequest(e.getMessage());
    }
  }

  @ApiOperation("Updates a course survey detail")
  @PostMapping("/{courseId}/survey-status/{learnerId:.+}")
  public ResponseEntity<RestResult> updateAssessmentStatus(@PathVariable String courseId, @PathVariable String learnerId)
  {
    LearnerAssessmentStatusInput input = new LearnerAssessmentStatusInput(courseId, learnerId);
    UpdateLearnerAssessmentStatus updateAssessmentStatus = new UpdateLearnerAssessmentStatus(lmsRepositoryRegistry, lmsServiceRegistry,
        learnerCourseAssessmentRepository);

    updateAssessmentStatus.execute(input);
    return RestResponse.success();
  }

  @ApiOperation("fetch a course survey details")
  @GetMapping("/{courseId}/survey-status/{learnerId:.+}")
  public ResponseEntity<RestResult> fetchAssessmentStatus(@PathVariable String courseId, @PathVariable String learnerId)
  {
    LearnerAssessmentStatusInput input = new LearnerAssessmentStatusInput(courseId, learnerId);
    GetLearnerAssessmentStatus getLearnerAssessmentStatus = new GetLearnerAssessmentStatus(lmsRepositoryRegistry, lmsServiceRegistry,
        learnerCourseAssessmentRepository);

    LearnerCourseAssessment output = null;
    try
    {
      output = getLearnerAssessmentStatus.execute(input);
    }
    catch (UseCaseException e)
    {
      e.printStackTrace();
    }
    return RestResponse.success(output);
  }

  @ApiOperation("Fetches received assessment")
  @GetMapping("/received-assessment/{learnerId:.+}")
  public ResponseEntity<RestResult> getReceivedAssessment(@PathVariable String learnerId)
  {
    GetReceivedCertificates getReceivedCertificates = new GetReceivedCertificates(lmsRepositoryRegistry, lmsServiceRegistry);
    try
    {
      return RestResponse.success(getReceivedCertificates.execute(learnerId));
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  private RestAttendance toRestAttendance(Attendance courseAttendance)
  {
    RestAttendance restAttendance = new RestAttendance(courseAttendance.getLearnerId().getId(), courseAttendance.isPresent(), courseAttendance.getScores());
    if (courseAttendance.getSupervisorId() != null)
    {
      restAttendance.setSupervisorId(courseAttendance.getSupervisorId().getId());
    }
    restAttendance.setInvited(courseAttendance.isInvited());
    restAttendance.setGroupName(courseAttendance.getGroupName());
    return restAttendance;
  }

  private List<RestAttendance> toRestCourseAttendance(List<Attendance> courseAttendances)
  {
    return courseAttendances.stream()
        .map(this::toRestAttendance)
        .collect(Collectors.toList());
  }

  private List<RestCourseAttendance> toRestListCourseAttendance(List<ClassroomCourseAttendance> classroomCourseAttendances)
  {
    return classroomCourseAttendances.stream().map(courseAttendance ->
        new RestCourseAttendance(courseAttendance.getCourseId().getId(), toRestCourseAttendance(courseAttendance.getAttendances())))
        .collect(Collectors.toList());
  }
}

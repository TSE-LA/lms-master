package mn.erin.lms.legacy.infrastructure.unitel.rest.promotion;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import mn.erin.domain.aim.repository.UserRepository;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.infrastucture.rest.common.response.RestResponse;
import mn.erin.infrastucture.rest.common.response.RestResult;
import mn.erin.lms.base.aim.AccessIdentityManagement;
import mn.erin.lms.legacy.domain.lms.model.course.Course;
import mn.erin.lms.legacy.domain.lms.model.course.CourseDetail;
import mn.erin.lms.legacy.domain.lms.model.course.PublishStatus;
import mn.erin.lms.legacy.domain.lms.repository.CourseAuditRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseEnrollmentRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseRepository;
import mn.erin.lms.legacy.domain.lms.repository.LMSRepositoryException;
import mn.erin.lms.legacy.domain.lms.usecase.audit.ChangeEnrollmentToReadership;
import mn.erin.lms.legacy.domain.lms.usecase.audit.FixCourseReadership;
import mn.erin.lms.legacy.domain.lms.usecase.audit.dto.EnrollmentToReaderShipInput;
import mn.erin.lms.legacy.domain.lms.usecase.readership.create_readership.CreateReaderships;
import mn.erin.lms.legacy.domain.lms.usecase.readership.create_readership.CreateReadershipsInput;
import mn.erin.lms.legacy.domain.lms.usecase.readership.create_readership.DeleteReaderships;
import mn.erin.lms.legacy.domain.lms.usecase.readership.create_readership.GetNotEnrolledCourses;
import mn.erin.lms.legacy.domain.lms.usecase.readership.create_readership.GetNotEnrolledCoursesInput;
import mn.erin.lms.legacy.domain.lms.usecase.readership.create_readership.UpdateCourseEnrollments;
import mn.erin.lms.legacy.domain.lms.usecase.readership.create_readership.UpdateCourseEnrollmentsInput;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Api("Readership REST API")
@RequestMapping(value = "/legacy/lms/readerships", name = "Provides promotion readership for LMS")
public class PromotionReadershipRestApi
{
  @Inject
  private AccessIdentityManagement accessIdentityManagement;

  @Inject
  private CourseRepository courseRepository;

  @Inject
  private CourseAuditRepository courseAuditRepository;

  @Inject
  private CourseEnrollmentRepository courseEnrollmentRepository;

  @Inject
  private UserRepository userRepository;

  @ApiOperation("Gets learners promotion readership")
  @GetMapping
  public ResponseEntity<RestResult> getReadership(
      @RequestParam String learnerId,
      @RequestParam String groupId)
  {
    try
    {
      GetNotEnrolledCourses getNotEnrolledCourses = new GetNotEnrolledCourses(accessIdentityManagement, courseRepository, courseEnrollmentRepository);

      List<Course> courses = getNotEnrolledCourses
          .execute(new GetNotEnrolledCoursesInput(learnerId, groupId, null));
      return RestResponse.success(courses);
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Creates promotion readership")
  @PostMapping
  public ResponseEntity<RestResult> create(@RequestBody PromoReadershipRequest request)
  {
    if (!StringUtils.isBlank(request.getLearnerId()))
    {
      try
      {
        GetNotEnrolledCourses getNotEnrolledCourses = new GetNotEnrolledCourses(accessIdentityManagement, courseRepository, courseEnrollmentRepository);

        Set<String> courses = getNotEnrolledCourses
            .execute(new GetNotEnrolledCoursesInput(request.getLearnerId(), request.getGroupId(), request.getNewGroupId())).stream()
            .map(course -> course.getCourseId().getId()).collect(
                Collectors.toSet());

        CreateReaderships createReaderships = new CreateReaderships(courseRepository, courseAuditRepository, userRepository);

        List<String> results = createReaderships.execute(new CreateReadershipsInput(courses, request.getLearnerId()));
        return RestResponse.success(results);
      }
      catch (UseCaseException e)
      {
        return RestResponse.internalError(e.getMessage());
      }
    }
    else
    {
      try
      {
        UpdateCourseEnrollments updateCourseEnrollments = new UpdateCourseEnrollments(courseRepository);
        updateCourseEnrollments.execute(new UpdateCourseEnrollmentsInput(request.getGroupId(), request.getNewGroupId()));
        return RestResponse.success();
      }
      catch (UseCaseException e)
      {
        return RestResponse.internalError(e.getMessage());
      }
    }
  }

  @ApiOperation("Delete promotion readerships")
  @DeleteMapping("/{learnerId:.+}")
  public ResponseEntity<RestResult> delete(@PathVariable String learnerId)
  {
    Validate.notBlank(learnerId);

    try
    {
      DeleteReaderships deleteReaderships = new DeleteReaderships(courseAuditRepository);
      return RestResponse.success(deleteReaderships.execute(learnerId));
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Move promotion readerships")
  @PatchMapping("/move")
  public ResponseEntity<RestResult> move(@RequestBody MovePromoReadershipsRequest request)
  {
    Validate.notNull(request);
    Validate.notBlank(request.getLearnerId());
    Validate.notBlank(request.getGroupId());

    try
    {
      DeleteReaderships deleteReaderships = new DeleteReaderships(courseAuditRepository);
      deleteReaderships.execute(request.getLearnerId());

      GetNotEnrolledCourses getNotEnrolledCourses = new GetNotEnrolledCourses(accessIdentityManagement, courseRepository, courseEnrollmentRepository);
      Set<String> courses = getNotEnrolledCourses.execute(new GetNotEnrolledCoursesInput(request.getLearnerId(), request.getGroupId())).stream()
          .map(course -> course.getCourseId().getId()).collect(
              Collectors.toSet());

      CreateReaderships createReaderships = new CreateReaderships(courseRepository, courseAuditRepository, userRepository);
      List<String> result = createReaderships.execute(new CreateReadershipsInput(courses, request.getLearnerId()));

      return RestResponse.success(result);
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Enrollment to readership")
  @PostMapping("/enrollment-to-readership")
  public ResponseEntity<RestResult> changeEnrollment(@RequestBody RestEnrollmentToReadership body)
  {

    EnrollmentToReaderShipInput input = new EnrollmentToReaderShipInput(body.getStartDate(), body.getEndDate(),
        accessIdentityManagement.getCurrentUserDepartmentId(), body.getState());
    ChangeEnrollmentToReadership useCase = new ChangeEnrollmentToReadership(courseEnrollmentRepository, courseAuditRepository, courseRepository);

    try
    {
      return RestResponse.success(useCase.execute(input));
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Course group name to group id converter")
  @GetMapping("/course-groupName-to-groupId")
  public ResponseEntity<RestResult> changeEnrollment()
  {
    List<Course> allPublishedCourses = courseRepository.getCourseList(PublishStatus.PUBLISHED);
    int fixed = 0;
    List<String> faulty = new ArrayList<>();
    for (Course course : allPublishedCourses)
    {
      Object enrolledGroups = course.getCourseDetail().getProperties().get("enrolledGroups");
      Set<String> currentEnrolledGroups = new HashSet<>();
      if (enrolledGroups != null)
      {
        currentEnrolledGroups.addAll((List<String>) enrolledGroups);
      }
      Set<String> groupNames = course.getUserGroup().getGroups();
      Set<String> finalGroupIdSet = new HashSet<>();
      if (!groupNames.isEmpty())
      {
        groupNames.forEach(name -> {
          String convertedGroupId = accessIdentityManagement.getDepartmentId(name);
          if (convertedGroupId == null)
          {
            faulty.add(name);
          }
          else
          {
            if (!finalGroupIdSet.contains(convertedGroupId))
            {
              Set<String> subGroups = accessIdentityManagement.getSubDepartments(convertedGroupId);
              finalGroupIdSet.add(convertedGroupId);
              finalGroupIdSet.addAll(subGroups);
            }
          }
        });
        CourseDetail courseDetail = course.getCourseDetail();
        courseDetail.addProperty("enrolledGroups", (Serializable) finalGroupIdSet);
        try
        {
          if (!currentEnrolledGroups.containsAll(finalGroupIdSet))
          {
            fixed++;
            courseRepository.update(course.getCourseId(), courseDetail);
          }
        }
        catch (LMSRepositoryException e)
        {
          System.out.println("error updating course enrolledGroups property for course: " + course.getCourseId().getId());
        }
      }
    }
    Map<String, Object> result = new HashMap<>();
    result.put("TOTAL FIXED PROMOS: ", fixed);
    result.put("Renamed groups: ", faulty);
    return RestResponse.success(result);
  }

  @ApiOperation("")
  @DeleteMapping("/fix-readership")
  public ResponseEntity<RestResult> fixReaderShip(
      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate)
  {
    Map<String, Date> dateFilter = new HashMap<>();
    Date startDateFilter = Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    Date endDateFilter = Date.from(endDate.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant());
    dateFilter.put("startDate", startDateFilter);
    dateFilter.put("endDate", endDateFilter);
    FixCourseReadership fixCourseReadership = new FixCourseReadership(courseEnrollmentRepository, courseAuditRepository);
    try
    {
      return RestResponse.success(fixCourseReadership.execute(dateFilter));
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }
}

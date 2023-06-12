package mn.erin.lms.base.domain.usecase.course;

import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.LmsDepartmentService;
import mn.erin.lms.base.aim.organization.DepartmentId;
import mn.erin.lms.base.aim.role.LmsRole;
import mn.erin.lms.base.aim.user.Author;
import mn.erin.lms.base.aim.user.LearnerId;
import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.base.domain.model.course.Course;
import mn.erin.lms.base.domain.model.course.CourseDepartmentRelation;
import mn.erin.lms.base.domain.model.course.CourseDetail;
import mn.erin.lms.base.domain.model.course.CourseId;
import mn.erin.lms.base.domain.model.course.CourseType;
import mn.erin.lms.base.domain.model.course.DateInfo;
import mn.erin.lms.base.domain.model.course.PublishStatus;
import mn.erin.lms.base.domain.model.enrollment.CourseEnrollment;
import mn.erin.lms.base.domain.model.task.LmsScheduledTask;
import mn.erin.lms.base.domain.model.task.TaskType;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.CoursePublisher;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.service.LmsTaskScheduler;
import mn.erin.lms.base.domain.service.TaskCancellationResult;
import mn.erin.lms.base.domain.usecase.assessment.UpdateAssessmentStatus;
import mn.erin.lms.base.domain.usecase.assessment.dto.UpdateAssessmentStatusInput;
import mn.erin.lms.base.domain.usecase.course.dto.CourseDto;
import mn.erin.lms.base.domain.usecase.course.dto.NotificationInput;
import mn.erin.lms.base.domain.usecase.course.dto.PublishCourseInput;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Authorized(users = { Author.class })
public class PublishCourse extends CourseUseCase<PublishCourseInput, CourseDto>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(PublishCourse.class);
  public static final String PUBLISH_COURSE_PREFIX = "publishCourse_";
  public static final String COURSE_ID = "courseId";

  private final LmsTaskScheduler scheduler;
  private final LmsDepartmentService departmentService;

  private  long startTime;
  private long endTime;
  private boolean hasAutoChildDepartmentEnroll;

  public PublishCourse(LmsRepositoryRegistry lmsRepositoryRegistry, LmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
    this.scheduler = lmsServiceRegistry.getLmsScheduler();
    this.departmentService = lmsServiceRegistry.getDepartmentService();
  }

  @Override
  public CourseDto execute(PublishCourseInput input) throws UseCaseException
  {
    hasAutoChildDepartmentEnroll = input.hasAutoChildDepartmentEnroll();
    TaskCancellationResult taskResult = scheduler.cancel(input.getCourseId());
    Date publishDate = input.getPublishDate();
    boolean publishInTheFuture = publishDate != null && publishDate.after(new Date());
    return publishCourse(input, taskResult.isComplete(), publishInTheFuture);
  }

  private CourseDto publishCourse(PublishCourseInput input, boolean isPublished, boolean publishInTheFuture) throws UseCaseException
  {
    String courseId = input.getCourseId();
    if (!isPublished)
    {
      startTime = System.currentTimeMillis();
      publishContent(courseId);
      endTime = System.currentTimeMillis();
      LOGGER.warn("COURSE ############## => Create scorm took [{}] millis", (endTime - startTime));
    }

    Set<DepartmentId> assignedDepartments = input.getAssignedDepartments().stream()
        .map(department -> DepartmentId.valueOf(departmentService.getDepartmentId(department))).collect(Collectors.toSet());
    Set<LearnerId> assignedLearners = input.getAssignedLearners().stream().map(LearnerId::valueOf).collect(Collectors.toSet());
    Set<String> allRelatedDepartmentId = new HashSet<>();
    //    TODO: Remove this logic when version is 3.0.0
    if (hasAutoChildDepartmentEnroll)
    {
      for (DepartmentId departmentId : assignedDepartments)
      {
        allRelatedDepartmentId.add(departmentId.getId());
        Set<String> subDepartments = departmentService.getSubDepartments(departmentId.getId());
        allRelatedDepartmentId.addAll(subDepartments);
      }
       assignedDepartments = allRelatedDepartmentId.stream().map(DepartmentId::valueOf).collect(Collectors.toSet());
    }
    Set<DepartmentId> allAssignedDepartments = assignedDepartments;
    Course course = getCourse(CourseId.valueOf(courseId));
    CourseDetail courseDetail = course.getCourseDetail();

    startTime = System.currentTimeMillis();
    Course updatedCourse;
    updatedCourse = updatePendingCourse(courseId, input.getPublishDate(), assignedLearners, courseDetail, course);
    endTime = System.currentTimeMillis();
    LOGGER.warn("COURSE ############## => Update course state took [{}] millis", (endTime - startTime));

    if (!publishInTheFuture)
    {
      CompletableFuture.supplyAsync(() -> {
        try
        {
          enrollAndNotify(course, input, allAssignedDepartments, assignedLearners, updatedCourse);
        }
        catch (LmsRepositoryException e)
        {
          LOGGER.error(e.getMessage());
          sendPublishFailNotice(course);
        }
        return true;
      });
    }
    else
    {
      LmsScheduledTask task = new LmsScheduledTask(PUBLISH_COURSE_PREFIX + input.getCourseId(), input.getPublishDate(), TaskType.COURSE_PUBLICATION);
      Map<String, Object> properties = new HashMap<>();
      properties.put(COURSE_ID, course.getCourseId().getId());
      task.fillProperties(properties);
      task.setAction(() -> {
        try
        {
          enrollAndNotify(course, input, allAssignedDepartments, assignedLearners, updatedCourse);
        }
        catch (LmsRepositoryException e)
        {
          LOGGER.error(e.getMessage());
          sendPublishFailNotice(course);
        }
      });
      scheduler.schedule(task);
    }

    return toCourseDto(updatedCourse);
  }

  private void sendPublishFailNotice(Course course)
  {
    Map<String, Object> courseData = new HashMap<>();
    courseData.put(COURSE_ID, course.getCourseId().getId());
    courseData.put("courseName", course.getCourseDetail().getTitle());
    lmsServiceRegistry.getNotificationService().notifyPublisher(course.getAuthorId().getId(), courseData, false);
  }

  private void publishContent(String courseId) throws UseCaseException
  {
    CoursePublisher coursePublisher = lmsServiceRegistry.getCoursePublisher();
    boolean isContentPublished = coursePublisher.publishCourseContent(courseId);

    if (!isContentPublished)
    {
      LOGGER.error("Failed to publish the course content: [{}]", courseId);
      throw new UseCaseException("Failed to publish the course content");
    }
  }

  private Course updatePendingCourse(
      String courseId,
      Date publishDate,
      Set<LearnerId> assignedLearners,
      CourseDetail courseDetail,
      Course course) throws UseCaseException
  {
    courseDetail.changePublishStatus(PublishStatus.PENDING);

    DateInfo dateInfo = courseDetail.getDateInfo();
    if(publishDate != null){
      dateInfo.setPublishDate(publishDate.toInstant()
          .atZone(ZoneId.systemDefault())
          .toLocalDateTime());
    }

    CourseDepartmentRelation courseDepartmentRelation = course.getCourseDepartmentRelation();
    courseDepartmentRelation.setAssignedLearners(assignedLearners);

    if (course.getCourseDetail().hasAssessment())
    {
      activateAssessmentStatus(course.getAssessmentId());
    }
    try
    {
      courseRepository.update(course.getCourseId(), course.getCourseCategoryId(), courseDetail, course.getCourseType(), course.getAssessmentId(),
          course.getCertificateId());
      return courseRepository.updateByEnrollment(CourseId.valueOf(courseId), courseDepartmentRelation, PublishStatus.PUBLISHED);
    }
    catch (LmsRepositoryException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }
  }

  private void enrollAndNotify(Course course, PublishCourseInput input, Set<DepartmentId> assignedDepartments, Set<LearnerId> assignedLearners, Course updatedCourse)
      throws LmsRepositoryException
  {
    startTime = System.currentTimeMillis();
    CourseDepartmentRelation courseDepartmentRelation = course.getCourseDepartmentRelation();
    courseDepartmentRelation.setAssignedDepartments(assignedDepartments);
    courseDepartmentRelation.setAssignedLearners(assignedLearners);
    courseRepository.updateByDepartment(CourseId.valueOf(input.getCourseId()), courseDepartmentRelation, PublishStatus.PUBLISHED);
    endTime = System.currentTimeMillis();
    LOGGER.warn("COURSE ############## => Update course by department took [{}] millis", (endTime - startTime));

    createEnrollmentsAndSendNotification(input.getNotificationInput(), assignedDepartments, assignedLearners, updatedCourse);
  }

  private void createEnrollmentsAndSendNotification(NotificationInput notificationInput, Set<DepartmentId> assignedDepartments,
      Set<LearnerId> assignedLearners, Course course)
  {
    startTime = System.currentTimeMillis();
    Set<String> enrolledLearners = createEnrollments(course.getCourseId(), assignedDepartments, assignedLearners, course.getCourseType());
    endTime = System.currentTimeMillis();
    LOGGER.warn("COURSE ############## => Create enrollments took [{}] millis", (endTime - startTime));

    Set<String> usersToNotify = new HashSet<>(enrolledLearners);
    usersToNotify.addAll(getDepartmentInstructors(assignedDepartments));
    startTime = System.currentTimeMillis();
    notifyCoursePublished(notificationInput, course, usersToNotify);
    endTime = System.currentTimeMillis();
    LOGGER.warn("COURSE ############## => Send notification took [{}] millis", (endTime - startTime));

  }

  private Set<String> createEnrollments(CourseId courseId, Set<DepartmentId> assignedDepartments, Set<LearnerId> assignedLearners, CourseType courseType)
  {
    Set<String> learners = new HashSet<>();

    assignedLearners.forEach(learner -> learners.add(learner.getId()));

    if (hasAutoChildDepartmentEnroll)
    {
      learners.addAll(getDepartmentsLearners(assignedDepartments, courseType));
    }
    learners.addAll(assignedLearners.stream().map(LearnerId::getId).collect(Collectors.toSet()));
    for (String learner : learners)
    {
      CourseEnrollment courseEnrollment = new CourseEnrollment(courseId, LearnerId.valueOf(learner));
      courseEnrollmentRepository.save(courseEnrollment);
    }

    return learners;
  }

  private void notifyCoursePublished(NotificationInput input, Course course, Set<String> learners)
  {
    Map<String, Object> publishedCourseData = new HashMap<>(course.getCourseDetail().getProperties());
    publishedCourseData.put(COURSE_ID, course.getCourseId().getId());
    publishedCourseData.put("courseName", course.getCourseDetail().getTitle());
    publishedCourseData.put("memo", input.getNote());
    publishedCourseData.put("authorId", course.getAuthorId().getId());
    publishedCourseData.putIfAbsent("endDate", "");
    lmsServiceRegistry.getNotificationService().notifyCoursePublished(learners, publishedCourseData,
        input.isSendEmail(), input.isSendSms());
    lmsServiceRegistry.getNotificationService().notifyPublisher(course.getAuthorId().getId(), publishedCourseData, true);
  }

  private Set<String> getDepartmentInstructors(Set<DepartmentId> departments)
  {
    Set<String> instructors = new HashSet<>();

    for (DepartmentId departmentId : departments)
    {
      Set<String> departmentInstructors = departmentService.getInstructors(departmentId.getId());
      instructors.addAll(departmentInstructors);
    }

    return instructors;
  }

  private Set<String> getDepartmentsLearners(Set<DepartmentId> departments, CourseType courseType)
  {
    Set<String> learners = new HashSet<>();
    Set<String> roles = new HashSet<>();
    switch (courseType.getType())
    {
    case "EMPLOYEE":
      roles.add(LmsRole.LMS_USER.name());
      roles.add(LmsRole.LMS_SUPERVISOR.name());
      roles.add(LmsRole.LMS_MANAGER.name());
      break;
    case "SUPERVISOR":
      roles.add(LmsRole.LMS_SUPERVISOR.name());
      roles.add(LmsRole.LMS_MANAGER.name());
      break;
    case "MANAGER":
      roles.add(LmsRole.LMS_MANAGER.name());
      break;
    default:
      break;
    }

    if (roles.isEmpty())
    {
      for (DepartmentId departmentId : departments)
      {
        Set<String> departmentLearners = departmentService.getLearners(departmentId.getId());
        learners.addAll(departmentLearners);
      }
    }
    else
    {
      for (DepartmentId departmentId : departments)
      {
        for (String role : roles)
        {
          Set<String> departmentLearners = departmentService.getLearnersByRole(departmentId.getId(), role);
          learners.addAll(departmentLearners);
        }
      }
    }
    return learners;
  }

  private void activateAssessmentStatus(String assessmentId)
  {
    UpdateAssessmentStatus updateAssessmentStatus = new UpdateAssessmentStatus(lmsRepositoryRegistry, lmsServiceRegistry);
    try
    {
      updateAssessmentStatus.execute(new UpdateAssessmentStatusInput(assessmentId, true));
    }
    catch (UseCaseException e)
    {
      LOGGER.error(e.getMessage(), e);
    }
  }
}

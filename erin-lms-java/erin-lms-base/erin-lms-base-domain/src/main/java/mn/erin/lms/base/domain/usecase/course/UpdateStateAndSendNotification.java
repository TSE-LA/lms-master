package mn.erin.lms.base.domain.usecase.course;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.base.domain.model.category.CourseCategoryId;
import mn.erin.lms.base.domain.model.course.Course;
import mn.erin.lms.base.domain.model.course.CourseDetail;
import mn.erin.lms.base.domain.model.course.CourseId;
import mn.erin.lms.base.domain.model.course.CourseType;
import mn.erin.lms.base.domain.model.course.DateInfo;
import mn.erin.lms.base.domain.model.enrollment.CourseEnrollment;
import mn.erin.lms.base.aim.organization.DepartmentId;
import mn.erin.lms.base.aim.user.Author;
import mn.erin.lms.base.aim.user.LearnerId;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.aim.LmsDepartmentService;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.service.NotificationService;
import mn.erin.lms.base.domain.service.UnknownCourseTypeException;
import mn.erin.lms.base.domain.usecase.course.dto.CourseDto;
import mn.erin.lms.base.domain.usecase.course.dto.UpdateCourseInput;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Authorized(users = { Author.class })
public class UpdateStateAndSendNotification extends CourseUseCase<UpdateCourseInput, CourseDto>
{
  public static final String MANAGER = "MANAGER";
  public static final String EMPLOYEE = "EMPLOYEE";
  public static final String SUPERVISOR = "SUPERVISOR";

  protected NotificationService notificationService;
  protected LmsDepartmentService departmentService;
  protected boolean isNameChanged = false;
  protected boolean isCourseTypeChanged = false;

  protected String previousName = "";
  protected String previousCourseType = "";

  public UpdateStateAndSendNotification(LmsRepositoryRegistry lmsRepositoryRegistry,
      LmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
    this.notificationService = lmsServiceRegistry.getNotificationService();
    this.departmentService = lmsServiceRegistry.getDepartmentService();
  }

  @Override
  public CourseDto execute(UpdateCourseInput input) throws UseCaseException
  {
    try
    {
      CourseId courseId = CourseId.valueOf(input.getCourseId());
      Course oldCourse = getCourse(courseId);

      if (!oldCourse.getCourseType().getType().equals(input.getType()))
      {
        updateEnrollment(oldCourse, input.getType());
      }

      Course course = getCourse(CourseId.valueOf(input.getCourseId()));
      CourseDetail courseDetail = getCourseDetail(input);
      CourseType courseType = courseTypeResolver.resolve(input.getType());
      String assessmentId = input.getAssessmentId() != null ? input.getAssessmentId() : course.getAssessmentId();
      String certificateId = input.getCertificateId() != null ? input.getCertificateId() : course.getCertificateId();
      courseDetail.getProperties().put("state", input.getProperties().get("state"));
      Course updatedCourse = courseRepository.update(CourseId.valueOf(input.getCourseId()), CourseCategoryId.valueOf(input.getCategoryId()),
          courseDetail, courseType, assessmentId, certificateId);
      notifyCourseUpdated(updatedCourse, input.getEmailSubject(), input.getTemplateName(), input.getNote());
      return toCourseDto(updatedCourse);
    }
    catch (LmsRepositoryException | UnknownCourseTypeException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }
  }


  private void updateEnrollment(Course course, String newType)
  {
    String oldType = course.getCourseType().getType();
    if (oldType.equals(MANAGER) || (oldType.equals(SUPERVISOR) && newType.equals(EMPLOYEE)))
    {
      addEnrollment(course, newType);
    }
    else if (oldType.equals(EMPLOYEE) || (oldType.equals(SUPERVISOR) && newType.equals(MANAGER)))
    {
      removeEnrollment(course, newType);
    }
  }

  // New type is either Employee or Supervisor
  private void addEnrollment(Course course, String type)
  {
    Set<String> addingLearners = new HashSet<>();
    Set<String> roles = new HashSet<>();

    roles.add("LMS_SUPERVISOR");
    if (type.equals(EMPLOYEE))
    {
      roles.add("LMS_USER");
    }

    for (DepartmentId departmentId : course.getCourseDepartmentRelation().getAssignedDepartments())
    {
      for (String role : roles)
      {
        Set<String> departmentLearners = departmentService.getLearnersByRole(departmentId.getId(), role);
        addingLearners.addAll(departmentLearners);
      }
    }

    for (String learner : addingLearners)
    {
      CourseEnrollment courseEnrollment = new CourseEnrollment(course.getCourseId(), LearnerId.valueOf(learner));
      courseEnrollmentRepository.save(courseEnrollment);
    }
  }

  // New type is either Manager or Supervisor
  private void removeEnrollment(Course course, String type)
  {
    Set<String> learners = courseEnrollmentRepository.listAll(course.getCourseId()).stream()
        .map(enrollment -> enrollment.getLearnerId().getId()).collect(Collectors.toSet());

    Set<String> removingLearners = learners.stream().filter(learner -> departmentService.getRole(learner).equals("LMS_USER")).collect(Collectors.toSet());
    if (type.equals(MANAGER))
    {
      removingLearners.addAll(learners.stream().filter(learner -> departmentService.getRole(learner).equals("LMS_SUPERVISOR")).collect(Collectors.toSet()));
    }

    for (String learner : removingLearners)
    {
      courseEnrollmentRepository.delete(LearnerId.valueOf(learner), course.getCourseId());
    }
  }

  protected CourseDetail getCourseDetail(UpdateCourseInput input) throws UseCaseException
  {
    try
    {
      Course course = courseRepository.fetchById(CourseId.valueOf(input.getCourseId()));

      isNameChanged = !course.getCourseDetail().getTitle().equals(input.getTitle());
      previousName = course.getCourseDetail().getTitle();

      isCourseTypeChanged = !course.getCourseType().getType().equals(input.getType());
      previousCourseType = course.getCourseType().getType();

      CourseDetail courseDetail = new CourseDetail(input.getTitle());
      courseDetail.setDescription(input.getDescription());
      courseDetail.setHasFeedbackOption(course.getCourseDetail().hasFeedbackOption());
      courseDetail.setHasQuiz(course.getCourseDetail().hasQuiz());
      courseDetail.setHasAssessment(course.getCourseDetail().hasAssessment());
      courseDetail.setHasCertificate(course.getCourseDetail().hasCertificate());
      courseDetail.changePublishStatus(course.getCourseDetail().getPublishStatus());
      courseDetail.addProperties(course.getCourseDetail().getProperties());
      courseDetail.addProperties(input.getProperties());
      DateInfo dateInfo = course.getCourseDetail().getDateInfo();
      dateInfo.setModifiedDate(LocalDateTime.now());
      courseDetail.setDateInfo(dateInfo);
      return courseDetail;
    }
    catch (LmsRepositoryException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }
  }

  protected void notifyCourseUpdated(Course updatedCourse, String subject, String templateName, String note)
  {
    Map<String, Object> updatedCourseData = new HashMap<>(Objects.requireNonNull(updatedCourse).getCourseDetail().getProperties());
    updatedCourseData.put("authorId", lmsServiceRegistry.getLmsUserService().getCurrentUser().getId().getId());
    updatedCourseData.put("courseName", updatedCourse.getCourseDetail().getTitle());
    updatedCourseData.put("date", updatedCourse.getCourseDetail().getProperties().get("date"));
    updatedCourseData.put("startTime", updatedCourse.getCourseDetail().getProperties().get("startTime"));
    updatedCourseData.put("endTime", updatedCourse.getCourseDetail().getProperties().get("endTime"));
    updatedCourseData.put("note", note);
    updatedCourseData.put("courseId", updatedCourse.getCourseId().getId());
    Set<DepartmentId> assignedDepartments = updatedCourse.getCourseDepartmentRelation().getAssignedDepartments();
    notificationService.notifyCourseUpdated(getAllRecipients(updatedCourse.getCourseId().getId(), assignedDepartments), updatedCourseData, subject,
        templateName);
  }

  protected Set<String> getAllRecipients(String courseId, Set<DepartmentId> assignedDepartments)
  {
    List<CourseEnrollment> enrollmentsList = courseEnrollmentRepository.listAll(CourseId.valueOf(courseId));

    Set<String> enrolledUsers = new HashSet<>();

    for (CourseEnrollment courseEnrollment : enrollmentsList)
    {
      enrolledUsers.add(courseEnrollment.getLearnerId().getId());
    }

    Set<String> admins = new HashSet<>();
    for (DepartmentId group : assignedDepartments)
    {
      Set<String> adminIds = departmentService.getInstructors(group.getId());
      admins.addAll(adminIds);
    }

    Set<String> recipients = new HashSet<>();
    recipients.addAll(admins);
    recipients.addAll(enrolledUsers);
    return recipients;
  }
}

package mn.erin.lms.base.domain.usecase.course;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.group.Group;
import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.domain.aim.model.membership.Membership;
import mn.erin.domain.aim.repository.AimRepositoryRegistry;
import mn.erin.domain.aim.repository.MembershipRepository;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.LmsDepartmentService;
import mn.erin.lms.base.aim.organization.DepartmentId;
import mn.erin.lms.base.aim.role.LmsRole;
import mn.erin.lms.base.aim.user.LearnerId;
import mn.erin.lms.base.domain.model.course.Course;
import mn.erin.lms.base.domain.model.course.CourseDepartmentRelation;
import mn.erin.lms.base.domain.model.course.CourseDetail;
import mn.erin.lms.base.domain.model.course.CourseId;
import mn.erin.lms.base.domain.model.enrollment.CourseEnrollment;
import mn.erin.lms.base.domain.repository.ClassroomCourseAttendanceRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.service.NotificationService;
import mn.erin.lms.base.domain.usecase.course.classroom.CreateClassroomCourseAttendance;
import mn.erin.lms.base.domain.usecase.course.dto.CourseAttendanceInput;
import mn.erin.lms.base.domain.usecase.course.dto.CourseDto;
import mn.erin.lms.base.domain.usecase.course.dto.LearnerAttendanceInput;

/**
 * @author Munkh
 */
public class ConfirmCourse extends CourseUseCase<String, CourseDto>
{
  private static final Logger LOG = LoggerFactory.getLogger(ConfirmCourse.class);
  public static final String MANAGER = "MANAGER";

  protected ClassroomCourseAttendanceRepository classroomCourseAttendanceRepository;
  protected NotificationService notificationService;
  protected LmsDepartmentService departmentService;
  protected MembershipRepository membershipRepository;
  protected AimRepositoryRegistry aimRepositoryRegistry;

  public ConfirmCourse(LmsRepositoryRegistry lmsRepositoryRegistry, LmsServiceRegistry lmsServiceRegistry,
      MembershipRepository membershipRepository, ClassroomCourseAttendanceRepository classroomCourseAttendanceRepository, AimRepositoryRegistry aimRepositoryRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
    this.notificationService = lmsServiceRegistry.getNotificationService();
    this.departmentService = lmsServiceRegistry.getDepartmentService();
    this.classroomCourseAttendanceRepository = classroomCourseAttendanceRepository;
    this.membershipRepository = membershipRepository;
    this.aimRepositoryRegistry = aimRepositoryRegistry;
  }

  @Override
  public CourseDto execute(String courseId) throws UseCaseException
  {
    try
    {
      Course course = getCourse(CourseId.valueOf(courseId));
      Map<String, Set<String>> suggested = courseSuggestedUsersRepository.fetchAll(courseId);
      Set<DepartmentId> suggestedGroups = suggested.get("groups").stream().map(DepartmentId::valueOf).collect(Collectors.toSet());
      Set<LearnerId> suggestedUsers = suggested.get("users").stream().map(LearnerId::valueOf).collect(Collectors.toSet());

      String courseType = course.getCourseType().getType();
      suggestedUsers.addAll(getDepartmentsLearners(suggestedGroups, courseType).stream().map(LearnerId::valueOf).collect(Collectors.toSet()));

      CourseDetail courseDetail = course.getCourseDetail();
      courseDetail.getProperties().put("state", "READY");

      CourseDepartmentRelation courseDepartmentRelation = course.getCourseDepartmentRelation();

      suggestedGroups.addAll(courseDepartmentRelation.getAssignedDepartments());

      int enrollmentCount = 0;
      if (courseType.equals(MANAGER))
      {
        enrollmentCount += courseEnrollmentRepository.getEnrollmentCount(CourseId.valueOf(courseId));
        suggestedUsers.addAll(courseDepartmentRelation.getAssignedLearners());
      }
      else
      {
        enrollmentCount += suggestedUsers.size();
      }
      courseDetail.getProperties().put("enrollmentCount", Integer.toString(enrollmentCount));
      courseDepartmentRelation.setAssignedDepartments(suggestedGroups);
      courseDepartmentRelation.setAssignedLearners(suggestedUsers);

      Course updatedCourse = courseRepository.update(CourseId.valueOf(courseId), courseDepartmentRelation, courseDetail);

      createAttendance(course, suggestedUsers);

      updateEnrollmentAndSendNotification(updatedCourse);
      return toCourseDto(updatedCourse);
    }
    catch (LmsRepositoryException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }
  }

  private void createAttendance(Course course, Set<LearnerId> learnerIds)
  {
    CreateClassroomCourseAttendance createClassroomCourseAttendance = new CreateClassroomCourseAttendance(lmsRepositoryRegistry, lmsServiceRegistry,
        classroomCourseAttendanceRepository);
    List<LearnerAttendanceInput> attendances = new ArrayList<>();

    for (LearnerId learnerId : learnerIds)
    {
      GroupId groupId = null;
      try
      {
        Membership membership = membershipRepository.findByUsername(learnerId.getId());
        groupId = membership.getGroupId();
        String groupName = departmentService.getDepartmentName(groupId.getId());
        LearnerAttendanceInput attendanceInput = new LearnerAttendanceInput(learnerId.getId(), false, null);
        if (!course.getCourseType().getType().equals(MANAGER))
        {
          String supervisor = null;
          if (!membership.getRoleId().getId().equals(LmsRole.LMS_MANAGER.name()))
          {
            supervisor = lmsServiceRegistry.getAccessIdentityManagement().getParentGroupLearnersByRole(groupId.getId(), LmsRole.LMS_SUPERVISOR.name()).stream()
                .findFirst().orElse(null);
            if (supervisor == null)
            {
              supervisor = lmsServiceRegistry.getAccessIdentityManagement().getParentGroupLearnersByRole(groupId.getId(), LmsRole.LMS_MANAGER.name()).stream()
                  .findFirst().orElse(null);
            }
          }
          else
          {
            Group group = aimRepositoryRegistry.getGroupRepository().findById(groupId);
            if (group.getParent() != null)
            {
              supervisor = lmsServiceRegistry.getAccessIdentityManagement().getParentGroupLearnersByRole(group.getParent().getId(), LmsRole.LMS_MANAGER.name())
                  .stream()
                  .findFirst().orElse(null);
            }
          }
          attendanceInput.setSupervisorId(supervisor);
        }

        attendanceInput.setGroupName(groupName);
        attendanceInput.setInvited(true);
        attendances.add(attendanceInput);
      }
      catch (AimRepositoryException e)
      {
        LOG.warn("Course [{}] confirmation failed for user [{}]", course.getCourseId(), learnerId, e);
      }
    }

    createClassroomCourseAttendance.execute(new CourseAttendanceInput(course.getCourseId().getId(), attendances));
  }

  private void updateEnrollmentAndSendNotification(Course course)
  {
    Set<LearnerId> enrolledLearners = addEnrollment(course);
    Set<String> usersToNotify = new HashSet<>(enrolledLearners).stream().map(LearnerId::getId).collect(Collectors.toSet());
    usersToNotify.addAll(getDepartmentInstructors(course.getCourseDepartmentRelation().getAssignedDepartments()));
    Map<String, Object> confirmedCourseData = new HashMap<>(course.getCourseDetail().getProperties());
    confirmedCourseData.put("courseName", course.getCourseDetail().getTitle());
    confirmedCourseData.put("courseId", course.getCourseId().getId());
    notificationService.notifyCourseConfirmed(usersToNotify, confirmedCourseData);
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

  private Set<LearnerId> addEnrollment(Course course)
  {
    Set<LearnerId> addingLearners = course.getCourseDepartmentRelation().getAssignedLearners();

    for (LearnerId learnerId : addingLearners)
    {
      CourseEnrollment courseEnrollment = new CourseEnrollment(course.getCourseId(), learnerId);
      courseEnrollmentRepository.save(courseEnrollment);
    }
    return addingLearners;
  }

  private Set<String> getDepartmentsLearners(Set<DepartmentId> departments, String courseType)
  {
    Set<String> learners = new HashSet<>();
    Set<String> roles = new HashSet<>();
    switch (courseType)
    {
    case "EMPLOYEE":
      roles.add(LmsRole.LMS_USER.name());
      break;
    case "SUPERVISOR":
      roles.add(LmsRole.LMS_SUPERVISOR.name());
      break;
    case MANAGER:
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
}

package mn.erin.lms.base.domain.usecase.course.classroom;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.group.Group;
import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.domain.aim.model.membership.Membership;
import mn.erin.domain.aim.repository.AimRepositoryRegistry;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.role.LmsRole;
import mn.erin.lms.base.domain.model.course.Course;
import mn.erin.lms.base.domain.model.course.CourseDepartmentRelation;
import mn.erin.lms.base.domain.model.course.CourseDetail;
import mn.erin.lms.base.domain.model.course.CourseId;
import mn.erin.lms.base.aim.user.LearnerId;
import mn.erin.lms.base.domain.model.enrollment.CourseEnrollment;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.course.CourseUseCase;
import mn.erin.lms.base.domain.model.classroom_course.Attendance;
import mn.erin.lms.base.domain.model.classroom_course.ClassroomCourseAttendance;
import mn.erin.lms.base.domain.repository.ClassroomCourseAttendanceRepository;
import mn.erin.lms.base.domain.usecase.course.dto.CourseAttendanceInput;

/**
 * @author Munkh
 */
public class UpdateClassroomCourseAttendance extends CourseUseCase<CourseAttendanceInput, ClassroomCourseAttendance>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(UpdateClassroomCourseAttendance.class);
  private final ClassroomCourseAttendanceRepository classroomCourseAttendanceRepository;
  private final AimRepositoryRegistry aimRepositoryRegistry;
  private Course course;

  public UpdateClassroomCourseAttendance(LmsRepositoryRegistry lmsRepositoryRegistry,
      LmsServiceRegistry lmsServiceRegistry, AimRepositoryRegistry aimRepositoryRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
    this.classroomCourseAttendanceRepository = lmsRepositoryRegistry.getClassroomAttendanceRepository();
    this.aimRepositoryRegistry = aimRepositoryRegistry;
  }

  @Override
  public ClassroomCourseAttendance execute(CourseAttendanceInput input) throws UseCaseException
  {
    Validate.notNull(input);
    List<Attendance> attendances = input.getAttendances().stream()
        .map(attendance -> {
          Attendance mapped = new Attendance(LearnerId.valueOf(attendance.getLearnerId()), attendance.isPresent(), attendance.getGrades());
          mapped.setGroupName(attendance.getGroupName());
          mapped.setInvited(attendance.isInvited());
          mapped.setSupervisorId(attendance.getSupervisorId() != null ? LearnerId.valueOf(attendance.getSupervisorId()) : null);
          return mapped;
        })
        .collect(Collectors.toList());
    CourseId courseId = CourseId.valueOf(input.getCourseId());
    List<CourseEnrollment> existingEnrollments = courseEnrollmentRepository.listAll(courseId);
    Set<String> newLearners = new HashSet<>();

    try
    {
      course = lmsRepositoryRegistry.getCourseRepository().fetchById(courseId);
      CourseDepartmentRelation courseDepartmentRelation = course.getCourseDepartmentRelation();
      Set<LearnerId> learnerIds = attendances.stream().map(Attendance::getLearnerId).collect(Collectors.toSet());
      courseDepartmentRelation.setAssignedLearners(learnerIds);
      CourseDetail courseDetail = course.getCourseDetail();
      courseDetail.getProperties().put("enrollmentCount", Integer.toString(attendances.size()));
      courseRepository.update(courseId, courseDepartmentRelation, courseDetail);
    }
    catch (LmsRepositoryException e)
    {
      throw new UseCaseException(e.getMessage());
    }

    for (Attendance attendance : attendances)
    {
      List<CourseEnrollment> found = existingEnrollments.stream().filter(courseEnrollment -> courseEnrollment.getLearnerId().equals(attendance.getLearnerId()))
          .collect(
              Collectors.toList());
      if (found.isEmpty())
      {
        assignSupersAndCreateEnrollments(courseId, newLearners, attendance);
      }
    }
    /*Update suggested user list*/
    Set<String> attendanceNames = attendances.stream().map(attendance -> attendance.getLearnerId().getId()).collect(Collectors.toSet());
    courseSuggestedUsersRepository.updateUsers(courseId.getId(), attendanceNames);

    /*Update course attendance list*/
    ClassroomCourseAttendance classroomCourseAttendance = new ClassroomCourseAttendance(courseId, attendances);
    classroomCourseAttendanceRepository.save(classroomCourseAttendance);
    try
    {
      return classroomCourseAttendanceRepository.findByCourseId(courseId);
    }
    catch (LmsRepositoryException e)
    {
      LOGGER.error(e.getMessage(), e);
    }
    return null;
  }

  private void assignSupersAndCreateEnrollments(CourseId courseId, Set<String> newLearners, Attendance attendance)
  {
    try
    {
      setSupervisor(attendance);
      CourseEnrollment courseEnrollment = new CourseEnrollment(courseId, attendance.getLearnerId());
      /*Create enrollment for new learners*/
      courseEnrollmentRepository.save(courseEnrollment);
      newLearners.add(attendance.getLearnerId().getId());
    }
    catch (AimRepositoryException e)
    {
      LOGGER.error(e.getMessage());
    }
  }

  private void setSupervisor(Attendance attendance) throws AimRepositoryException
  {
    Membership membership = aimRepositoryRegistry.getMembershipRepository().findByUsername(attendance.getLearnerId().getId());
    GroupId groupId = membership.getGroupId();
    if (!course.getCourseType().getType().equals("MANAGER"))
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
          supervisor = lmsServiceRegistry.getAccessIdentityManagement().getParentGroupLearnersByRole(group.getParent().getId(), LmsRole.LMS_MANAGER.name()).stream()
              .findFirst().orElse(null);
        }
      }

      attendance.setSupervisorId(supervisor != null? LearnerId.valueOf(supervisor): null);
    }
  }
}

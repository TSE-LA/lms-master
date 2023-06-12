package mn.erin.lms.base.domain.usecase.course.classroom;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;

import mn.erin.lms.base.aim.user.Supervisor;
import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.base.domain.model.course.CourseId;
import mn.erin.lms.base.aim.user.Instructor;
import mn.erin.lms.base.aim.user.LearnerId;
import mn.erin.lms.base.aim.user.Manager;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.course.CourseUseCase;
import mn.erin.lms.base.domain.model.classroom_course.Attendance;
import mn.erin.lms.base.domain.model.classroom_course.ClassroomCourseAttendance;
import mn.erin.lms.base.domain.repository.ClassroomCourseAttendanceRepository;
import mn.erin.lms.base.domain.usecase.course.dto.CourseAttendanceInput;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Authorized(users = { Instructor.class, Manager.class, Supervisor.class })
public class CreateClassroomCourseAttendance extends CourseUseCase<CourseAttendanceInput, Void>
{
  private final ClassroomCourseAttendanceRepository classroomCourseAttendanceRepository;
  public CreateClassroomCourseAttendance(LmsRepositoryRegistry lmsRepositoryRegistry,
      LmsServiceRegistry lmsServiceRegistry, ClassroomCourseAttendanceRepository classroomCourseAttendanceRepository)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
    this.classroomCourseAttendanceRepository = classroomCourseAttendanceRepository;
  }

  @Override
  public Void execute(CourseAttendanceInput input)
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
    ClassroomCourseAttendance courseAttendance = new ClassroomCourseAttendance(CourseId.valueOf(input.getCourseId()), attendances);
    classroomCourseAttendanceRepository.save(courseAttendance);
    return null;
  }
}

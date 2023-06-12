package mn.erin.lms.base.domain.usecase.course.classroom;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import mn.erin.domain.aim.model.user.UserAggregate;
import mn.erin.domain.aim.model.user.UserStatus;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.user.LearnerId;
import mn.erin.lms.base.domain.model.classroom_course.Attendance;
import mn.erin.lms.base.domain.model.course.CourseId;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.course.CourseUseCase;
import mn.erin.lms.base.domain.model.classroom_course.ClassroomCourseAttendance;
import mn.erin.lms.base.domain.repository.ClassroomCourseAttendanceRepository;

/**
 * @author Munkh
 */
public class GetClassroomCourseAttendance extends CourseUseCase<String, ClassroomCourseAttendance>
{
  private final ClassroomCourseAttendanceRepository classroomCourseAttendanceRepository;

  public GetClassroomCourseAttendance(LmsRepositoryRegistry lmsRepositoryRegistry, LmsServiceRegistry lmsServiceRegistry,
      ClassroomCourseAttendanceRepository classroomCourseAttendanceRepository)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
    this.classroomCourseAttendanceRepository = classroomCourseAttendanceRepository;
  }

  @Override
  public ClassroomCourseAttendance execute(String courseId) throws UseCaseException
  {
    if(StringUtils.isBlank(courseId))
    {
      throw new UseCaseException("Input cannot be null or blank!");
    }
    try
    {
      ClassroomCourseAttendance courseAttendance = classroomCourseAttendanceRepository.findByCourseId(CourseId.valueOf(courseId));
      Set<String> usernames = courseAttendance.getAttendances().stream().map(Attendance::getLearnerId).map(LearnerId::getId).collect(Collectors.toSet());
      List<UserAggregate> userAggregates = lmsServiceRegistry.getAccessIdentityManagement().getUserAggregatesByUsername(usernames);

      for (UserAggregate userAggregate : userAggregates)
      {
        if (!userAggregate.getStatus().equals(UserStatus.ACTIVE))
        {
          courseAttendance.getAttendances().removeIf(attendance -> attendance.getLearnerId().getId().equals(userAggregate.getUsername()));
        }
      }

      return courseAttendance;
    }
    catch (LmsRepositoryException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }
  }
}

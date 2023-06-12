package mn.erin.lms.base.domain.usecase.course.classroom;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.user.LearnerId;
import mn.erin.lms.base.domain.model.classroom_course.Attendance;
import mn.erin.lms.base.domain.model.classroom_course.ClassroomCourseAttendance;
import mn.erin.lms.base.domain.model.course.CourseId;
import mn.erin.lms.base.domain.repository.ClassroomCourseAttendanceRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.course.CourseUseCase;
import mn.erin.lms.base.domain.usecase.course.dto.DeleteClassroomAttendanceInput;

/**
 * @author Oyungerel Chuluunsukh.
 */
public class DeleteClassroomCourseAttendance extends CourseUseCase<DeleteClassroomAttendanceInput, Boolean>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(DeleteClassroomCourseAttendance.class);
  private final ClassroomCourseAttendanceRepository classroomCourseAttendanceRepository;

  public DeleteClassroomCourseAttendance(LmsRepositoryRegistry lmsRepositoryRegistry,
      LmsServiceRegistry lmsServiceRegistry,
      ClassroomCourseAttendanceRepository classroomCourseAttendanceRepository)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
    this.classroomCourseAttendanceRepository = classroomCourseAttendanceRepository;
  }

  @Override
  public Boolean execute(DeleteClassroomAttendanceInput input) throws UseCaseException
  {
    if (StringUtils.isBlank(input.getCourseId()))
    {
      throw new UseCaseException("Classroom course id cannot be null!");
    }

    if (StringUtils.isBlank(input.getLearnerId()))
    {
      throw new UseCaseException("Learner id cannot be null!");
    }
    CourseId courseId = CourseId.valueOf(input.getCourseId());
    LearnerId learnerId = LearnerId.valueOf(input.getLearnerId());
    ClassroomCourseAttendance courseAttendance;
    try
    {
      courseAttendance = this.classroomCourseAttendanceRepository.findByCourseId(courseId);
    }
    catch (LmsRepositoryException e)
    {
      LOGGER.error(e.getMessage());
      return false;
    }
    List<Attendance> attendances = courseAttendance.getAttendances().stream().filter(attendance ->
        attendance.getLearnerId().equals(learnerId)).collect(Collectors.toList());
    if (!attendances.isEmpty())
    {
      courseAttendance.getAttendances().remove(attendances.get(0));
      this.classroomCourseAttendanceRepository.save(courseAttendance);
      return this.courseEnrollmentRepository.delete(learnerId, courseId);
    }
    else
    {
      return false;
    }
  }
}

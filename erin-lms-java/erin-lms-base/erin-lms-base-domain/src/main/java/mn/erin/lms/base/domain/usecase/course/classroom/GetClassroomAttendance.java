package mn.erin.lms.base.domain.usecase.course.classroom;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.domain.model.course.CourseId;
import mn.erin.lms.base.aim.user.LearnerId;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.course.CourseUseCase;
import mn.erin.lms.base.domain.model.classroom_course.Attendance;
import mn.erin.lms.base.domain.repository.ClassroomCourseAttendanceRepository;
import mn.erin.lms.base.domain.usecase.course.classroom.dto.GetAttendanceInput;

/**
 * @author Munkh
 */
public class GetClassroomAttendance extends CourseUseCase<GetAttendanceInput, Attendance>
{
  private final ClassroomCourseAttendanceRepository classroomCourseAttendanceRepository;
  public GetClassroomAttendance(LmsRepositoryRegistry lmsRepositoryRegistry,
      LmsServiceRegistry lmsServiceRegistry, ClassroomCourseAttendanceRepository classroomCourseAttendanceRepository)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
    this.classroomCourseAttendanceRepository = classroomCourseAttendanceRepository;
  }

  @Override
  public Attendance execute(GetAttendanceInput input) throws UseCaseException
  {
    if(input == null || input.getCourseId() == null || input.getLearnerId() == null)
    {
      throw new UseCaseException("Input cannot be null!");
    }
    try
    {
      return classroomCourseAttendanceRepository.findByCourseIdAndLearnerId(CourseId.valueOf(input.getCourseId()), LearnerId.valueOf(input.getLearnerId()));
    }
    catch (LmsRepositoryException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }
  }
}

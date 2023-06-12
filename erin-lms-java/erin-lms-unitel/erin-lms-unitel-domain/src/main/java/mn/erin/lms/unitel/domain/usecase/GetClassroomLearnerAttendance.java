package mn.erin.lms.unitel.domain.usecase;

import java.util.List;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.user.LearnerId;
import mn.erin.lms.base.domain.model.classroom_course.ClassroomCourseAttendance;
import mn.erin.lms.base.domain.repository.ClassroomCourseAttendanceRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.course.CourseUseCase;

/**
 * @author Munkh
 */
public class GetClassroomLearnerAttendance extends CourseUseCase<String, List<ClassroomCourseAttendance>>
{
  private final ClassroomCourseAttendanceRepository classroomCourseAttendanceRepository;

  public GetClassroomLearnerAttendance(LmsRepositoryRegistry lmsRepositoryRegistry,
      LmsServiceRegistry lmsServiceRegistry, ClassroomCourseAttendanceRepository classroomCourseAttendanceRepository)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
    this.classroomCourseAttendanceRepository = classroomCourseAttendanceRepository;
  }

  @Override
  public List<ClassroomCourseAttendance> execute(String input) throws UseCaseException
  {
    if (input == null)
    {
      throw new UseCaseException("Input cannot be null!");
    }
    return classroomCourseAttendanceRepository.findByLearnerId(LearnerId.valueOf(input));
  }
}

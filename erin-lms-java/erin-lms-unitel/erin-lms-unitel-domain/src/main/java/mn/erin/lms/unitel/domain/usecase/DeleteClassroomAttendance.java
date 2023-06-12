package mn.erin.lms.unitel.domain.usecase;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.domain.model.course.CourseId;
import mn.erin.lms.base.domain.repository.ClassroomCourseAttendanceRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.course.CourseUseCase;

/**
 * @author Munkh
 */
public class DeleteClassroomAttendance extends CourseUseCase<String, Boolean>
{
  private final ClassroomCourseAttendanceRepository classroomCourseAttendanceRepository;

  public DeleteClassroomAttendance(LmsRepositoryRegistry lmsRepositoryRegistry,
      LmsServiceRegistry lmsServiceRegistry,
      ClassroomCourseAttendanceRepository classroomCourseAttendanceRepository)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
    this.classroomCourseAttendanceRepository = classroomCourseAttendanceRepository;
  }

  @Override
  public Boolean execute(String input) throws UseCaseException
  {
    Validate.notNull(input);
    return classroomCourseAttendanceRepository.deleteCourseAttendance(CourseId.valueOf(input));
  }
}

package mn.erin.lms.base.domain.usecase.course.classroom;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.domain.model.course.CourseId;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.course.CourseUseCase;
import mn.erin.lms.base.domain.repository.ClassroomCourseAttendanceRepository;

/**
 * @author Munkh
 */
public class DeleteClassroomAllAttendance extends CourseUseCase<String, Boolean>
{
  private final ClassroomCourseAttendanceRepository classroomCourseAttendanceRepository;

  public DeleteClassroomAllAttendance(LmsRepositoryRegistry lmsRepositoryRegistry,
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

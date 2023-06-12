package mn.erin.lms.unitel.domain.usecase;

import org.apache.commons.lang3.StringUtils;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.domain.model.classroom_course.ClassroomCourseAttendance;
import mn.erin.lms.base.domain.model.course.CourseId;
import mn.erin.lms.base.domain.repository.ClassroomCourseAttendanceRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.course.CourseUseCase;

/**
 * @author Munkh
 */
public class GetClassroomCourseAttendance extends CourseUseCase<String, ClassroomCourseAttendance>
{
  private final ClassroomCourseAttendanceRepository classroomCourseAttendanceRepository;
  public GetClassroomCourseAttendance(LmsRepositoryRegistry lmsRepositoryRegistry,
      LmsServiceRegistry lmsServiceRegistry, ClassroomCourseAttendanceRepository classroomCourseAttendanceRepository)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
    this.classroomCourseAttendanceRepository = classroomCourseAttendanceRepository;
  }

  @Override
  public ClassroomCourseAttendance execute(String input) throws UseCaseException
  {
    if(StringUtils.isBlank(input))
    {
      throw new UseCaseException("Input cannot be null or blank!");
    }
    try
    {
      return classroomCourseAttendanceRepository.findByCourseId(CourseId.valueOf(input));
    }
    catch (LmsRepositoryException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }
  }
}

package mn.erin.lms.unitel.domain.usecase;

import java.util.List;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.user.LearnerId;
import mn.erin.lms.base.domain.model.classroom_course.Attendance;
import mn.erin.lms.base.domain.model.classroom_course.ClassroomCourseAttendance;
import mn.erin.lms.base.domain.repository.ClassroomCourseAttendanceRepository;

/**
 * @author Munkh
 */
public class GetClassroomCourseTotalScore implements UseCase<String, Integer>
{
  private final ClassroomCourseAttendanceRepository classroomCourseAttendanceRepository;

  public GetClassroomCourseTotalScore(ClassroomCourseAttendanceRepository classroomCourseAttendanceRepository)
  {
    this.classroomCourseAttendanceRepository = classroomCourseAttendanceRepository;
  }

  @Override
  public Integer execute(String input) throws UseCaseException
  {
    Validate.notNull(input);
    int totalCourse = 0;
    int averageScore = 0;
    List<ClassroomCourseAttendance> attendances = classroomCourseAttendanceRepository.findByLearnerId(LearnerId.valueOf(input));

    for (ClassroomCourseAttendance classroomCourseAttendance : attendances)
    {
      Attendance attendance = classroomCourseAttendance.getAttendances().get(0);
      List<Integer> scores = attendance.getScores();

      if (!attendance.isPresent() ||
          scores == null)
      {
        continue;
      }

      int totalScorePerCourse = 0;
      int nullCount = 0;

      for (Integer score : scores)
      {
        if (score == null)
        {
          nullCount++;
        }
        else
        {
          totalScorePerCourse += score;
        }
      }
      if(scores.size() != nullCount){
        averageScore += totalScorePerCourse / (scores.size() - nullCount);
        totalCourse++;
      }
    }

    return averageScore == 0 ? averageScore : averageScore / totalCourse;
  }
}

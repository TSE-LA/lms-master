package mn.erin.lms.jarvis.domain.report.usecase;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.base.aim.user.Author;
import mn.erin.lms.base.aim.user.Instructor;
import mn.erin.lms.base.aim.user.Manager;
import mn.erin.lms.base.aim.user.Supervisor;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.course.CourseUseCase;
import mn.erin.lms.base.domain.usecase.course.GetCourseById;
import mn.erin.lms.base.domain.usecase.course.classroom.GetClassroomLearnerAttendance;
import mn.erin.lms.base.domain.usecase.course.dto.CourseDto;
import mn.erin.lms.jarvis.domain.report.usecase.dto.ClassroomActivityReportDto;
import mn.erin.lms.jarvis.domain.report.usecase.dto.ReportFilter;
import mn.erin.lms.base.domain.model.classroom_course.Attendance;
import mn.erin.lms.base.domain.model.classroom_course.ClassroomCourseAttendance;
import mn.erin.lms.base.domain.repository.ClassroomCourseAttendanceRepository;

/**
 * @author Munkh
 */
@Authorized(users = { Author.class, Manager.class, Instructor.class, Supervisor.class})
public class GenerateClassroomActivityReports extends CourseUseCase<ReportFilter, List<ClassroomActivityReportDto>>
{
  private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
  private final ClassroomCourseAttendanceRepository classroomCourseAttendanceRepository;

  public GenerateClassroomActivityReports(LmsRepositoryRegistry lmsRepositoryRegistry,
      LmsServiceRegistry lmsServiceRegistry, ClassroomCourseAttendanceRepository classroomCourseAttendanceRepository)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
    this.classroomCourseAttendanceRepository = classroomCourseAttendanceRepository;
  }

  @Override
  public List<ClassroomActivityReportDto> execute(ReportFilter input) throws UseCaseException
  {
    GetClassroomLearnerAttendance getClassroomLearnerAttendance = new GetClassroomLearnerAttendance(lmsRepositoryRegistry, lmsServiceRegistry,
        classroomCourseAttendanceRepository);

    List<ClassroomCourseAttendance> courseAttendances = getClassroomLearnerAttendance.execute(input.getLearnerId());
    GetCourseById getCourseById = new GetCourseById(lmsRepositoryRegistry, lmsServiceRegistry);
    List<ClassroomActivityReportDto> reports = new ArrayList<>();
    for (ClassroomCourseAttendance courseAttendance: courseAttendances)
    {
      CourseDto courseDto = getCourseById.execute(courseAttendance.getCourseId().getId());
      String date = courseDto.getProperties().get("date");
      if (date == null || !dateIsBetween(LocalDate.parse(date, DATE_TIME_FORMATTER), input.getStartDate(), input.getEndDate()))
      {
        continue;
      }

      Attendance attendance = courseAttendance.getAttendances().get(0);

      int avg = 0;
      if (attendance.getScores() != null && !attendance.getScores().isEmpty())
      {
        int sum = 0;
        List<Integer> scores = attendance.getScores().stream().filter(Objects::nonNull).collect(Collectors.toList());

        if (!scores.isEmpty())
        {
          for (int score : scores)
          {
            sum += score;
          }
          avg = sum/scores.size();
        }

      }

      ClassroomActivityReportDto report = new ClassroomActivityReportDto.Builder(courseDto.getId())
          .withName(courseDto.getTitle())
          .withCourseType(courseDto.getCourseCategoryName())
          .isPresent(attendance.isPresent())
          .withTestScore(avg)
          .withCertificate(courseDto.getCertificateId())
          .withTeacher(courseDto.getProperties().get("teacher"))
          .withStartTime(courseDto.getProperties().get("startTime"))
          .withEndTime(courseDto.getProperties().get("endTime"))
          .withDate(courseDto.getProperties().get("date"))
          .build();

      reports.add(report);
    }
    return reports;
  }

  private boolean dateIsBetween(LocalDate date, LocalDate startDate, LocalDate endDate)
  {
    return (date.isAfter(startDate) || date.isEqual(startDate)) &&
        (date.isBefore(endDate) || date.isEqual(endDate));
  }
}

package mn.erin.lms.base.domain.usecase.course.classroom;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import mn.erin.common.excel.ExcelWriterUtil;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.domain.model.course.Course;
import mn.erin.lms.base.domain.model.course.CourseId;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.course.CourseUseCase;
import mn.erin.lms.base.domain.usecase.course.classroom.dto.AttendanceDto;
import mn.erin.lms.base.domain.usecase.course.classroom.dto.DownloadClassroomCourseAttendanceInput;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class DownloadClassroomCourseAttendance extends CourseUseCase<DownloadClassroomCourseAttendanceInput, byte[]>
{
  private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

  public DownloadClassroomCourseAttendance(LmsRepositoryRegistry lmsRepositoryRegistry, LmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
  }

  @Override
  public byte[] execute(DownloadClassroomCourseAttendanceInput input) throws UseCaseException
  {
    Course course = getCourse(CourseId.valueOf(input.getCourseId()));
    String sheetTitle = course.getCourseDetail().getTitle() + " нэртэй танхимын сургалтын бүртгэл хуудас\nҮүсгэсэн огноо: " +
        LocalDateTime.now().format(dateFormatter);
    String[] headers = getHeaders().toArray(new String[0]);
    Object[][] data = convert(input);

    try
    {
      try (ByteArrayOutputStream os = new ByteArrayOutputStream())
      {
        ExcelWriterUtil.write(false, sheetTitle, headers, data, os);
        return os.toByteArray();
      }
    }
    catch (IOException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }
  }

  private Object[][] convert(DownloadClassroomCourseAttendanceInput input)
  {
    List<AttendanceDto> attendances = input.getAttendances();

    int size = attendances.size();
    Object[][] data = new Object[size][];

    for (int index = 0; index < attendances.size(); index++)
    {
      AttendanceDto attendance = attendances.get(index);
      int score1 = attendance.getGrade1() != null ? Integer.parseInt(attendance.getGrade1()) : 0;
      int score2 = attendance.getGrade2() != null ? Integer.parseInt(attendance.getGrade2()) : 0;
      int score3 = attendance.getGrade3() != null ? Integer.parseInt(attendance.getGrade3()) : 0;
      String department = attendance.getDepartment() != null ? attendance.getDepartment() : "";
      String supervisor = attendance.getSupervisor() != null ? attendance.getSupervisor() : "";
      Object[] row = { 0, attendance.getEmployeeName(), department, supervisor, "ИЛГЭЭСЭН", attendance.isPresent() ?
          "ИРСЭН" : "ИРЭЭГҮЙ", score1, score2, score3 };
      data[index] = row;
    }

    return data;
  }

  private List<String> getHeaders()
  {
    List<String> headers = new ArrayList<>();
    headers.add("№");
    headers.add("Суралцагчийн нэр");
    headers.add("Групп");
    headers.add("Ахлах");
    headers.add("Урилга");
    headers.add("Ирц");
    headers.add("Дүн 1");
    headers.add("Дүн 2");
    headers.add("Дүн 3");
    return headers;
  }
}

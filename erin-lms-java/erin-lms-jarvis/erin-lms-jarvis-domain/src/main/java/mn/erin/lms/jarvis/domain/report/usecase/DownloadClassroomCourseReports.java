package mn.erin.lms.jarvis.domain.report.usecase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.common.excel.ExcelTableDataConverter;
import mn.erin.common.excel.ExcelWriterUtil;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.user.Author;
import mn.erin.lms.base.aim.user.Instructor;
import mn.erin.lms.base.aim.user.Manager;
import mn.erin.lms.base.aim.user.Supervisor;
import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.course.CourseUseCase;
import mn.erin.lms.base.domain.usecase.course.dto.CourseDto;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Authorized(users = { Author.class, Manager.class, Instructor.class, Supervisor.class })
public class DownloadClassroomCourseReports extends CourseUseCase<List<CourseDto>,byte[]>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(DownloadCourseReports.class);
  private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

  private static final String[] CLASSROOM_COURSE_REPORT_HEADERS = {
      "№",
      "Нэр",
      "Төрөл",
      "Хэзээ",
      "Хэн ЗБ",
      "Суух ёстой",
      "Хаана ЗБ",
      "Хэдэн хүн суусан",
      "Групп",
      "Үргэлжлэх хугацаа"
  };
  private final ExcelTableDataConverter<List<CourseDto>> excelTableDataConverter = new ClassroomCourseReportExcelTableDataConverter();

  public DownloadClassroomCourseReports(LmsRepositoryRegistry lmsRepositoryRegistry,
      LmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
  }

  public byte[] execute(List<CourseDto> reportResults) throws UseCaseException
  {
    try (ByteArrayOutputStream os = new ByteArrayOutputStream())
    {
      Object[][] excelData = excelTableDataConverter.convert(reportResults);
      String sheetTitle = ("Танхимын сургалт тайлан\nТатаж авсан: " + LocalDateTime.now().format(dateFormatter));
      ExcelWriterUtil.write(false, sheetTitle, CLASSROOM_COURSE_REPORT_HEADERS, excelData, os);
      return os.toByteArray();
    }
    catch (IOException e)
    {
      LOGGER.error(e.getMessage(), e);
      return new byte[0];
    }
  }
}

package mn.erin.lms.unitel.domain.usecase;

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
import mn.erin.lms.unitel.domain.usecase.dto.CourseReportDto;

/**
 * @author Erdenetulga
 */
@Authorized(users = { Author.class, Manager.class, Instructor.class, Supervisor.class })
public class DownloadCourseActivityReports extends CourseUseCase<List<CourseReportDto>, byte[]>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(DownloadCourseReports.class);
  private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

  private static final String[] COURSE_ACTIVITY_REPORT_HEADERS = {
      "№",
      "Сургалтын нэр",
      "Хамрах хүрээ",
      "Статус",
      "Сорилын оноо",
      "Сорилд зарцуулсан хугацаа",
      "Сертификат",
      "Үнэлгээ",
      "Үзсэн давтамж",
      "Зарцуулсан хугацаа",
      "Анх танилцсан",
      "Сүүлд танилцсан"
  };
  private final ExcelTableDataConverter<List<CourseReportDto>> excelTableDataConverter = new CourseActivityReportExcelTableDataConverter();

  public DownloadCourseActivityReports(LmsRepositoryRegistry lmsRepositoryRegistry,
      LmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
  }

  @Override
  public byte[] execute(List<CourseReportDto> reportResults) throws UseCaseException
  {
    try (ByteArrayOutputStream os = new ByteArrayOutputStream())
    {
      Object[][] excelData = excelTableDataConverter.convert(reportResults);
      String sheetTitle = ("Сургалтын идэвх тайлан\nТатаж авсан: " + LocalDateTime.now().format(dateFormatter));
      ExcelWriterUtil.write(false, sheetTitle, COURSE_ACTIVITY_REPORT_HEADERS, excelData, os);
      return os.toByteArray();
    }
    catch (IOException e)
    {
      LOGGER.error(e.getMessage(), e);
      return new byte[0];
    }
  }
}

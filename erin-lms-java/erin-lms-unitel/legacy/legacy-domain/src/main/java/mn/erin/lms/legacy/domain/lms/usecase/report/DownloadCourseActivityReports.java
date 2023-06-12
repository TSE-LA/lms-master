package mn.erin.lms.legacy.domain.lms.usecase.report;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.common.excel.ExcelTableDataConverter;
import mn.erin.common.excel.ExcelWriterUtil;
import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.legacy.domain.unitel.service.PromotionActivityReportExcelTableDataConverter;

/**
 * @author Erdenetulga
 */
public class DownloadCourseActivityReports implements UseCase<List<CourseReportResult>, byte[]>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(DownloadCourseActivityReports.class);
  private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");


  private final ExcelTableDataConverter<List<CourseReportResult>> excelTableDataConverter = new PromotionActivityReportExcelTableDataConverter();
  private static final String[] PROMOTION_ACTIVITY_REPORT_HEADERS = {
      "№",
      "Нэр",
      "Үйлчилгээний төрөл",
      "Статус",
      "Сорил",
      "Асуулга",
      "Үзсэн давтамж",
      "Зарцуулсан хугацаа",
      "Анх танилцсан",
      "Сүүлд танилцсан"
  };

  @Override
  public byte[] execute(List<CourseReportResult> reportResults) throws UseCaseException
  {
    try (ByteArrayOutputStream os = new ByteArrayOutputStream())
    {
      Object[][] excelData = excelTableDataConverter.convert(reportResults);
      String sheetTitle = ("Урамшууллын идэвхийн тайлан\nТатаж авсан: " + LocalDateTime.now().format(dateFormatter));
      ExcelWriterUtil.write(false, sheetTitle, PROMOTION_ACTIVITY_REPORT_HEADERS, excelData, os);
      return os.toByteArray();
    }
    catch (IOException e)
    {
      LOGGER.error(e.getMessage(), e);
      return new byte[0];
    }
  }
}

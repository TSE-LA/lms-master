package mn.erin.lms.unitel.domain.service;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;

import mn.erin.common.excel.ExcelTableDataConverter;
import mn.erin.lms.unitel.domain.CourseConstants;
import mn.erin.lms.unitel.domain.usecase.dto.CourseReportDto;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class PromotionReportTableDataConverter implements ExcelTableDataConverter<List<CourseReportDto>>
{
  private DecimalFormat decimalFormat = new DecimalFormat("#.##");

  @Override
  public Object[][] convert(List<CourseReportDto> reports)
  {
    int reportDataSize = reports.size();
    Object[][] data = new Object[reportDataSize][];

    for (int index = 0; index < reportDataSize; index++)
    {
      CourseReportDto report = reports.get(index);
      Map<String, String> promotionProperties = report.getCourseProperties();
      Map<String, Object> promotionReportData = report.getReportData();

      String code = promotionProperties.get(CourseConstants.PROPERTY_CODE);
      String name = report.getCourseName();
      LocalDate createdDate = report.getCourseCreatedDate();
      String author = report.getAuthorId();

      float status = promotionReportData.get(CourseConstants.REPORT_FIELD_STATUS) != null ?
          Float.parseFloat(decimalFormat.format((float) promotionReportData.get(CourseConstants.REPORT_FIELD_STATUS))) : 0;

      int views = promotionReportData.containsKey(CourseConstants.REPORT_FIELD_VIEWS) ?
          Integer.parseInt(String.valueOf(promotionReportData.get(CourseConstants.REPORT_FIELD_VIEWS))) : 0;

      boolean hasQuiz = report.hasQuiz();

      String questionCount = String.valueOf(promotionReportData.get(CourseConstants.REPORT_FIELD_QUESTIONS_COUNT));

      float score = promotionReportData.get(CourseConstants.REPORT_FIELD_SCORE) != null ?
          Float.parseFloat(decimalFormat.format((float) promotionReportData.get(CourseConstants.REPORT_FIELD_SCORE))) : 0;

      int totalEnrollment = promotionReportData.get(CourseConstants.REPORT_FIELD_TOTAL_ENROLLMENT) != null ?
          Integer.parseInt(decimalFormat.format((int) promotionReportData.get(CourseConstants.REPORT_FIELD_TOTAL_ENROLLMENT))) : 0;

      int feedback = promotionReportData.containsKey(CourseConstants.REPORT_FIELD_FEEDBACK) ?
          Integer.parseInt(decimalFormat.format((int) promotionReportData.get(CourseConstants.REPORT_FIELD_FEEDBACK))) : 0;

      Object[] row = { 0, code, name, Date.from(createdDate.atStartOfDay(ZoneId.systemDefault()).toInstant()), author, status, views,
          hasQuiz ? "Тийм" : "Үгүй", questionCount, score, totalEnrollment, feedback };

      data[index] = row;
    }

    return data;
  }
}

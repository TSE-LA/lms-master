package mn.erin.lms.jarvis.domain.report.usecase;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

import mn.erin.common.excel.ExcelTableDataConverter;
import mn.erin.lms.jarvis.domain.report.usecase.dto.CourseReportDto;

/**
 * @author Erdenetulga
 */
public class OnlineCourseReportExcelTableDataConverter implements ExcelTableDataConverter<List<CourseReportDto>>
{
  private DecimalFormat decimalFormat = new DecimalFormat("#.##");

  @Override
  @SuppressWarnings("unchecked")
  public Object[][] convert(List<CourseReportDto> reportResults)
  {
    int reportDataSize = reportResults.size();
    Object[][] data = new Object[reportDataSize][];

    for (int index = 0; index < reportDataSize; index++)
    {
      CourseReportDto report = reportResults.get(index);
      Map<String, Object> courseReportData = report.getReportData();

      String name = report.getCourseName();

      String state = courseReportData.get(CourseConstants.PROPERTY_STATE) != null ?
          (String) courseReportData.get(CourseConstants.PROPERTY_STATE) : "";

      String category = courseReportData.get(CourseConstants.PROPERTY_CATEGORY) != null ?
          (String) courseReportData.get(CourseConstants.PROPERTY_CATEGORY) : "";

      int status = courseReportData.get(CourseConstants.REPORT_FIELD_STATUS) != null ?
          Integer.parseInt(decimalFormat.format((int) courseReportData.get(CourseConstants.REPORT_FIELD_STATUS))) : 0;
      int completedViewers = courseReportData.get(CourseConstants.REPORT_FIELD_COMPLETEDVIEWERSCOUNT) != null ?
          Integer.parseInt(decimalFormat.format((int) courseReportData.get(CourseConstants.REPORT_FIELD_COMPLETEDVIEWERSCOUNT))) : 0;

      int viewersCount = courseReportData.get(CourseConstants.PROPERTY_VIEWERSCOUNT) != null ?
          Integer.parseInt(decimalFormat.format((int) courseReportData.get(CourseConstants.PROPERTY_VIEWERSCOUNT))) : 0;

      int totalEnrollment = courseReportData.get(CourseConstants.PROPERTY_ENROLLMENTCOUNT) != null ?
          Integer.parseInt(decimalFormat.format((int) courseReportData.get(CourseConstants.PROPERTY_ENROLLMENTCOUNT))) : 0;

      int receivedCertificateCount = courseReportData.get(CourseConstants.PROPERTY_RECEIVEDCERTIFICATECOUNT) != null ?
          Integer.parseInt(decimalFormat.format((int) courseReportData.get(CourseConstants.PROPERTY_RECEIVEDCERTIFICATECOUNT))) : 0;

      int repeatedViewersCount = courseReportData.get(CourseConstants.PROPERTY_REPEATEDVIEWERSCOUNT) != null ?
          Integer.parseInt(decimalFormat.format((int) courseReportData.get(CourseConstants.PROPERTY_REPEATEDVIEWERSCOUNT))) : 0;

      String testScore = courseReportData.get(CourseConstants.PROPERTY_TEST_SCORE) != null ?
          (String) courseReportData.get(CourseConstants.PROPERTY_TEST_SCORE) : "0/0";

      String spentTimeOnTest = courseReportData.get(CourseConstants.PROPERTY_SPENT_TIME) != null ?
          (String) courseReportData.get(CourseConstants.PROPERTY_SPENT_TIME) : "00:00:00";
      boolean hasCertificate =
          courseReportData.get(CourseConstants.PROPERTY_HASCERTIFICATE) != null && (boolean) courseReportData.get(CourseConstants.PROPERTY_HASCERTIFICATE);

      Object[] row = { 0, name, category, state, totalEnrollment, viewersCount, status, completedViewers, hasCertificate ? "Тийм" : "Үгүй",
          receivedCertificateCount, repeatedViewersCount, testScore, spentTimeOnTest };

      data[index] = row;
    }

    return data;
  }
}

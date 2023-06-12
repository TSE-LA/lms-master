package mn.erin.lms.jarvis.domain.report.usecase;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

import mn.erin.common.excel.ExcelTableDataConverter;
import mn.erin.lms.jarvis.domain.report.usecase.dto.CourseReportDto;

/**
 * @author Byambajav
 */
public class OnlineCourseStatisticsExcelTableDataConverter implements ExcelTableDataConverter<List<CourseReportDto>>
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

      String name = (String) courseReportData.get(CourseConstants.PROPERTY_USERNAME);

      String group = (String) courseReportData.get(CourseConstants.PROPERTY_GROUP);

      String viewDate = (String) courseReportData.get(CourseConstants.PROPERTY_FIRST_LAUNCH);

      String lastViewData = (String) courseReportData.get(CourseConstants.PROPERTY_LAST_LAUNCH);

      String spentTime = courseReportData.get(CourseConstants.PROPERTY_SPENT_TIME) != null ?
          (String) courseReportData.get(CourseConstants.PROPERTY_SPENT_TIME) : "0/0";

      String spentTimeRatio = courseReportData.get(CourseConstants.REPORT_FIELD_SPENT_TIME_RATIO) != null ?
          (String) courseReportData.get(CourseConstants.REPORT_FIELD_SPENT_TIME_RATIO) : "00:00/00:00";

      int status = Integer.parseInt(decimalFormat.format((int) courseReportData.get(CourseConstants.REPORT_FIELD_STATUS)));

      int viewCount = courseReportData.get(CourseConstants.PROPERTY_VIEW_COUNT) != null ?
          Integer.parseInt(decimalFormat.format((int) courseReportData.get(CourseConstants.PROPERTY_VIEW_COUNT))) : 0;

      int testScore = courseReportData.get(CourseConstants.PROPERTY_TEST_SCORE) != null ?
          Integer.parseInt(decimalFormat.format((int) courseReportData.get(CourseConstants.PROPERTY_TEST_SCORE))) : 0;

      String spentTimeOnTest = courseReportData.get(CourseConstants.PROPERTY_SPENT_TIME_ON_TEST) != null ?
          (String) courseReportData.get(CourseConstants.PROPERTY_SPENT_TIME_ON_TEST) : "00:00:00";

      String certificate = courseReportData.get(CourseConstants.PROPERTY_CERTIFICATION) != null ?
          (String) courseReportData.get(CourseConstants.PROPERTY_CERTIFICATION) : " ";
      String survey = courseReportData.get(CourseConstants.PROPERTY_SURVEY) != null ?
          "Бөглөсөн" : "Бөглөөгүй";

      Object[] row = { 0, name, group, status, spentTimeRatio, viewDate, lastViewData, viewCount, spentTime, testScore, spentTimeOnTest, certificate, survey };

      data[index] = row;
    }

    return data;
  }
}

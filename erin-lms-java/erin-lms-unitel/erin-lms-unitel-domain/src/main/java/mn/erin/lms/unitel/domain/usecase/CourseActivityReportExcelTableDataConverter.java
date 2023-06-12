package mn.erin.lms.unitel.domain.usecase;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

import mn.erin.common.excel.ExcelTableDataConverter;
import mn.erin.lms.unitel.domain.usecase.dto.CourseReportDto;

/**
 * @author Erdenetulga
 */
public class CourseActivityReportExcelTableDataConverter implements ExcelTableDataConverter<List<CourseReportDto>>
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

      float status;
      if (courseReportData.get(CourseConstants.REPORT_FIELD_STATUS).getClass() == Float.class)
      {
        status = courseReportData.get(CourseConstants.REPORT_FIELD_STATUS) != null ?
            Float.parseFloat(decimalFormat.format((float) courseReportData.get(CourseConstants.REPORT_FIELD_STATUS))) : 0;
      }
      else
      {
        status = courseReportData.get(CourseConstants.REPORT_FIELD_STATUS) != null ?
            Float.parseFloat(decimalFormat.format((int) courseReportData.get(CourseConstants.REPORT_FIELD_STATUS))) : 0;
      }
      String convertedState;
      switch (state)
      {
      case "MANAGER":
        convertedState = "МЕНЕЖЕР";
        break;
      case "SUPERVISOR":
        convertedState = "АХЛАХ";
        break;
      default:
        convertedState = "СУРАЛЦАГЧ";
      }

      String spentTimeRatio = courseReportData.get(CourseConstants.REPORT_FIELD_SPENT_TIME_RATIO) != null ?
          (String) (courseReportData.get(CourseConstants.REPORT_FIELD_SPENT_TIME_RATIO)) :  CourseConstants.NO_SPENT_TIME_RATIO;

      int score = courseReportData.get(CourseConstants.PROPERTY_TEST_SCORE) != null ?
          Integer.parseInt(decimalFormat.format((int) courseReportData.get(CourseConstants.PROPERTY_TEST_SCORE))) : 0;

      String spentTimeOnTest = courseReportData.get(CourseConstants.PROPERTY_SPENT_TIME_ON_TEST) != null ?
          (String) courseReportData.get(CourseConstants.PROPERTY_SPENT_TIME_ON_TEST) : CourseConstants.NO_SPENT_TIME;
      String certificate = courseReportData.get(CourseConstants.PROPERTY_CERTIFICATION) != null &&
          courseReportData.get(CourseConstants.PROPERTY_CERTIFICATION) != "" ?
          (String) courseReportData.get(CourseConstants.PROPERTY_CERTIFICATION) : "Байхгүй";

      String survey = courseReportData.get(CourseConstants.PROPERTY_SURVEY).toString();

      int repeatedView = courseReportData.get(CourseConstants.PROPERTY_REPEATED_VIEW) != null ?
          Integer.parseInt(decimalFormat.format((int) courseReportData.get(CourseConstants.PROPERTY_REPEATED_VIEW))) : 0;

      String spentTime = courseReportData.get(CourseConstants.PROPERTY_SPENT_TIME) != null ?
          (String) courseReportData.get(CourseConstants.PROPERTY_SPENT_TIME) : CourseConstants.NO_SPENT_TIME;

      String firstLaunch = courseReportData.get(CourseConstants.PROPERTY_FIRST_LAUNCH) != null ?
          (String) courseReportData.get(CourseConstants.PROPERTY_FIRST_LAUNCH) : CourseConstants.NO_DATE;

      String lastLaunch = courseReportData.get(CourseConstants.PROPERTY_LAST_LAUNCH) != null ?
          (String) courseReportData.get(CourseConstants.PROPERTY_LAST_LAUNCH) : CourseConstants.NO_DATE;

      Object[] row = { 0, name, convertedState, status, score, spentTimeOnTest, certificate, survey, repeatedView, spentTime,
          firstLaunch, lastLaunch };

      data[index] = row;
    }

    return data;
  }
}

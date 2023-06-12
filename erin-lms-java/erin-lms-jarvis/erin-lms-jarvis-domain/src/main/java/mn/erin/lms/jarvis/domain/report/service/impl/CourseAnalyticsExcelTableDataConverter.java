package mn.erin.lms.jarvis.domain.report.service.impl;

import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import mn.erin.common.excel.ExcelTableDataConverter;
import mn.erin.lms.jarvis.domain.report.model.analytics.CourseAnalyticData;

/**
 * @author Erdenetulga
 */
public class CourseAnalyticsExcelTableDataConverter implements ExcelTableDataConverter<Map<String, Object>>
{
  private DecimalFormat decimalFormat = new DecimalFormat("#.##");
  static DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

  @Override
  @SuppressWarnings("unchecked")
  public Object[][] convert(Map<String, Object> analyticsResults)
  {

    int size = analyticsResults.size();
    Object[][] data = new Object[size][];
    int index = 0;

    for (Map.Entry<String, Object> entry : analyticsResults.entrySet())
    {
      String name = entry.getKey();
      CourseAnalyticData courseAnalyticData = (CourseAnalyticData) entry.getValue();
      String departmentName = courseAnalyticData.getDepartmentName() != null ?
          courseAnalyticData.getDepartmentName() : "";
      float status = courseAnalyticData.getStatus() != null ?
          courseAnalyticData.getStatus() : 0;
      int score = courseAnalyticData.getScore() != null ? courseAnalyticData.getScore() : 0;
      String initialLaunchDate = courseAnalyticData.getInitialLaunchDate() != null ?
          courseAnalyticData.getInitialLaunchDate().format(formatter) : "00:00:00";
      int interactionsCount = courseAnalyticData.getInteractionsCount() != null ?
          courseAnalyticData.getInteractionsCount() :  0;
      String spentTime = courseAnalyticData.getTotalTime() != null ?
          courseAnalyticData.getTotalTime() : "00:00:00";
      String receivedCertificateDate = courseAnalyticData.getReceivedCertificateDate() != null ?
          courseAnalyticData.getReceivedCertificateDate().format(formatter) : "00:00:00";
      String survey = (courseAnalyticData.getFeedback() != null && !courseAnalyticData.getFeedback().equals("unknown")) ?
          "Бөглөсөн" : "Бөглөөгүй";
      Object[] row = { 0, name, departmentName, status, initialLaunchDate, interactionsCount, spentTime, score, receivedCertificateDate, survey };
      data[index] = row;
      index++;
    }

    return data;
  }
}

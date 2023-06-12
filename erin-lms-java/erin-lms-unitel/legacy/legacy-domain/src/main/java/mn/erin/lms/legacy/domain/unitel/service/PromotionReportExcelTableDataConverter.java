package mn.erin.lms.legacy.domain.unitel.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import mn.erin.common.datetime.DateTimeUtils;
import mn.erin.common.excel.ExcelTableDataConverter;
import mn.erin.lms.legacy.domain.lms.usecase.report.CourseReportResult;
import mn.erin.lms.legacy.domain.unitel.PromotionConstants;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class PromotionReportExcelTableDataConverter implements ExcelTableDataConverter<List<CourseReportResult>>
{
  @Override
  @SuppressWarnings("unchecked")
  public Object[][] convert(List<CourseReportResult> reportResults)
  {
    int reportDataSize = reportResults.size();
    Object[][] data = new Object[reportDataSize][];

    for (int index = 0; index < reportDataSize; index++)
    {
      CourseReportResult report = reportResults.get(index);
      Map<String, Object> promotionProperties = report.getCourseProperties();
      Map<String, Object> promotionReportData = report.getReportData();

      String code = (String) promotionProperties.get(PromotionConstants.PROPERTY_CODE);
      String name = report.getCourseName();
      Date createdDate = report.getCourseCreatedDate();
      String author = report.getAuthorName();

      double status = promotionReportData.get(PromotionConstants.REPORT_FIELD_STATUS) != null ?
          Double.parseDouble((String) promotionReportData.get(PromotionConstants.REPORT_FIELD_STATUS)) : 0;

      int views = promotionReportData.containsKey(PromotionConstants.REPORT_FIELD_VIEWS) ?
          (int) promotionReportData.get(PromotionConstants.REPORT_FIELD_VIEWS) : 0;

      boolean hasTest = promotionProperties.containsKey(PromotionConstants.PROPERTY_HAS_TEST) &&
          (boolean) promotionProperties.get(PromotionConstants.PROPERTY_HAS_TEST);

      int questionCount = promotionReportData.get(PromotionConstants.REPORT_FIELD_QUESTIONS_COUNT) != null ?
          (int) promotionReportData.get(PromotionConstants.REPORT_FIELD_QUESTIONS_COUNT) : 0;

      double score = promotionReportData.get(PromotionConstants.REPORT_FIELD_SCORE) != null ?
          Double.parseDouble((String) promotionReportData.get(PromotionConstants.REPORT_FIELD_SCORE)) : 0;

      int totalEnrollment = promotionReportData.get(PromotionConstants.REPORT_FIELD_TOTAL_ENROLLMENT) != null ?
          (int) promotionReportData.get(PromotionConstants.REPORT_FIELD_TOTAL_ENROLLMENT) : 0;

      int feedback = promotionReportData.containsKey(PromotionConstants.REPORT_FIELD_FEEDBACK) ?
          (int) promotionReportData.get(PromotionConstants.REPORT_FIELD_FEEDBACK) : 0;

      Object[] row = { 0, code, name, DateTimeUtils.toIsoString(createdDate, DateTimeUtils.SHORT_ISO_DATE_FORMAT), author, status, views,
          hasTest ? "Тийм" : "Үгүй", questionCount, score, totalEnrollment, feedback };

      data[index] = row;
    }

    return data;
  }
}

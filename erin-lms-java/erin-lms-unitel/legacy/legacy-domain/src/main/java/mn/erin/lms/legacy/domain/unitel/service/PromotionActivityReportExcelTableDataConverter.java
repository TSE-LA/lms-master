package mn.erin.lms.legacy.domain.unitel.service;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

import mn.erin.common.excel.ExcelTableDataConverter;
import mn.erin.lms.legacy.domain.lms.usecase.report.CourseReportResult;
import mn.erin.lms.legacy.domain.unitel.PromotionConstants;

/**
 * @author Erdenetulga
 */
public class PromotionActivityReportExcelTableDataConverter implements ExcelTableDataConverter<List<CourseReportResult>>
{
  private DecimalFormat decimalFormat = new DecimalFormat("#.##");

  @Override
  @SuppressWarnings("unchecked")
  public Object[][] convert(List<CourseReportResult> reportResults)
  {
    int reportDataSize = reportResults.size();
    Object[][] data = new Object[reportDataSize][];

    for (int index = 0; index < reportDataSize; index++)
    {
      CourseReportResult report = reportResults.get(index);
      Map<String, Object> promotionReportData = report.getReportData();

      String name = report.getCourseName() != null ?
          report.getCourseName() : "";

      String promoCategory = report.getCategoryName() != null ?
          report.getCategoryName() : "";

      float status = promotionReportData.get(PromotionConstants.REPORT_FIELD_STATUS) != null ?
          Float.parseFloat(decimalFormat.format((float) promotionReportData.get(PromotionConstants.REPORT_FIELD_STATUS))) : 0;

      int score = promotionReportData.get(PromotionConstants.REPORT_FIELD_SCORE) != null ?
          Integer.parseInt(decimalFormat.format((int) promotionReportData.get(PromotionConstants.REPORT_FIELD_SCORE))) : 0;

      int feedback = promotionReportData.containsKey(PromotionConstants.REPORT_FIELD_FEEDBACK) ?
          Integer.parseInt(decimalFormat.format((int) promotionReportData.get(PromotionConstants.REPORT_FIELD_FEEDBACK))) : 0;

      int views = promotionReportData.containsKey(PromotionConstants.REPORT_FIELD_VIEWS) ?
          Integer.parseInt(decimalFormat.format((int) promotionReportData.get(PromotionConstants.REPORT_FIELD_VIEWS))) : 0;

      String spentTime = promotionReportData.get(PromotionConstants.REPORT_FIELD_FIRST_LAUNCH_DATE) != null ?
          (String) promotionReportData.get(PromotionConstants.REPORT_FIELD_SPENT_TIME) : "00:00:00";

      String firstLaunchDate = promotionReportData.get(PromotionConstants.REPORT_FIELD_FIRST_LAUNCH_DATE) != null ?
          (String) promotionReportData.get(PromotionConstants.REPORT_FIELD_FIRST_LAUNCH_DATE) : "";

      String lastLaunchDate = promotionReportData.get(PromotionConstants.REPORT_FIELD_LAST_LAUNCH_DATE) != null ?
          (String) promotionReportData.get(PromotionConstants.REPORT_FIELD_LAST_LAUNCH_DATE) : "";

      Object[] row = { 0, name, promoCategory, status, score, feedback, views, spentTime, firstLaunchDate, lastLaunchDate };

      data[index] = row;
    }

    return data;
  }
}

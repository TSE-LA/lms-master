package mn.erin.lms.unitel.domain.usecase;

import java.text.DecimalFormat;
import java.util.List;

import mn.erin.common.excel.ExcelTableDataConverter;
import mn.erin.lms.unitel.domain.usecase.dto.ClassroomActivityReportDto;

public class ClassroomCourseActivityReportExcelTableDataConverter implements ExcelTableDataConverter<List<ClassroomActivityReportDto>>
{
  private DecimalFormat decimalFormat = new DecimalFormat("#.##");

  @Override
  public Object[][] convert(List<ClassroomActivityReportDto> reportResults)
  {
    int reportDataSize = reportResults.size();
    Object[][] data = new Object[reportDataSize][];

    for (int index = 0; index < reportDataSize; index++)
    {
      ClassroomActivityReportDto report = reportResults.get(index);

      String name = report.getName();
      String courseType = report.getCourseType();
      String present = !report.isPresent() ? "Хамрагдаагүй" : "Хамрагдсан";;
      int testScore = report.getTestScore();
      String certificate = report.getCertificate() != null ? "Сертификаттай" : "Сертификатгүй";
      String teacher = report.getTeacher();
      String startTime = report.getStartTime();
      String endTime = report.getEndTime();
      String date = report.getDate();
      Object[] row = { 0, name, courseType, present, Integer.toString(testScore),certificate,teacher,startTime,endTime,date };

      data[index] = row;
    }

    return data;
  }
}

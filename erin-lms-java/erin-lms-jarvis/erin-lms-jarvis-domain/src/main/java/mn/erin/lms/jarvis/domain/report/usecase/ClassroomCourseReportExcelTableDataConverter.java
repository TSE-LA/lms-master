package mn.erin.lms.jarvis.domain.report.usecase;

import java.util.List;

import mn.erin.common.excel.ExcelTableDataConverter;
import mn.erin.lms.base.domain.usecase.course.dto.CourseDto;

/**
 * @author Erdenetulga
 */
public class ClassroomCourseReportExcelTableDataConverter implements ExcelTableDataConverter<List<CourseDto>>
{
  @Override
  @SuppressWarnings("unchecked")
  public Object[][] convert(List<CourseDto> reportResults)
  {
    int reportDataSize = reportResults.size();
    Object[][] data = new Object[reportDataSize][];

    for (int index = 0; index < reportDataSize; index++)
    {
      CourseDto report = reportResults.get(index);

      String name = report.getTitle();
      String category = report.getCourseCategoryName();
      String date = report.getProperties().get("date") != null ? report.getProperties().get("date") : "00:00:00";
      String teacher = report.getProperties().get("teacher") != null ? report.getProperties().get("teacher") : "";
      int maxEnrollment = report.getProperties().get("maxEnrollmentCount") != null ?
          Integer.parseInt(report.getProperties().get("maxEnrollmentCount")) : 0;
      String address = report.getProperties().get("address") != null ? report.getProperties().get("address") : "";
      int enrollmentCount = report.getProperties().get("enrollmentCount") != null ?
          Integer.parseInt(report.getProperties().get("enrollmentCount")) : 0;
      String courseGroup = report.getBelongingDepartmentName() != null ? report.getBelongingDepartmentName() : "";
      String durationTime = report.getDurationTime() != null ? report.getDurationTime() : "";
      Object[] row = { 0, name, category, date, teacher, maxEnrollment, address, enrollmentCount, courseGroup, durationTime };

      data[index] = row;
    }

    return data;
  }
}

package mn.erin.lms.legacy.domain.lms.usecase.report;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.legacy.domain.lms.model.report.CourseReport;
import mn.erin.lms.legacy.domain.lms.service.CourseActivityReportService;
import mn.erin.lms.legacy.domain.unitel.PromotionConstants;

/**
 * @author Munkh
 */
public class GenerateCourseActivityReports implements UseCase<ReportFilter, List<CourseReportResult>>
{
  private final CourseActivityReportService courseActivityReportService;

  public GenerateCourseActivityReports(CourseActivityReportService courseActivityReportService)
  {
    this.courseActivityReportService = Objects.requireNonNull(courseActivityReportService, "CourseActivityReportService cannot be null!");
  }

  @Override
  public List<CourseReportResult> execute(ReportFilter filter) throws UseCaseException
  {
    Validate.notNull(filter, "Input is required!");

    List<CourseReport> courseReports;

    if (!StringUtils.isBlank(filter.getLearnerId()) && filter.getCoursePropertyFilter() != null)
    {
      courseReports = courseActivityReportService.generateCourseActivityReports(filter.getLearnerId(), filter.getCoursePropertyFilter());
    }
    else
    {
      return new ArrayList<>();
    }

    List<CourseReportResult> result = new ArrayList<>();

    for (CourseReport courseReport : courseReports)
    {

      String courseCategory = (String) courseReport.getReportData().get(PromotionConstants.REPORT_FIELD_PROMO_CATEGORY);
      result.add(new CourseReportResult.Builder(courseReport.getRootEntity().getCourseDetail().getTitle())
          .withId(courseReport.getRootEntity().getCourseId().getId())
          .withAuthor(courseReport.getRootEntity().getAuthorId().getId())
          .withCategory(courseCategory != null ? courseCategory : "төрөл зөрсөн")
          .createdAt(courseReport.getRootEntity().getCourseDetail().getCreatedDate())
          .havingCourseProperties(courseReport.getRootEntity().getCourseDetail().getProperties())
          .havingReportData(courseReport.getReportData())
          .build());
    }
    return result;
  }
}

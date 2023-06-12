package mn.erin.lms.legacy.infrastructure.lms.rest.report;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.inject.Inject;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import mn.erin.common.datetime.DateTimeUtils;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.infrastucture.rest.common.response.RestResponse;
import mn.erin.infrastucture.rest.common.response.RestResult;
import mn.erin.lms.legacy.domain.lms.service.CourseActivityReportService;
import mn.erin.lms.legacy.domain.lms.usecase.report.CourseReportResult;
import mn.erin.lms.legacy.domain.lms.usecase.report.DownloadCourseActivityReports;
import mn.erin.lms.legacy.domain.lms.usecase.report.GenerateCourseActivityReports;
import mn.erin.lms.legacy.domain.lms.usecase.report.ReportFilter;
/**
 *
 * @author Munkh
 */
@Api("Promotion Activity Report")
@RequestMapping(value = "/legacy/lms/course-activity")
public class CourseActivityReportRestApi
{
  private static final Logger LOGGER = LoggerFactory.getLogger(CourseActivityReportRestApi.class);

  @Inject
  private CourseActivityReportService courseActivityReportService;


  @ApiOperation("Get Promotion Activity Report")
  @GetMapping
  public ResponseEntity<RestResult> readAll(
      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
      @RequestParam String departmentId,
      @RequestParam String userName,
      @RequestParam(required = false) String categoryId
  )
  {
    Map<String, Object> properties = new HashMap<>();

    Date startDateFilter = Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    Date endDateFilter = Date.from(endDate.atTime(23,59,59).atZone(ZoneId.systemDefault()).toInstant());


    properties.put("startDate", startDateFilter);
    properties.put("endDate", endDateFilter);

    ReportFilter reportFilter = new ReportFilter(startDateFilter, endDateFilter);
    reportFilter.setGroupId(departmentId);
    reportFilter.setLearnerId(userName);

    Optional<String> optionalCategory = Optional.ofNullable(categoryId);
    properties.remove("categoryName");
    properties.remove("groupId");
    Optional<Map<String, Object>> optionalState = Optional.of(properties);
    optionalCategory.ifPresent(reportFilter::setCategory);
    optionalState.ifPresent(reportFilter::setProperties);

    GenerateCourseActivityReports generateCourseActivityReports = new GenerateCourseActivityReports(courseActivityReportService);

    try
    {
      List<CourseReportResult> report = generateCourseActivityReports.execute(reportFilter);
      return RestResponse.success(report);
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Download Online Course Activity Table Excel Report")
  @GetMapping("/download-excel")
  public ResponseEntity read(
      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
      @RequestParam String departmentId,
      @RequestParam String userName,
      @RequestParam(required = false) String categoryId
  )
  {
    Map<String, Object> properties = new HashMap<>();

    Date startDateFilter = Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    Date endDateFilter = Date.from(endDate.atTime(23,59,59).atZone(ZoneId.systemDefault()).toInstant());

    properties.put("startDate", startDateFilter);
    properties.put("endDate", endDateFilter);

    ReportFilter reportFilter = new ReportFilter(startDateFilter, endDateFilter);
    reportFilter.setGroupId(departmentId);
    reportFilter.setLearnerId(userName);

    Optional<String> optionalCategory = Optional.ofNullable(categoryId);
    properties.remove("categoryName");
    properties.remove("groupId");
    Optional<Map<String, Object>> optionalState = Optional.of(properties);
    optionalCategory.ifPresent(reportFilter::setCategory);
    optionalState.ifPresent(reportFilter::setProperties);
    GenerateCourseActivityReports generateCourseActivityReports = new GenerateCourseActivityReports(courseActivityReportService);
    DownloadCourseActivityReports downloadCourseActivityReports = new DownloadCourseActivityReports();
    List<CourseReportResult> report;
    String currentFormattedDate = DateTimeUtils.getCurrentLocalDateTime(DateTimeUtils.LONG_ISO_DATE_FORMAT);

    try
    {
      report = generateCourseActivityReports.execute(reportFilter);
      byte[] reportData = downloadCourseActivityReports.execute(report);
      ByteArrayResource resource = new ByteArrayResource(reportData);
        return ResponseEntity.ok()
            .contentLength(resource.contentLength())
            .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
            .header("Content-Disposition", "attachment; filename=\"Promotion-Activity-Report" + currentFormattedDate + ".xlsx\"")
            .body(resource);
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }
}

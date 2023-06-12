package mn.erin.lms.jarvis.rest.api;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import mn.erin.common.datetime.DateTimeUtils;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.infrastucture.rest.common.response.RestResponse;
import mn.erin.infrastucture.rest.common.response.RestResult;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.rest.api.BaseLmsRestApi;
import mn.erin.lms.jarvis.domain.report.usecase.DownloadOnlineCourseStatistics;
import mn.erin.lms.jarvis.domain.report.usecase.analytics.GenerateSurveyExcelReport;
import mn.erin.lms.jarvis.domain.report.usecase.analytics.GetCourseAnalytics;
import mn.erin.lms.jarvis.domain.report.usecase.analytics.GetCreatedCourseCount;
import mn.erin.lms.jarvis.domain.report.usecase.analytics.GetEnrolledCourseCount;
import mn.erin.lms.jarvis.domain.report.usecase.analytics.dto.CourseAnalyticsDto;
import mn.erin.lms.jarvis.domain.report.usecase.analytics.dto.CourseCountDto;
import mn.erin.lms.jarvis.domain.report.usecase.analytics.dto.GetCourseAnalyticsInput;
import mn.erin.lms.jarvis.domain.report.usecase.analytics.dto.GetCourseCountInput;
import mn.erin.lms.jarvis.domain.report.usecase.dto.CourseReportDto;
import mn.erin.lms.jarvis.domain.service.JarvisLmsServiceRegistry;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Api("Course Analytics REST API")
@RequestMapping(value = "/lms/courses", name = "Provides LMS course analytics features")
@RestController
public class CourseAnalyticsRestApi extends BaseLmsRestApi
{
  private static final String EXCEL_MEDIA_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
  private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

  CourseAnalyticsRestApi(LmsRepositoryRegistry lmsRepositoryRegistry,
      LmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
  }

  @ApiOperation("Fetches course analytic data by course ID")
  @GetMapping("/{courseId}/analytics")
  public ResponseEntity<RestResult> readById(
      @PathVariable String courseId,
      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
      @RequestParam String departmentId
  )
  {
    GetCourseAnalytics getCourseAnalytics = new GetCourseAnalytics((getLmsServiceRegistry()).getCourseAnalyticsService());
    GetCourseAnalyticsInput input = new GetCourseAnalyticsInput(courseId, departmentId, startDate, endDate);

    try
    {
      CourseAnalyticsDto output = getCourseAnalytics.execute(input);
      return RestResponse.success(output);
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Download course statistics report excel")
  @GetMapping("/{courseId}/survey/download")
  @ResponseBody
  public ResponseEntity getSurveyExcelReport(
      @PathVariable String courseId,
      @RequestParam @DateTimeFormat(pattern = "yyyyMMdd") LocalDate startDate,
      @RequestParam @DateTimeFormat(pattern = "yyyyMMdd") LocalDate endDate,
      @RequestParam String departmentId
  )
  {
    GetCourseAnalyticsInput input = new GetCourseAnalyticsInput(courseId, departmentId, startDate, endDate);
    GenerateSurveyExcelReport generateSurveyExcelReport = new GenerateSurveyExcelReport(getLmsServiceRegistry().getCourseAnalyticsService());

    try
    {
      byte[] surveyReportData = generateSurveyExcelReport.execute(input);
      ByteArrayResource resource = new ByteArrayResource(surveyReportData);
      LocalDateTime dateTime = LocalDateTime.now();
      String formattedDate = dateTime.format(dateFormatter);
      return ResponseEntity.ok()
          .contentLength(resource.contentLength())
          .contentType(MediaType.parseMediaType(EXCEL_MEDIA_TYPE))
          .header("Content-Disposition", "attachment; filename=\"Online-Course-Survey" + formattedDate + ".xlsx\"")
          .body(resource);
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  private JarvisLmsServiceRegistry getLmsServiceRegistry()
  {
    return (JarvisLmsServiceRegistry) lmsServiceRegistry;
  }

  @ApiOperation("Download course statistics report excel")
  @GetMapping("/{courseId}/survey/download-one-row")
  @ResponseBody
  public ResponseEntity getSurveyInOneRowExcelReport(
      @PathVariable String courseId,
      @RequestParam @DateTimeFormat(pattern = "yyyyMMdd") LocalDate startDate,
      @RequestParam @DateTimeFormat(pattern = "yyyyMMdd") LocalDate endDate,
      @RequestParam String departmentId,
      @RequestParam String userId
  )
  {
    GetCourseAnalyticsInput input = new GetCourseAnalyticsInput(courseId, departmentId, startDate, endDate, userId);
    GenerateSurveyExcelReport generateSurveyExcelReport = new GenerateSurveyExcelReport((getLmsServiceRegistry()).getCourseAnalyticsService());

    try
    {
      byte[] surveyReportData = generateSurveyExcelReport.execute(input);
      ByteArrayResource resource = new ByteArrayResource(surveyReportData);
      LocalDateTime dateTime = LocalDateTime.now();
      String formattedDate = dateTime.format(dateFormatter);
      return ResponseEntity.ok()
          .contentLength(resource.contentLength())
          .contentType(MediaType.parseMediaType(EXCEL_MEDIA_TYPE))
          .header("Content-Disposition", "attachment; filename=\"Online-Course-Survey" + formattedDate + ".xlsx\"")
          .body(resource);
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Download course statistics report excel")
  @PostMapping("/analytics/download")
  public ResponseEntity download(@RequestBody List<CourseReportDto> body)
  {
    DownloadOnlineCourseStatistics downloadCourseReports = new DownloadOnlineCourseStatistics(lmsRepositoryRegistry, lmsServiceRegistry);
    String currentFormattedDate = DateTimeUtils.getCurrentLocalDateTime(DateTimeUtils.LONG_ISO_DATE_FORMAT);
    try
    {
      byte[] reportData = downloadCourseReports.execute(body);
      ByteArrayResource resource = new ByteArrayResource(reportData);
      return ResponseEntity.ok()
          .contentLength(resource.contentLength())
          .contentType(MediaType.parseMediaType(EXCEL_MEDIA_TYPE))
          .header("Content-Disposition", "attachment; filename=\"Online-Course-Report-" + currentFormattedDate + ".xlsx\"")
          .body(resource);
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError("Failed to generate course report!");
    }
  }

  @ApiOperation("Gets created course count")
  @GetMapping("/created-course-count")
  public ResponseEntity<RestResult> read(
      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
      @RequestParam String parentCategoryId,
      @RequestParam Set<String> courseTypes,
      @RequestParam(required = false) String authorId
  )
  {
    GetCreatedCourseCount getCreatedCourseCount = new GetCreatedCourseCount(lmsRepositoryRegistry, lmsServiceRegistry);
    GetCourseCountInput input = new GetCourseCountInput(startDate, endDate, parentCategoryId);
    input.setAuthorId(authorId);
    input.setCourseTypes(courseTypes);

    try
    {
      List<CourseCountDto> output = getCreatedCourseCount.execute(input);
      return RestResponse.success(output);
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Gets enrolled course count")
  @GetMapping("/enrolled-course-count")
  public ResponseEntity<RestResult> read(
      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
      @RequestParam String parentCategoryId
  )
  {
    GetEnrolledCourseCount getEnrolledCourseCount = new GetEnrolledCourseCount(lmsRepositoryRegistry, lmsServiceRegistry);
    GetCourseCountInput input = new GetCourseCountInput(startDate, endDate, parentCategoryId);

    try
    {
      List<CourseCountDto> output = getEnrolledCourseCount.execute(input);
      return RestResponse.success(output);
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }
}

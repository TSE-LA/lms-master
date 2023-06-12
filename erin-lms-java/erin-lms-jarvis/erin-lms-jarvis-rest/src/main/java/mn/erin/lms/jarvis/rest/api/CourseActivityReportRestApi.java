package mn.erin.lms.jarvis.rest.api;

import java.time.LocalDate;
import java.util.List;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import mn.erin.common.datetime.DateTimeUtils;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.infrastucture.rest.common.response.RestResponse;
import mn.erin.infrastucture.rest.common.response.RestResult;
import mn.erin.lms.base.domain.repository.ClassroomCourseAttendanceRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.rest.api.BaseLmsRestApi;
import mn.erin.lms.jarvis.domain.report.usecase.DownloadClassroomCourseActivityReports;
import mn.erin.lms.jarvis.domain.report.usecase.DownloadCourseActivityReports;
import mn.erin.lms.jarvis.domain.report.usecase.GenerateClassroomActivityReports;
import mn.erin.lms.jarvis.domain.report.usecase.GenerateCourseActivityReports;
import mn.erin.lms.jarvis.domain.report.usecase.dto.ClassroomActivityReportDto;
import mn.erin.lms.jarvis.domain.report.usecase.dto.CourseReportDto;
import mn.erin.lms.jarvis.domain.report.usecase.dto.ReportFilter;
import mn.erin.lms.jarvis.domain.service.JarvisLmsServiceRegistry;

/**
 * @author Munkh
 */
@Api("Course Report REST API")
@RequestMapping(value = "/lms/course-activity", name = "Provides LMS course report features")
@RestController
public class CourseActivityReportRestApi extends BaseLmsRestApi
{
  private static final Logger LOGGER = LoggerFactory.getLogger(CourseActivityReportRestApi.class);
  private final ClassroomCourseAttendanceRepository classroomCourseAttendanceRepository;

  public CourseActivityReportRestApi(LmsRepositoryRegistry lmsRepositoryRegistry,
      LmsServiceRegistry lmsServiceRegistry, ClassroomCourseAttendanceRepository classroomCourseAttendanceRepository)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
    this.classroomCourseAttendanceRepository = classroomCourseAttendanceRepository;
  }

  @ApiOperation("Generates course activity reports")
  @GetMapping
  public ResponseEntity<RestResult> readAll(
      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
      @RequestParam String departmentId,
      @RequestParam String reportType,
      @RequestParam String userName,
      @RequestParam(required = false) String categoryId,
      @RequestParam(required = false) String courseType
  )
  {
    GenerateCourseActivityReports generateCourseActivityReports = new GenerateCourseActivityReports(lmsRepositoryRegistry,
        (JarvisLmsServiceRegistry) lmsServiceRegistry);
    ReportFilter filter = new ReportFilter(departmentId, startDate, endDate);
    filter.setCategoryId(categoryId);
    filter.setCourseType(courseType);
    filter.setReportType(reportType);
    filter.setLearnerId(userName);
    try
    {
      List<CourseReportDto> result = generateCourseActivityReports.execute(filter);
      return RestResponse.success(result);
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Generates classroom course activity reports")
  @GetMapping("/classroom")
  public ResponseEntity<RestResult> getClassroomActivityReports(
      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
      @RequestParam String departmentId,
      @RequestParam String learnerId
  )
  {
    ReportFilter filter = new ReportFilter(departmentId, startDate, endDate);
    filter.setLearnerId(learnerId);
    GenerateClassroomActivityReports generateClassroomActivityReports = new GenerateClassroomActivityReports(lmsRepositoryRegistry,
        lmsServiceRegistry, classroomCourseAttendanceRepository);
    try
    {

      return RestResponse.success(generateClassroomActivityReports.execute(filter));
    }
    catch (UseCaseException e)
    {
      return RestResponse.badRequest(e.getMessage());
    }
  }

  @ApiOperation("Download Online Course Activity Table Excel Report")
  @PostMapping("/download-excel")
  public ResponseEntity download(
      @RequestBody List<CourseReportDto> body
  )
  {
    DownloadCourseActivityReports downloadCourseActivityReports = new DownloadCourseActivityReports(lmsRepositoryRegistry, lmsServiceRegistry);
    String currentFormattedDate = DateTimeUtils.getCurrentLocalDateTime(DateTimeUtils.LONG_ISO_DATE_FORMAT);

    try
    {
      byte[] reportData = downloadCourseActivityReports.execute(body);
      ByteArrayResource resource = new ByteArrayResource(reportData);
      return ResponseEntity.ok()
          .contentLength(resource.contentLength())
          .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
          .header("Content-Disposition", "attachment; filename=\"Online-Course-Activity-Report" + currentFormattedDate + ".xlsx\"")
          .body(resource);
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Download Online Course Activity Table Excel Report")
  @GetMapping("/download-excel-classroom")
  public ResponseEntity read(
      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
      @RequestParam String departmentId,
      @RequestParam String userName
  )
  {
    ReportFilter filter = new ReportFilter(departmentId, startDate, endDate);
    filter.setLearnerId(userName);
    GenerateClassroomActivityReports generateClassroomActivityReports = new GenerateClassroomActivityReports(lmsRepositoryRegistry,
        lmsServiceRegistry, classroomCourseAttendanceRepository);
    DownloadClassroomCourseActivityReports downloadClassroomCourseActivityReports = new DownloadClassroomCourseActivityReports(lmsRepositoryRegistry,
        lmsServiceRegistry);
    String currentFormattedDate = DateTimeUtils.getCurrentLocalDateTime(DateTimeUtils.LONG_ISO_DATE_FORMAT);

    try
    {
      List<ClassroomActivityReportDto> result = generateClassroomActivityReports.execute(filter);
      byte[] reportData = downloadClassroomCourseActivityReports.execute(result);
      ByteArrayResource resource = new ByteArrayResource(reportData);
        return ResponseEntity.ok()
            .contentLength(resource.contentLength())
            .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
            .header("Content-Disposition", "attachment; filename=\"Online-Course-Activity-Report" + currentFormattedDate + ".xlsx\"")
            .body(resource);
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }
}

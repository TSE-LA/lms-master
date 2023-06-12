package mn.erin.lms.unitel.rest.api;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import mn.erin.lms.base.aim.LmsDepartmentService;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.course.dto.CourseDto;
import mn.erin.lms.base.rest.api.BaseLmsRestApi;
import mn.erin.lms.unitel.domain.service.UnitelLmsServiceRegistry;
import mn.erin.lms.unitel.domain.usecase.DownloadClassroomCourseReports;
import mn.erin.lms.unitel.domain.usecase.DownloadCourseReports;
import mn.erin.lms.unitel.domain.usecase.GenerateClassroomCourseReports;
import mn.erin.lms.unitel.domain.usecase.GenerateCourseReports;
import mn.erin.lms.unitel.domain.usecase.dto.CourseReportDto;
import mn.erin.lms.unitel.domain.usecase.dto.ReportFilter;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Api("Course Report REST API")
@RequestMapping(value = "/lms/courses/report", name = "Provides LMS course report features")
@RestController
public class CourseReportRestApi extends BaseLmsRestApi
{
  private final LmsDepartmentService departmentService;

  private static final Logger LOGGER = LoggerFactory.getLogger(CourseReportRestApi.class);

  CourseReportRestApi(LmsRepositoryRegistry lmsRepositoryRegistry, LmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
    this.departmentService = lmsServiceRegistry.getDepartmentService();
  }

  @ApiOperation("Generates course reports")
  @GetMapping
  public ResponseEntity<RestResult> readAll(
      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
      @RequestParam String departmentId,
      @RequestParam(required = false) String categoryId,
      @RequestParam(required = false) String courseType
  )
  {
    GenerateCourseReports generateCourseReports = new GenerateCourseReports(lmsRepositoryRegistry, (UnitelLmsServiceRegistry) lmsServiceRegistry);
    ReportFilter filter = new ReportFilter(departmentId, startDate, endDate);
    filter.setCategoryId(categoryId);
    filter.setCourseType(courseType);
    try
    {
      List<CourseReportDto> result = generateCourseReports.execute(filter);
      return RestResponse.success(result);
    }
    catch (Exception e)
    {
      LOGGER.error(e.getMessage(), e);
      return RestResponse.internalError("failed to get report data");
    }
  }

  @ApiOperation("Generates course reports")
  @GetMapping("/classroom")
  public ResponseEntity<RestResult> readAllClassroom(
      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
      @RequestParam String departmentId,
      @RequestParam(required = false) String categoryId
  )
  {
    GenerateClassroomCourseReports generateCourseReports = new GenerateClassroomCourseReports(lmsRepositoryRegistry,
        (UnitelLmsServiceRegistry) lmsServiceRegistry);
    ReportFilter filter = new ReportFilter(departmentId, startDate, endDate);
    filter.setCategoryId(categoryId);
    try
    {
      List<CourseDto> result = generateCourseReports.execute(filter);
      return RestResponse.success(result);
    }
    catch (Exception e)
    {
      LOGGER.error(e.getMessage(), e);
      return RestResponse.internalError("failed to get report data");
    }
  }



  @ApiOperation("Download Online course report")
  @PostMapping("/download-online-course-report")
  public ResponseEntity download(
      @RequestBody List<CourseReportDto> body
  )
  {
    DownloadCourseReports downloadCourseReports = new DownloadCourseReports(lmsRepositoryRegistry, lmsServiceRegistry);
    String currentFormattedDate = DateTimeUtils.getCurrentLocalDateTime(DateTimeUtils.LONG_ISO_DATE_FORMAT);
    try
    {
      byte[] reportData = downloadCourseReports.execute(body);
      ByteArrayResource resource = new ByteArrayResource(reportData);
      return ResponseEntity.ok()
          .contentLength(resource.contentLength())
          .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
          .header("Content-Disposition", "attachment; filename=\"Online-Course-Report-" + currentFormattedDate + ".xlsx\"")
          .body(resource);
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError("Failed to generate course report!");
    }
  }

  @ApiOperation("Download Classroom course Table Excel Report")
  @GetMapping("/classroom-course-report-excel")
  public ResponseEntity readClassroom(
      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
      @RequestParam String departmentId,
      @RequestParam(required = false) String categoryId,
      @RequestParam(required = false) List<String> durationList
  )
  {
    GenerateClassroomCourseReports generateCourseReports = new GenerateClassroomCourseReports(lmsRepositoryRegistry,
        (UnitelLmsServiceRegistry) lmsServiceRegistry);
    DownloadClassroomCourseReports downloadCourseReports = new DownloadClassroomCourseReports(lmsRepositoryRegistry, lmsServiceRegistry);
    List<CourseDto> reportResults;
    ReportFilter filter = new ReportFilter(departmentId, startDate, endDate);
    filter.setCategoryId(categoryId);
    String currentFormattedDate = DateTimeUtils.getCurrentLocalDateTime(DateTimeUtils.LONG_ISO_DATE_FORMAT);
    try
    {
      Map<String, String> durationDtoList = new HashMap<>();
      for (String restDuration : durationList)
      {
        String[] split = restDuration.split("-", 2);
        durationDtoList.put(split[0], split[1]);
      }

      reportResults = generateCourseReports.execute(filter);
      for (CourseDto reportResult : reportResults)
      {
        String departmentName = departmentService.getDepartmentName(reportResult.getBelongingDepartmentId());
        reportResult.setBelongingDepartmentName(departmentName);

        String durationTime = durationDtoList.get(reportResult.getId());
        if (durationTime != null)
        {
          reportResult.setDurationTime(durationTime);
        }
      }
      byte[] reportData = downloadCourseReports.execute(reportResults);
      ByteArrayResource resource = new ByteArrayResource(reportData);
      return ResponseEntity.ok()
          .contentLength(resource.contentLength())
          .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
          .header("Content-Disposition", "attachment; filename=\"Classroom-Course-Report-" + currentFormattedDate + ".xlsx\"")
          .body(resource);
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError("Failed to generate course report!");
    }
  }
}

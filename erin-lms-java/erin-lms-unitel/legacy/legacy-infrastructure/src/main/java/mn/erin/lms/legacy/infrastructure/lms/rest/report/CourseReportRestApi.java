/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.infrastructure.lms.rest.report;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.inject.Inject;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.infrastucture.rest.common.response.RestResponse;
import mn.erin.lms.legacy.domain.lms.repository.CourseCategoryRepository;
import mn.erin.lms.legacy.domain.lms.service.CourseReportService;
import mn.erin.lms.legacy.domain.lms.usecase.report.CourseReportResult;
import mn.erin.lms.legacy.domain.lms.usecase.report.GenerateCourseReports;
import mn.erin.lms.legacy.domain.lms.usecase.report.ReportFilter;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Api("Course Report")
@RequestMapping(value = "/legacy/lms/course-report", name = "Provides 'ERIN' LMS reporting features")
public class CourseReportRestApi
{
  @Inject
  private CourseReportService courseReportService;

  @Inject
  private CourseCategoryRepository courseCategoryRepository;

  @ApiOperation("Generate course reports")
  @GetMapping
  public ResponseEntity read
      (
          @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
          @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
          @RequestParam String groupId,
          @RequestParam(required = false) String categoryName,
          @RequestParam(required = false) Map<String, Object> properties
      )
  {
    Date startDateFilter = Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    Date endDateFilter = Date.from(endDate.atTime(23,59,59).atZone(ZoneId.systemDefault()).toInstant());

    properties.put("startDate", startDateFilter);
    properties.put("endDate", endDateFilter);

    ReportFilter reportFilter = new ReportFilter(startDateFilter, endDateFilter);
    reportFilter.setGroupId(groupId);
    Optional<String> optionalCategory = Optional.ofNullable(categoryName);
    properties.remove("categoryName");
    properties.remove("groupId");
    Optional<Map<String, Object>> optionalState = Optional.ofNullable(properties);
    optionalCategory.ifPresent(reportFilter::setCategory);
    optionalState.ifPresent(reportFilter::setProperties);

    GenerateCourseReports generateCourseReports = new GenerateCourseReports(courseReportService, courseCategoryRepository);

    try
    {
      List<CourseReportResult> report = generateCourseReports.execute(reportFilter);
      return RestResponse.success(report);
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }
}

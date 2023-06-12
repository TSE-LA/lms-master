package mn.erin.lms.legacy.infrastructure.unitel.rest.promotion_statistics;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import mn.erin.domain.aim.repository.GroupRepository;
import mn.erin.domain.aim.repository.MembershipRepository;
import mn.erin.domain.aim.repository.UserIdentityRepository;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.infrastucture.rest.common.response.RestResponse;
import mn.erin.infrastucture.rest.common.response.RestResult;
import mn.erin.lms.base.aim.AccessIdentityManagement;
import mn.erin.lms.legacy.domain.lms.repository.CourseAuditRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseCategoryRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseEnrollmentRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseGroupRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseRepository;
import mn.erin.lms.legacy.domain.unitel.usecase.analytics.AnalyticsFilter;
import mn.erin.lms.legacy.domain.unitel.usecase.analytics.excel_report.GenerateExcelReport;
import mn.erin.lms.legacy.domain.unitel.usecase.analytics.get_promotion_analytics.GetPromotionAnalytics;
import mn.erin.lms.legacy.domain.unitel.usecase.analytics.get_promotion_analytics.GetPromotionAnalyticsInput;
import mn.erin.lms.legacy.domain.unitel.usecase.analytics.get_promotion_analytics.GetPromotionAnalyticsOutput;
import mn.erin.lms.legacy.domain.unitel.usecase.analytics.get_promotion_count.GetCreatedPromotionCount;
import mn.erin.lms.legacy.domain.unitel.usecase.analytics.get_promotion_count.GetEnrolledPromotionCount;
import mn.erin.lms.legacy.domain.unitel.usecase.analytics.get_promotion_count.PromotionCount;
import mn.erin.lms.legacy.infrastructure.unitel.analytics.PromotionAnalyticsImpl;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Api("Promotion Statistics")
@RequestMapping(value = "/legacy/lms/promotion-statistics", name = "Provides statistics for 'UNITEL' promotions")
public class PromotionStatisticsRestApi
{
  private static final Logger LOGGER = LoggerFactory.getLogger(PromotionStatisticsRestApi.class);

  private final CourseRepository courseRepository;
  private final CourseCategoryRepository courseCategoryRepository;
  private final PromotionAnalyticsImpl promotionAnalytics;
  private static final String EXCEL_MEDIA_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
  private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

  @Inject
  private AccessIdentityManagement accessIdentityManagement;

  @Inject
  private CourseEnrollmentRepository courseEnrollmentRepository;

  @Inject
  private CourseAuditRepository courseAuditRepository;

  @Inject
  private CourseGroupRepository courseGroupRepository;

  @Inject
  private GroupRepository groupRepository;

  @Inject
  private MembershipRepository membershipRepository;

  @Inject
  private UserIdentityRepository userIdentityRepository;

  public PromotionStatisticsRestApi(CourseRepository courseRepository, CourseCategoryRepository courseCategoryRepository,
      PromotionAnalyticsImpl promotionAnalytics)
  {
    this.courseRepository = Objects.requireNonNull(courseRepository, "CourseRepository cannot be null!");
    this.courseCategoryRepository = Objects.requireNonNull(courseCategoryRepository, "CourseCategoryRepository cannot be null!");
    this.promotionAnalytics = Objects.requireNonNull(promotionAnalytics, "PromotionAnalytics cannot be null!");
  }

  @ApiOperation("Get Created Promotion Count")
  @GetMapping("/created-promotion-count")
  public ResponseEntity read(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
      @RequestParam(required = false) String adminId)
  {
    Date startDateFilter = Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    Date endDateFilter = Date.from(endDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

    AnalyticsFilter filter = new AnalyticsFilter(startDateFilter, endDateFilter);
    Optional<String> optionalAdminId = Optional.ofNullable(adminId);
    optionalAdminId.ifPresent(filter::setUserId);

    GetCreatedPromotionCount getCreatedPromotionCount = new GetCreatedPromotionCount(courseCategoryRepository, promotionAnalytics, accessIdentityManagement,
        courseGroupRepository, groupRepository, membershipRepository);
    Set<PromotionCount> output = getCreatedPromotionCount.execute(filter);
    return RestResponse.success(output);
  }

  @ApiOperation("Get Enrolled Promotion Count")
  @GetMapping("/enrolled-promotion-count")
  public ResponseEntity<RestResult> read(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate)
  {
    AnalyticsFilter filter = new AnalyticsFilter(startDate, endDate);
    GetEnrolledPromotionCount getEnrolledPromotionCount = new GetEnrolledPromotionCount(accessIdentityManagement, courseCategoryRepository,
        courseEnrollmentRepository, courseRepository, courseAuditRepository);
    Set<PromotionCount> output = getEnrolledPromotionCount.execute(filter);
    return RestResponse.success(output);
  }

  @ApiOperation("Get A Promotion's Analytic Data")
  @GetMapping("/{promotionId}")
  public ResponseEntity read(@PathVariable String promotionId,
      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
      @RequestParam(required = false) String groupId)
  {
    AnalyticsFilter filter = new AnalyticsFilter(startDate, endDate);

    Optional<String> optionalGroup = Optional.ofNullable(groupId);
    optionalGroup.ifPresent(filter::setGroupId);

    GetPromotionAnalyticsInput input = new GetPromotionAnalyticsInput(promotionId, filter);
    GetPromotionAnalytics getPromotionAnalytics = new GetPromotionAnalytics(courseRepository, courseEnrollmentRepository,
        promotionAnalytics, accessIdentityManagement, groupRepository, userIdentityRepository);

    try
    {
      GetPromotionAnalyticsOutput output = getPromotionAnalytics.execute(input);
      return RestResponse.success(output);
    }
    catch (UseCaseException e)
    {
      LOGGER.error(e.getMessage(), e);
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Download promotion statistics report excel")
  @GetMapping("/excel/{promotionId}")
  @ResponseBody
  public ResponseEntity getExcelReport(@PathVariable String promotionId,
      @RequestParam @DateTimeFormat(pattern = "yyyyMMdd") Date startDate,
      @RequestParam @DateTimeFormat(pattern = "yyyyMMdd") Date endDate,
      @RequestParam(required = false) String groupId)

  {
    AnalyticsFilter filter = new AnalyticsFilter(startDate, endDate);

    Optional<String> optionalDepartment = Optional.ofNullable(groupId);
    optionalDepartment.ifPresent(filter::setGroupId);

    GetPromotionAnalyticsInput input = new GetPromotionAnalyticsInput(promotionId, filter);
    GenerateExcelReport generateExcelReport = new GenerateExcelReport(courseRepository,
        new GetPromotionAnalytics(courseRepository, courseEnrollmentRepository, promotionAnalytics, accessIdentityManagement, groupRepository,
            userIdentityRepository));
    try
    {
      byte[] generatedExcelData = generateExcelReport.execute(input);
      ByteArrayResource resource = new ByteArrayResource(generatedExcelData);

      LocalDateTime dateTime = LocalDateTime.now();
      String formattedDate = dateTime.format(dateFormatter);
      return ResponseEntity.ok()
          .contentLength(resource.contentLength())
          .contentType(MediaType.parseMediaType(EXCEL_MEDIA_TYPE))
          .header("Content-Disposition", "attachment; filename=\"Statistics-" + formattedDate + ".xlsx\"")
          .body(resource);
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError("generate excel report error");
    }
  }
}

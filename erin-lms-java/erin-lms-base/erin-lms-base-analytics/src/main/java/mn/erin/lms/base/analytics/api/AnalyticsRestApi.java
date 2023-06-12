package mn.erin.lms.base.analytics.api;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import javax.inject.Inject;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.Validate;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import mn.erin.common.datetime.DateTimeUtils;
import mn.erin.domain.aim.repository.AimRepositoryRegistry;
import mn.erin.domain.aim.repository.MembershipRepository;
import mn.erin.domain.aim.service.AimConfigProvider;
import mn.erin.domain.aim.service.UserAggregateService;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.infrastucture.rest.common.response.RestResponse;
import mn.erin.infrastucture.rest.common.response.RestResult;
import mn.erin.lms.base.aim.LmsDepartmentService;
import mn.erin.lms.base.analytics.repository.AnalyticsRepositoryRegistry;
import mn.erin.lms.base.analytics.usecase.announcement.GenerateAnnouncementStatistic;
import mn.erin.lms.base.analytics.usecase.dto.CourseFilter;
import mn.erin.lms.base.analytics.usecase.dto.DateFilter;
import mn.erin.lms.base.analytics.usecase.dto.ExamFilter;
import mn.erin.lms.base.analytics.usecase.dto.LearnerFilter;
import mn.erin.lms.base.analytics.usecase.dto.StatisticsFilter;
import mn.erin.lms.base.analytics.usecase.dto.SurveyFilter;
import mn.erin.lms.base.analytics.usecase.exam.GenerateExamAnalytics;
import mn.erin.lms.base.analytics.usecase.learner.GenerateLearnerAnalytics;
import mn.erin.lms.base.analytics.usecase.online_course.GenerateOnlineCourseActivities;
import mn.erin.lms.base.analytics.usecase.online_course.GenerateOnlineCourseAnalyticStatisticExcel;
import mn.erin.lms.base.analytics.usecase.online_course.GenerateOnlineCourseAnalytics;
import mn.erin.lms.base.analytics.usecase.online_course.GenerateOnlineCourseProgress;
import mn.erin.lms.base.analytics.usecase.online_course.GenerateOnlineCourseStatistics;
import mn.erin.lms.base.analytics.usecase.promotion.GeneratePromotionActivityAnalytics;
import mn.erin.lms.base.analytics.usecase.promotion.GeneratePromotionAnalytics;
import mn.erin.lms.base.analytics.usecase.promotion.GeneratePromotionLearnerSuccess;
import mn.erin.lms.base.analytics.usecase.survey.GenerateSurveyAnalytics;
import mn.erin.lms.base.domain.repository.AnnouncementRepository;
import mn.erin.lms.base.domain.repository.AnnouncementRuntimeRepository;
import mn.erin.lms.base.domain.repository.ExamCategoryRepository;
import mn.erin.lms.base.domain.repository.ExamGroupRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.service.impl.OnlineCourseTypeResolver;
import mn.erin.lms.base.domain.service.impl.PromotionTypeResolver;

/**
 * @author Munkh.
 */
@Api("Course reporting and analytics API")
@RequestMapping(value = "/lms/analytics", name = "NEW course report features")
@RestController
public class AnalyticsRestApi
{
  private final AnalyticsRepositoryRegistry analyticsRepositoryRegistry;
  private final ExamCategoryRepository examCategoryRepository;
  private final ExamGroupRepository examGroupRepository;
  private final LmsServiceRegistry lmsServiceRegistry;
  private final AimConfigProvider aimConfigProvider;
  private final AnnouncementRuntimeRepository announcementRuntimeRepository;
  private final MembershipRepository membershipRepository;
  private final AimRepositoryRegistry aimRepositoryRegistry;
  private final LmsDepartmentService lmsDepartmentService;
  private static final String EXCEL_MEDIA_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

  @Inject
  AnalyticsRestApi(AnalyticsRepositoryRegistry analyticsRepositoryRegistry,
      ExamCategoryRepository examCategoryRepository,
      ExamGroupRepository examGroupRepository,
      LmsServiceRegistry lmsServiceRegistry,
      AimConfigProvider aimConfigProvider,
      AnnouncementRuntimeRepository announcementRuntimeRepository,
      MembershipRepository membershipRepository,
      AimRepositoryRegistry aimRepositoryRegistry,
      LmsDepartmentService lmsDepartmentService
  )
  {
    this.analyticsRepositoryRegistry = analyticsRepositoryRegistry;
    this.examCategoryRepository = examCategoryRepository;
    this.examGroupRepository = examGroupRepository;
    this.lmsServiceRegistry = lmsServiceRegistry;
    this.aimConfigProvider = aimConfigProvider;
    this.announcementRuntimeRepository = announcementRuntimeRepository;
    this.membershipRepository = membershipRepository;
    this.aimRepositoryRegistry = aimRepositoryRegistry;
    this.lmsDepartmentService = lmsDepartmentService;
  }

  @ApiOperation("Generates online course reports")
  @GetMapping("/online-course")
  public ResponseEntity<RestResult> getOnlineCourseAnalytics(
      @RequestParam String departmentId,
      @RequestParam(required = false) String categoryId,
      @RequestParam(required = false) String courseType,
      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate
  )
  {
    CourseFilter filter = new CourseFilter(departmentId, new DateFilter(startDate, endDate));
    filter.setCategoryId(categoryId);
    filter.setCourseType(courseType);
    try
    {
      GenerateOnlineCourseAnalytics generateOnlineCourseAnalytics = new GenerateOnlineCourseAnalytics(analyticsRepositoryRegistry,
          new OnlineCourseTypeResolver());

      return RestResponse.success(generateOnlineCourseAnalytics.execute(filter));
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Generates online courses excel reports")
  @GetMapping("/download-all-online-course-reports")
  public ResponseEntity getOnlineCourseAnalyticStatistic(
      @RequestParam String departmentId,
      @RequestParam(required = false) String categoryId,
      @RequestParam(required = false) String courseType,
      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate
  )
  {
    CourseFilter filter = new CourseFilter(departmentId, new DateFilter(startDate, endDate));
    filter.setCategoryId(categoryId);
    filter.setCourseType(courseType);
    GenerateOnlineCourseAnalyticStatisticExcel analyticStatisticExcel =
        new GenerateOnlineCourseAnalyticStatisticExcel(analyticsRepositoryRegistry, new OnlineCourseTypeResolver());
    String currentFormattedDate = DateTimeUtils.getCurrentLocalDateTime(DateTimeUtils.LONG_ISO_DATE_FORMAT);

    try
    {
      byte[] reportData = analyticStatisticExcel.execute(filter);
      ByteArrayResource resource = new ByteArrayResource(reportData);
      return ResponseEntity.ok()
          .contentLength(resource.contentLength())
          .contentType(MediaType.parseMediaType(EXCEL_MEDIA_TYPE))
          .header("Content-Disposition", "attachment; filename=\"Unified-Online-Course-Report-" + currentFormattedDate + ".xlsx\"")
          .body(resource);
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Generates exam reports")
  @GetMapping("/exam")
  public ResponseEntity<RestResult>
  getExamAnalytics(
      @RequestParam(required = false) String groupId,
      @RequestParam(required = false) String categoryId,
      @RequestParam(required = false) String type,
      @RequestParam(required = false) String status,
      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate
  )
  {
    ExamFilter filter = new ExamFilter(new DateFilter(startDate.minusDays(1), endDate.minusDays(1)));
    filter.setGroupId(groupId);
    filter.setCategoryId(categoryId);
    filter.setType(type);
    filter.setStatus(status);
    try
    {
      GenerateExamAnalytics generateExamAnalytics = new GenerateExamAnalytics(analyticsRepositoryRegistry, examCategoryRepository, examGroupRepository,
          lmsServiceRegistry);

      return RestResponse.success(generateExamAnalytics.execute(filter));
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Generates learner activities")
  @GetMapping("/learner")
  public ResponseEntity<RestResult> getOnlineCourseLearnerAnalytics(
      @RequestParam String learnerId,
      @RequestParam String categoryId,
      @RequestParam String departmentId,
      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate
  )
  {
    LearnerFilter filter = new LearnerFilter(learnerId, new DateFilter(startDate, endDate), categoryId, departmentId);

    GenerateLearnerAnalytics generateLearnerAnalytics = new GenerateLearnerAnalytics(analyticsRepositoryRegistry, new OnlineCourseTypeResolver());

    try
    {
      return RestResponse.success(generateLearnerAnalytics.execute(filter));
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Generates course statistics")
  @GetMapping("/statistics/{courseId}")
  public ResponseEntity<RestResult> getOnlineCourseStatistics(
      @PathVariable String courseId,
      @RequestParam String groupId,
      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate
  )
  {
    StatisticsFilter filter = new StatisticsFilter(groupId, courseId, new DateFilter(startDate, endDate));

    GenerateOnlineCourseStatistics generateOnlineCourseStatistics = new GenerateOnlineCourseStatistics(analyticsRepositoryRegistry,
        new OnlineCourseTypeResolver());
    try
    {
      return RestResponse.success(generateOnlineCourseStatistics.execute(filter));
    }
    catch (Exception e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Generates online course activity")
  @GetMapping("/activity/{groupId}")
  public ResponseEntity<RestResult> getOnlineCourseStatistics(@PathVariable String groupId)
  {
    Validate.notEmpty(groupId);

    GenerateOnlineCourseActivities generateOnlineCourseActivities = new GenerateOnlineCourseActivities(analyticsRepositoryRegistry,
        new OnlineCourseTypeResolver());
    try
    {
      return RestResponse.success(generateOnlineCourseActivities.execute(groupId));
    }
    catch (Exception e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Generates learners course activity of online course")
  @GetMapping("/activity/progress/{groupId}")
  public ResponseEntity<RestResult> getLearnerProgress(@PathVariable String groupId)
  {
    Validate.notEmpty(groupId);

    GenerateOnlineCourseProgress generateOnlineCourseProgress = new GenerateOnlineCourseProgress(analyticsRepositoryRegistry, new OnlineCourseTypeResolver());
    try
    {
      return RestResponse.success(generateOnlineCourseProgress.execute(groupId));
    }
    catch (Exception e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Generates promotion reports")
  @GetMapping("/promotion")
  public ResponseEntity<RestResult> getPromotionAnalytics(
      @RequestParam String groupId,
      @RequestParam(required = false) String categoryName,
      @RequestParam(required = false) String type,
      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate
  )
  {
    CourseFilter filter = new CourseFilter(groupId, new DateFilter(startDate.minusDays(1), endDate.minusDays(1)));
    filter.setCategoryName(categoryName);
    filter.setCourseType(type);
    try
    {
      GeneratePromotionAnalytics generatePromotionAnalytics = new GeneratePromotionAnalytics(analyticsRepositoryRegistry, new PromotionTypeResolver());

      return RestResponse.success(generatePromotionAnalytics.execute(filter));
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Generates all learners activities")
  @GetMapping("/promotion/activities")
  public ResponseEntity<RestResult> getPromotionLearnerActivities(@RequestParam String groupId)
  {
    GeneratePromotionActivityAnalytics generatePromotionActivityAnalytics = new GeneratePromotionActivityAnalytics(
        analyticsRepositoryRegistry.getPromotionAnalyticsRepository());

    return RestResponse.success(generatePromotionActivityAnalytics.execute(groupId));
  }

  @ApiOperation("Generates learner activities")
  @GetMapping("/survey/{surveyId}")
  public ResponseEntity<RestResult> getOnlineCourseSurveyAnalytics(
      @PathVariable String surveyId,
      @RequestParam(required = false) String courseId,
      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate
  )
  {
    SurveyFilter filter = new SurveyFilter(surveyId, new DateFilter(startDate, endDate), courseId);

    GenerateSurveyAnalytics generateSurveyAnalytics = new GenerateSurveyAnalytics(analyticsRepositoryRegistry, new OnlineCourseTypeResolver());

    return RestResponse.success(generateSurveyAnalytics.execute(filter));
  }

  @ApiOperation("Generate promotion learner success")
  @GetMapping("/generate-promotion-learner-success")
  public ResponseEntity<RestResult> generateLearnerSuccess()
  {
    Date now = convertToDate(LocalDate.now());

    Date startDate = convertToDate(LocalDate.of(2020, 4, 1));
    Date endDate = convertToDate(LocalDate.of(2020, 5, 1));

    while (endDate.before(now))
    {
      GeneratePromotionLearnerSuccess generatePromotionLearnerSuccess = new GeneratePromotionLearnerSuccess(
          analyticsRepositoryRegistry.getLearnerAnalyticSuccessRepository(), aimConfigProvider);
      try
      {
        generatePromotionLearnerSuccess.execute(convertToLocalDate(startDate));
      }
      catch (UseCaseException e)
      {
        System.out.println("Exception on generate learner success promotion: " + e.getMessage());
      }
      startDate = convertToDate(convertToLocalDate(startDate).plusMonths(1));
      endDate = convertToDate(convertToLocalDate(endDate).plusMonths(1));
    }
    return RestResponse.success();
  }

  @ApiOperation("Generate announcement statistic")
  @GetMapping("/announcement/{announcementId}")
  public ResponseEntity<RestResult> generateAnnouncementStatistic(@PathVariable String announcementId)
  {
    GenerateAnnouncementStatistic generateAnnouncementStatistic = new GenerateAnnouncementStatistic(announcementRuntimeRepository, lmsServiceRegistry,
        membershipRepository, aimRepositoryRegistry, lmsDepartmentService);
    try
    {
      return RestResponse.success(generateAnnouncementStatistic.execute(announcementId));
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  private Date convertToDate(LocalDate date)
  {
    return Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
  }

  private LocalDate convertToLocalDate(Date dateToConvert)
  {
    return dateToConvert.toInstant()
        .atZone(ZoneId.systemDefault())
        .toLocalDate();
  }
}

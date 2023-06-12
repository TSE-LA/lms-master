package mn.erin.lms.unitel.domain.usecase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.common.datetime.DateTimeUtils;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.AccessIdentityManagement;
import mn.erin.lms.base.domain.model.course.Course;
import mn.erin.lms.base.domain.model.course.CourseId;
import mn.erin.lms.base.domain.model.enrollment.CourseEnrollment;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.aim.LmsDepartmentService;
import mn.erin.lms.base.domain.usecase.course.CourseUseCase;
import mn.erin.lms.unitel.domain.CourseConstants;
import mn.erin.lms.unitel.domain.model.analytics.CourseAnalyticData;
import mn.erin.lms.unitel.domain.service.UnitelLmsServiceRegistry;
import mn.erin.lms.unitel.domain.usecase.dto.CourseAnalyticsDto;
import mn.erin.lms.unitel.domain.usecase.dto.GetCourseAnalyticsInput;
import mn.erin.lms.unitel.domain.usecase.dto.PromotionAnalyticsDto;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class GetPromotionAnalytics extends CourseUseCase<GetCourseAnalyticsInput, List<PromotionAnalyticsDto>>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(GetPromotionAnalytics.class);

  private final LmsDepartmentService lmsDepartmentService;
  private final AccessIdentityManagement aimService;

  private final GetCourseAnalytics getCourseAnalytics;

  private Course course;

  public GetPromotionAnalytics(LmsRepositoryRegistry lmsRepositoryRegistry, UnitelLmsServiceRegistry lmsServiceRegistry,
      AccessIdentityManagement aimService)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
    this.lmsDepartmentService = lmsServiceRegistry.getDepartmentService();
    this.aimService = aimService;
    this.getCourseAnalytics = new GetCourseAnalytics(lmsServiceRegistry.getCourseAnalyticsService());
  }

  @Override
  public List<PromotionAnalyticsDto> execute(GetCourseAnalyticsInput input) throws UseCaseException
  {
    CourseAnalyticsDto courseAnalytics = getCourseAnalytics.execute(input);
    this.course = getCourse(CourseId.valueOf(input.getCourseId()));
    return toOutput(courseAnalytics, input.getDepartmentId());
  }

  private List<PromotionAnalyticsDto> toOutput(CourseAnalyticsDto courseAnalytics, String departmentId)
  {
    List<PromotionAnalyticsDto> result = new ArrayList<>();

    List<CourseEnrollment> enrollments = courseEnrollmentRepository.listAll(CourseId.valueOf(courseAnalytics.getCourseId()));
    Set<String> learners = lmsDepartmentService.getLearners(departmentId);
    Set<String> learnersWithData = new HashSet<>(courseAnalytics.getData().keySet());

    for (CourseEnrollment enrollment : enrollments)
    {
      String learnerId = enrollment.getLearnerId().getId();
      boolean isInDepartment = learners.contains(learnerId);

      if (!isInDepartment)
      {
        continue;
      }

      if (learnersWithData.contains(learnerId))
      {
        result.add(getPromotionAnalytics(learnerId, (CourseAnalyticData) courseAnalytics.getData().get(learnerId)));
      }
      else
      {
        PromotionAnalyticsDto dto = new PromotionAnalyticsDto();
        dto.setUserName(learnerId);
        setRoleAndDepartment(learnerId, dto);
        result.add(dto);
      }
    }

    return result;
  }

  private PromotionAnalyticsDto getPromotionAnalytics(String learnerId, CourseAnalyticData analyticData)
  {
    PromotionAnalyticsDto dto = new PromotionAnalyticsDto();
    dto.setUserName(learnerId);
    setRoleAndDepartment(learnerId, dto);
    dto.setFeedback(analyticData.getFeedback());
    dto.setInitialLaunchDate(analyticData.getInitialLaunchDate() != null ? analyticData.getInitialLaunchDate().toString() : null);
    dto.setLastLaunchDate(analyticData.getLastLaunchDate() != null ? analyticData.getLastLaunchDate().toString() : null);
    dto.setScore(analyticData.getScore());
    dto.setStatus(analyticData.getStatus());
    dto.setTotalTime(analyticData.getTotalTime());
    dto.setLate(isLate(analyticData.getInitialLaunchDate()));
    dto.setViews(analyticData.getInteractionsCount());
    return dto;
  }

  private void setRoleAndDepartment(String learnerId, PromotionAnalyticsDto dto)
  {
    dto.setRole(aimService.getRole(learnerId));
    dto.setDepartment(aimService.getUserDepartmentId(learnerId));
  }

  private boolean isLate(LocalDateTime initialLaunchDate)
  {
    SimpleDateFormat formatter = new SimpleDateFormat(DateTimeUtils.FULL_ISO_DATE_FORMAT);
    try
    {
      Date startDate = formatter.parse(this.course.getCourseDetail().getProperties().get(CourseConstants.PROPERTY_START_DATE));
      LocalDate startDateAsLocalDate = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
      return initialLaunchDate.toLocalDate().isAfter(startDateAsLocalDate);
    }
    catch (ParseException e)
    {
      LOGGER.error(e.getMessage(), e);
      return false;
    }
  }
}

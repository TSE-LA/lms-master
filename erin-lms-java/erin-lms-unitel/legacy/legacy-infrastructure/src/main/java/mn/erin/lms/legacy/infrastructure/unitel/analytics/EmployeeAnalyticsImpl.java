package mn.erin.lms.legacy.infrastructure.unitel.analytics;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.common.datetime.TimeUtils;
import mn.erin.domain.aim.model.membership.Membership;
import mn.erin.lms.base.aim.role.LmsRole;
import mn.erin.lms.legacy.domain.lms.model.content.CourseContentId;
import mn.erin.lms.legacy.domain.lms.model.course.Course;
import mn.erin.lms.legacy.domain.lms.model.course.CourseId;
import mn.erin.lms.legacy.domain.lms.model.course.PublishStatus;
import mn.erin.lms.legacy.domain.lms.model.enrollment.CourseEnrollment;
import mn.erin.lms.legacy.domain.lms.model.enrollment.LearnerId;
import mn.erin.lms.legacy.domain.lms.repository.CourseEnrollmentRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseRepository;
import mn.erin.lms.legacy.domain.lms.repository.LMSRepositoryException;
import mn.erin.lms.legacy.domain.unitel.model.EmployeeAnalyticData;
import mn.erin.lms.legacy.domain.unitel.model.EmployeePromotionPoints;
import mn.erin.lms.legacy.domain.unitel.model.PromotionAnalyticData;
import mn.erin.lms.legacy.domain.unitel.model.UserActivityData;
import mn.erin.lms.legacy.domain.unitel.service.EmployeeAnalytics;
import mn.erin.lms.legacy.domain.unitel.service.PromotionAnalytics;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class EmployeeAnalyticsImpl implements EmployeeAnalytics
{
  private static final Logger LOGGER = LoggerFactory.getLogger(EmployeeAnalyticsImpl.class);

  private final CourseRepository courseRepository;
  private final CourseEnrollmentRepository enrollmentRepository;
  private final PromotionAnalytics promotionAnalytics;

  public EmployeeAnalyticsImpl(CourseRepository courseRepository, CourseEnrollmentRepository enrollmentRepository,
      PromotionAnalytics promotionAnalytics)
  {
    this.courseRepository = Validate.notNull(courseRepository, "CourseRepository cannot be null!");
    this.enrollmentRepository = Validate.notNull(enrollmentRepository, "CourseEnrollmentRepository cannot be null!");
    this.promotionAnalytics = Validate.notNull(promotionAnalytics, "PromotionAnalytics cannot be null!");
  }

  @Override
  public EmployeePromotionPoints getPromotionPoint(String username)
  {
    return getPromoDataOf(username);
  }

  @Override
  public List<EmployeePromotionPoints> getPromotionPoints(List<Membership> employees)
  {
    List<EmployeePromotionPoints> result = new ArrayList<>();

    for (Membership employee : employees)
    {
      String roleName = employee.getRoleId().getId();
      if (LmsRole.LMS_USER.name().equalsIgnoreCase(roleName))
      {
        result.add(getPromoDataOf(employee.getUsername()));
      }
    }

    return result;
  }

  @Override
  public Set<EmployeeAnalyticData> getEmployeeAnalyticData(LearnerId learnerId)
  {
    if (learnerId == null)
    {
      return Collections.emptySet();
    }

    List<CourseEnrollment> enrollments = enrollmentRepository.getEnrollmentList(learnerId);

    Map<Integer, List<CourseEnrollment>> courseEnrollmentsByMonths = new HashMap<>();
    Map<Integer, List<PromotionAnalyticData>> promoAnalyticDataByMonths = new HashMap<>();

    for (int index = 1; index <= 12; index++)
    {
      courseEnrollmentsByMonths.put(index, new ArrayList<>());
      promoAnalyticDataByMonths.put(index, new ArrayList<>());
    }

    ZoneId defaultZoneId = ZoneId.systemDefault();

    Date currentDate = new Date();
    LocalDateTime currentDateLocalDateTime = currentDate.toInstant().atZone(defaultZoneId).toLocalDateTime();

    for (CourseEnrollment enrollment : enrollments)
    {
      Date enrolledDate = enrollment.getEnrolledDate();

      if (enrolledDate == null)
      {
        LOGGER.warn("Course enrollment with the ID {} doesn't contain an enrollment date", enrollment.getId().getId());
        continue;
      }

      LocalDateTime enrolledDateLocalDateTime = enrolledDate.toInstant().atZone(defaultZoneId).toLocalDateTime();

      if (enrolledDateLocalDateTime.getYear() == currentDateLocalDateTime.getYear())
      {
        List<CourseEnrollment> courseEnrollments = courseEnrollmentsByMonths.get(enrolledDateLocalDateTime.getMonthValue());
        courseEnrollments.add(enrollment);
      }
    }

    Set<PromotionAnalyticData> promotionAnalyticData = promotionAnalytics.getPromotionAnalyticData(learnerId);

    Map<String, CourseEnrollment> mappedEnrollments = enrollments.stream()
        .collect(Collectors.toMap(enrollment -> enrollment.getCourseId().getId(), enrollment -> enrollment));

    for (PromotionAnalyticData datum : promotionAnalyticData)
    {
      if (datum.getInitialLaunchDate() != null)
      {
        Course course;
        try
        {
          course = courseRepository.getCourse(new CourseContentId(datum.getContentId()));
        }
        catch (LMSRepositoryException ignored)
        {
          continue;
        }
        CourseEnrollment enrollment = mappedEnrollments.get(course.getCourseId().getId());
        if (enrollment == null || enrollment.getEnrolledDate() == null)
        {
          continue;
        }
        LocalDateTime promoInitialLaunchDateTime = enrollment.getEnrolledDate().toInstant().atZone(defaultZoneId).toLocalDateTime();
        if (promoInitialLaunchDateTime.getYear() == currentDateLocalDateTime.getYear())
        {
          List<PromotionAnalyticData> promotionData = promoAnalyticDataByMonths.get(promoInitialLaunchDateTime.getMonthValue());
          promotionData.add(datum);
        }
      }
    }

    Set<EmployeeAnalyticData> result = new HashSet<>();

    for (Map.Entry<Integer, List<CourseEnrollment>> entry : courseEnrollmentsByMonths.entrySet())
    {
      int monthValue = entry.getKey();
      EmployeeAnalyticData employeeAnalyticData = new EmployeeAnalyticData().on(monthValue);

      float enrollmentSize = entry.getValue().size();

      List<PromotionAnalyticData> promoAnalyticDataByMonth = promoAnalyticDataByMonths.get(monthValue);

      float analyticDataSize = promoAnalyticDataByMonth.size();

      employeeAnalyticData.withLaunchPercentage(enrollmentSize == 0 ? 0 : analyticDataSize / enrollmentSize * 100);

      int overallTestScore = promoAnalyticDataByMonth.stream()
          .reduce(0, (subtotal, element) -> subtotal + (element.getScore() != null ? element.getScore() : 0), Integer::sum);
      float overallMaxScore = promoAnalyticDataByMonth.stream()
          .reduce(0, (subtotal, element) -> subtotal + (element.getMaxScore() != null ? element.getMaxScore() : 0), Integer::sum);

      employeeAnalyticData.withAvgTestScorePercentage(overallMaxScore == 0 ? 0 : (overallTestScore / overallMaxScore * 100));
      result.add(employeeAnalyticData);
    }

    return result;
  }

  @Override
  public Set<UserActivityData> getUserActivityData(List<LearnerId> learners)
  {
    Set<UserActivityData> userActivityData = new HashSet<>();
    List<Course> allCourses = courseRepository.getCourseList(PublishStatus.PUBLISHED);
    List<CourseId> allCourseId = allCourses.stream()
        .map(Course::getCourseId).collect(Collectors.toList());

    for (LearnerId learner : learners)
    {

      List<CourseEnrollment> courseEnrollments = enrollmentRepository.getEnrollmentList(learner);
      List<CourseId> activeEnrollment = courseEnrollments.stream().filter(enrollment -> allCourseId.contains(enrollment.getCourseId()))
          .map(CourseEnrollment::getCourseId).collect(Collectors.toList());

      List<CourseContentId> activeContentId = allCourses.stream().filter(course -> activeEnrollment.contains(course.getCourseId()))
          .map(Course::getCourseContentId).collect(Collectors.toList());
      Set<PromotionAnalyticData> promotionAnalyticData = promotionAnalytics.getPromotionAnalyticData(learner)
       .stream().filter(data -> activeContentId.contains( new CourseContentId(data.getContentId()))).collect(Collectors.toSet());
      userActivityData.add(getUserActivityDataFrom(learner, promotionAnalyticData, activeEnrollment.size()));
    }

    return userActivityData;
  }

  private EmployeePromotionPoints getPromoDataOf(String learnerId)
  {
    Set<PromotionAnalyticData> promotionAnalyticData = promotionAnalytics.getPromotionAnalyticData(new LearnerId(learnerId));

    int promoPoints = 0;

    for (PromotionAnalyticData datum : promotionAnalyticData)
    {
      if (datum.getScore() != null)
      {
        promoPoints += datum.getScore();
      }
    }

    return new EmployeePromotionPoints(learnerId, promoPoints);
  }

  private List<PromotionAnalyticData> getPromoAnalyticDataOf(String learnerId) throws LMSRepositoryException
  {
    List<CourseEnrollment> enrollments = enrollmentRepository.getEnrollmentList(new LearnerId(learnerId));

    List<PromotionAnalyticData> result = new ArrayList<>();

    for (CourseEnrollment enrollment : enrollments)
    {
      Course course = courseRepository.getCourse(enrollment.getCourseId());
      Set<PromotionAnalyticData> promotionAnalyticData = promotionAnalytics.getPromotionAnalyticData(course.getCourseContentId().getId());

      PromotionAnalyticData promoData = getPromoDataOf(learnerId, promotionAnalyticData);

      if (promoData != null)
      {
        result.add(promoData);
      }
    }

    return result;
  }

  private PromotionAnalyticData getPromoDataOf(String userName, Set<PromotionAnalyticData> promotionAnalyticData)
  {
    Date currentDate = new Date();
    LocalDate localDate = currentDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    int currentMonthValue = localDate.getMonthValue();
    for (PromotionAnalyticData datum : promotionAnalyticData)
    {
      Date lastLaunchedDate = datum.getLastLaunchDate();

      if (lastLaunchedDate == null)
      {
        continue;
      }

      LocalDate lastLaunchDateLocal = lastLaunchedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

      if (userName.equalsIgnoreCase(datum.getContentId()) && currentMonthValue == lastLaunchDateLocal.getMonthValue())
      {
        return datum;
      }
    }

    return null;
  }

  private UserActivityData getUserActivityDataFrom(LearnerId learnerId, Collection<PromotionAnalyticData> promotionAnalyticData, Integer totalEnrollment)
  {
    if (promotionAnalyticData.isEmpty())
    {
      return new UserActivityData(learnerId, 0.0f, 0L);
    }

    Float averageStatus = 0.0f;
    Long overallTime = 0L;

    for (PromotionAnalyticData datum : promotionAnalyticData)
    {
      averageStatus += datum.getStatus();
      overallTime += TimeUtils.convertToLongRepresentation(datum.getTotalTime());
    }

    Float average = (averageStatus / totalEnrollment);
    return new UserActivityData(learnerId, average > 100 ? 100 : average, overallTime);
  }
}

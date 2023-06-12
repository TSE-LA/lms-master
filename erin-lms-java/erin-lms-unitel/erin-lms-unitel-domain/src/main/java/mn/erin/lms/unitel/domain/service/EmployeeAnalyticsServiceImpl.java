package mn.erin.lms.unitel.domain.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;

import mn.erin.common.datetime.TimeUtils;
import mn.erin.lms.base.aim.role.LmsRole;
import mn.erin.lms.base.aim.user.LearnerId;
import mn.erin.lms.base.domain.model.enrollment.CourseEnrollment;
import mn.erin.lms.base.domain.repository.CourseEnrollmentRepository;
import mn.erin.lms.base.scorm.model.RuntimeData;
import mn.erin.lms.base.scorm.repository.RuntimeDataRepository;
import mn.erin.lms.unitel.domain.model.analytics.CourseAnalyticData;
import mn.erin.lms.unitel.domain.model.analytics.EmployeeAnalyticData;
import mn.erin.lms.unitel.domain.model.analytics.EmployeePromoPoints;
import mn.erin.lms.unitel.domain.model.analytics.UserActivityData;
import mn.erin.lms.unitel.domain.model.user.Membership;

import static mn.erin.lms.unitel.domain.util.CourseAnalyticDataConverter.convert;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class EmployeeAnalyticsServiceImpl implements EmployeeAnalyticsService
{
  private CourseEnrollmentRepository courseEnrollmentRepository;
  private RuntimeDataRepository runtimeDataRepository;

  @Inject
  public void setCourseEnrollmentRepository(CourseEnrollmentRepository courseEnrollmentRepository)
  {
    this.courseEnrollmentRepository = courseEnrollmentRepository;
  }

  @Inject
  public void setRuntimeDataRepository(RuntimeDataRepository runtimeDataRepository)
  {
    this.runtimeDataRepository = runtimeDataRepository;
  }

  @Override
  public List<EmployeePromoPoints> getPromotionPoints(List<Membership> employees)
  {
    List<EmployeePromoPoints> result = new ArrayList<>();

    for (Membership employee : employees)
    {
      String roleName = employee.getRole();

      if (LmsRole.LMS_USER.name().equalsIgnoreCase(roleName))
      {
        result.add(getPromoDataOf(employee.getUserId()));
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

    List<CourseEnrollment> enrollments = courseEnrollmentRepository.listAll(learnerId);

    Map<Integer, List<CourseEnrollment>> courseEnrollmentsByMonths = new HashMap<>();
    Map<Integer, List<CourseAnalyticData>> promoAnalyticDataByMonths = new HashMap<>();

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
      LocalDateTime enrolledDate = enrollment.getEnrolledDate();

      if (enrolledDate == null)
      {
        continue;
      }

      if (enrolledDate.getYear() == currentDateLocalDateTime.getYear())
      {
        List<CourseEnrollment> courseEnrollments = courseEnrollmentsByMonths.get(enrolledDate.getMonthValue());
        courseEnrollments.add(enrollment);
      }
    }

    Set<CourseAnalyticData> courseAnalyticData = getLearnerPromotionAnalyticData(learnerId.getId());

    for (CourseAnalyticData datum : courseAnalyticData)
    {
      LocalDateTime promoInitialLaunchDateTime = datum.getInitialLaunchDate();
      if (promoInitialLaunchDateTime.getYear() == currentDateLocalDateTime.getYear())
      {
        List<CourseAnalyticData> promotionData = promoAnalyticDataByMonths.get(promoInitialLaunchDateTime.getMonthValue());
        promotionData.add(datum);
      }
    }

    Set<EmployeeAnalyticData> result = new HashSet<>();

    for (Map.Entry<Integer, List<CourseEnrollment>> entry : courseEnrollmentsByMonths.entrySet())
    {
      int monthValue = entry.getKey();
      EmployeeAnalyticData employeeAnalyticData = new EmployeeAnalyticData().on(monthValue);

      float enrollmentSize = entry.getValue().size();

      List<CourseAnalyticData> promoAnalyticDataByMonth = promoAnalyticDataByMonths.get(monthValue);

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

    for (LearnerId learner : learners)
    {
      Set<CourseAnalyticData> courseAnalyticData = getLearnerPromotionAnalyticData(learner.getId());
      List<CourseEnrollment> courseEnrollments = courseEnrollmentRepository.listAll(learner);
      userActivityData.add(getUserActivityDataFrom(learner, courseAnalyticData, courseEnrollments));
    }

    return userActivityData;
  }

  private UserActivityData getUserActivityDataFrom(LearnerId learnerId, Set<CourseAnalyticData> courseAnalyticData,
      List<CourseEnrollment> courseEnrollments)
  {
    if (courseAnalyticData.isEmpty())
    {
      return new UserActivityData(learnerId, 0.0f, 0L);
    }

    Float averageStatus = 0.0f;
    Long overallTime = 0L;

    for (CourseAnalyticData datum : courseAnalyticData)
    {
      averageStatus += datum.getStatus();
      overallTime += TimeUtils.convertToLongRepresentation(datum.getTotalTime());
    }

    if (courseEnrollments.size() < courseAnalyticData.size())
    {
      return new UserActivityData(learnerId, averageStatus / courseAnalyticData.size(), overallTime);
    }
    else
    {
      return new UserActivityData(learnerId, averageStatus / courseEnrollments.size(), overallTime);
    }
  }

  private EmployeePromoPoints getPromoDataOf(String learnerId)
  {
    Set<CourseAnalyticData> courseAnalyticData = getLearnerPromotionAnalyticData(learnerId);

    int promoPoints = 0;

    for (CourseAnalyticData datum : courseAnalyticData)
    {
      if (datum.getScore() != null)
      {
        promoPoints += datum.getScore();
      }
    }

    return new EmployeePromoPoints(learnerId, promoPoints);
  }

  private Set<CourseAnalyticData> getLearnerPromotionAnalyticData(String learnerId)
  {
    List<RuntimeData> runtimeData = runtimeDataRepository.listRuntimeData(learnerId);
    Set<CourseAnalyticData> result = new HashSet<>();

    Map<String, List<RuntimeData>> sortedByContent = sortByContentId(runtimeData);

    for (Map.Entry<String, List<RuntimeData>> dataEntry : sortedByContent.entrySet())
    {
      result.add(convert(dataEntry.getValue(), "", null));
    }

    return result;
  }

  private Map<String, List<RuntimeData>> sortByContentId(List<RuntimeData> runtimeData)
  {
    Map<String, List<RuntimeData>> sorted = new HashMap<>();

    Set<String> contentIds = runtimeData.stream().map(datum -> datum.getSco().getRootEntity().getScormContentId().getId())
        .collect(Collectors.toSet());

    for (String contentId : contentIds)
    {
      List<RuntimeData> data = runtimeData.stream().filter(datum -> contentId.equals(datum.getSco().getRootEntity().getScormContentId().getId()))
          .collect(Collectors.toList());
      sorted.put(contentId, data);
    }

    return sorted;
  }
}

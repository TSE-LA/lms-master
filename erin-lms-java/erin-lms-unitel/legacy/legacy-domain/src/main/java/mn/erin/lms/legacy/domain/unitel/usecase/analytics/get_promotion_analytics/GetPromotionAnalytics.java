package mn.erin.lms.legacy.domain.unitel.usecase.analytics.get_promotion_analytics;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.joda.time.DateTimeComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.common.datetime.DateTimeUtils;
import mn.erin.domain.aim.model.group.Group;
import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.domain.aim.model.user.UserIdentity;
import mn.erin.domain.aim.model.user.UserIdentitySource;
import mn.erin.domain.aim.repository.GroupRepository;
import mn.erin.domain.aim.repository.UserIdentityRepository;
import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.AccessIdentityManagement;
import mn.erin.lms.legacy.domain.lms.model.course.CourseId;
import mn.erin.lms.legacy.domain.lms.model.enrollment.CourseEnrollment;
import mn.erin.lms.legacy.domain.lms.repository.CourseEnrollmentRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseRepository;
import mn.erin.lms.legacy.domain.lms.usecase.course.get_course.GetCourse;
import mn.erin.lms.legacy.domain.lms.usecase.course.get_course.GetCourseInput;
import mn.erin.lms.legacy.domain.lms.usecase.course.get_course.GetCourseOutput;
import mn.erin.lms.legacy.domain.unitel.model.PromotionAnalyticData;
import mn.erin.lms.legacy.domain.unitel.service.PromotionAnalytics;
import mn.erin.lms.legacy.domain.unitel.usecase.analytics.AnalyticsFilter;

import static mn.erin.common.datetime.DateTimeUtils.LONG_ISO_DATE_FORMAT;
import static mn.erin.common.datetime.DateTimeUtils.SHORT_ISO_DATE_FORMAT;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class GetPromotionAnalytics implements UseCase<GetPromotionAnalyticsInput, GetPromotionAnalyticsOutput>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(GetPromotionAnalytics.class);

  private final CourseRepository courseRepository;
  private final CourseEnrollmentRepository courseEnrollmentRepository;
  private final PromotionAnalytics promotionAnalytics;
  private final AccessIdentityManagement accessIdentityManagement;
  private final GroupRepository groupRepository;
  private final UserIdentityRepository userIdentityRepository;

  private GetCourseOutput course;

  public GetPromotionAnalytics(CourseRepository courseRepository, CourseEnrollmentRepository courseEnrollmentRepository,
      PromotionAnalytics promotionAnalytics, AccessIdentityManagement accessIdentityManagement, GroupRepository groupRepository,
      UserIdentityRepository userIdentityRepository)
  {
    this.courseRepository = Objects.requireNonNull(courseRepository, "CourseRepository cannot be null!");
    this.courseEnrollmentRepository = Objects.requireNonNull(courseEnrollmentRepository, "CourseEnrollmentRepository cannot be null!");
    this.promotionAnalytics = Objects.requireNonNull(promotionAnalytics, "PromotionAnalytics cannot be null!");
    this.accessIdentityManagement = Objects.requireNonNull(accessIdentityManagement);
    this.groupRepository = Objects.requireNonNull(groupRepository);
    this.userIdentityRepository = Objects.requireNonNull(userIdentityRepository);
  }

  @Override
  public GetPromotionAnalyticsOutput execute(GetPromotionAnalyticsInput input) throws UseCaseException
  {
    Validate.notNull(input, "Input is required!");

    GetCourseInput getCourseInput = new GetCourseInput(input.getPromotionId());
    GetCourse getCourse = new GetCourse(courseRepository);

    String promotionContentId;
    course = getCourse.execute(getCourseInput);
    promotionContentId = course.getCourseContentId();

    Set<PromotionAnalyticData> analyticData = promotionAnalytics.getPromotionAnalyticData(promotionContentId);
    Set<PromotionAnalyticsOutput> analyticsOutputs = new HashSet<>();

    List<CourseEnrollment> enrollments = courseEnrollmentRepository.getEnrollmentList(new CourseId(this.course.getId()));
    String groupId = input.getAnalyticsFilter().getGroupId();

    Set<String> users = getUsers(groupId);
    Set<String> existingUsers = userIdentityRepository.getAllBySource(UserIdentitySource.LDAP).stream().map(UserIdentity::getUsername)
        .collect(Collectors.toSet());
    for (CourseEnrollment enrollment : enrollments)
    {
      String learnerId = enrollment.getLearnerId().getId();
      if (!users.contains(learnerId) || !existingUsers.contains(learnerId))
      {
        continue;
      }
      PromotionAnalyticData promotionAnalyticData = getDatum(analyticData, learnerId);
      if (promotionAnalyticData != null)
      {
        getFilteredRecord(promotionAnalyticData, input.getAnalyticsFilter(), analyticsOutputs);
      }
      else
      {
        try
        {
          PromotionAnalyticsOutput analyticsOutput = new PromotionAnalyticsOutput();
          analyticsOutput.setUserName(learnerId);
          setRoleGroup(learnerId, analyticsOutput);
          analyticsOutputs.add(analyticsOutput);
        }
        catch (NoSuchElementException e)
        {
          LOGGER.error(e.getMessage(), e);
        }
      }
    }
    return toOutput(analyticsOutputs);
  }

  private PromotionAnalyticData getDatum(Set<PromotionAnalyticData> analyticData, String learnerId)
  {
    return analyticData.stream().filter(data -> data.getContentId().equals(learnerId)).findFirst().orElse(null);
  }

  private Set<String> getUsers(String groupId)
  {
    return accessIdentityManagement.getAllLearners(!StringUtils.isBlank(groupId) ? groupId : accessIdentityManagement.getCurrentUserDepartmentId());
  }

  private GetPromotionAnalyticsOutput toOutput(Set<PromotionAnalyticsOutput> analyticsOutputs) throws UseCaseException
  {
    GetPromotionAnalyticsOutput output = new GetPromotionAnalyticsOutput();
    analyticsOutputs.forEach(output::addAnalyticData);

    SimpleDateFormat formatter = new SimpleDateFormat(SHORT_ISO_DATE_FORMAT);
    Date startDate;
    try
    {
      startDate = formatter.parse((String) this.course.getProperties().get("startDate"));
    }
    catch (ParseException e)
    {
      LOGGER.error(e.getMessage(), e);
      throw new UseCaseException("Course has invalid value on 'startDate' field");
    }

    for (PromotionAnalyticsOutput analyticsOutput : analyticsOutputs)
    {
      try
      {
        Date launchDate;

        if (analyticsOutput.getInitialLaunchDate() != null)
        {
          launchDate = formatter.parse(analyticsOutput.getInitialLaunchDate());
        }
        else
        {
          launchDate = new Date();
        }

        LocalDate launchLocaleDate = launchDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate startLocaleDate = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        if (launchLocaleDate.isAfter(startLocaleDate))
        {
          analyticsOutput.setLate(true);
        }
      }
      catch (ParseException e)
      {
        LOGGER.error(e.getMessage(), e);
      }
    }

    return output;
  }

  private void getFilteredRecord(PromotionAnalyticData datum, AnalyticsFilter filter, Set<PromotionAnalyticsOutput> result)
  {
    String userId = datum.getContentId();

    DateTimeComparator dateTimeComparator = DateTimeComparator.getDateOnlyInstance();
    Date startDate = filter.getStartDate();
    Date endDate = filter.getEndDate();
    Date lastLaunchDate = datum.getLastLaunchDate();

    int inRelationToStartDate = dateTimeComparator.compare(lastLaunchDate, startDate);
    int inRelationToEndDate = dateTimeComparator.compare(lastLaunchDate, endDate);

    if ((inRelationToStartDate == 0 || inRelationToStartDate > 0) &&
        (inRelationToEndDate == 0 || inRelationToEndDate < 0))
    {
      SimpleDateFormat formatter = new SimpleDateFormat(LONG_ISO_DATE_FORMAT);
      PromotionAnalyticsOutput record = new PromotionAnalyticsOutput();
      record.setUserName(userId);
      setRoleGroup(userId, record);
      record.setScore(datum.getScore());
      record.setTotalEnrollment(datum.getTotalEnrollment());
      record.setStatus(datum.getStatus());
      record.setInitialLaunchDate(datum.getInitialLaunchDate() == null ? null :
          formatter.format(datum.getInitialLaunchDate()));
      record.setLastLaunchDate(datum.getLastLaunchDate() == null ? null :
          formatter.format(datum.getLastLaunchDate()));
      record.setTotalTime(datum.getTotalTime());
      record.setFeedback(datum.getFeedback());
      result.add(record);
    }
  }

  private void setRoleGroup(String userId, PromotionAnalyticsOutput output)
  {
    String userGroupId = accessIdentityManagement.getUserDepartmentId(userId);
    Group group = groupRepository.findById(GroupId.valueOf(userGroupId));
    output.setGroup(group.getName());
    output.setRole(accessIdentityManagement.getRole(userId));
  }
}

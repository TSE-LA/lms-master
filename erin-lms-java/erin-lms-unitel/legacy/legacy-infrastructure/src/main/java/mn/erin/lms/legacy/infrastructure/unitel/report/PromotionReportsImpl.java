package mn.erin.lms.legacy.infrastructure.unitel.report;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.lms.base.aim.AccessIdentityManagement;
import mn.erin.lms.base.aim.role.LmsRole;
import mn.erin.lms.legacy.domain.lms.model.assessment.Assessment;
import mn.erin.lms.legacy.domain.lms.model.assessment.Test;
import mn.erin.lms.legacy.domain.lms.model.assessment.TestId;
import mn.erin.lms.legacy.domain.lms.model.audit.CourseAudit;
import mn.erin.lms.legacy.domain.lms.model.course.CompanyId;
import mn.erin.lms.legacy.domain.lms.model.course.Course;
import mn.erin.lms.legacy.domain.lms.model.course.CourseCategory;
import mn.erin.lms.legacy.domain.lms.model.course.CourseCategoryId;
import mn.erin.lms.legacy.domain.lms.model.course.CourseId;
import mn.erin.lms.legacy.domain.lms.model.course.PublishStatus;
import mn.erin.lms.legacy.domain.lms.model.enrollment.CourseEnrollment;
import mn.erin.lms.legacy.domain.lms.model.enrollment.LearnerId;
import mn.erin.lms.legacy.domain.lms.model.report.CourseReport;
import mn.erin.lms.legacy.domain.lms.repository.CourseAssessmentRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseAuditRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseCategoryRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseEnrollmentRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseGroupRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseTestRepository;
import mn.erin.lms.legacy.domain.lms.repository.LMSRepositoryException;
import mn.erin.lms.legacy.domain.lms.service.CourseReportService;
import mn.erin.lms.legacy.domain.unitel.CategoryConstants;
import mn.erin.lms.legacy.domain.unitel.PromotionConstants;
import mn.erin.lms.legacy.domain.unitel.model.PromotionAnalyticData;
import mn.erin.lms.legacy.domain.unitel.service.PromotionAnalytics;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class PromotionReportsImpl implements CourseReportService
{
  private static final Logger LOGGER = LoggerFactory.getLogger(PromotionReportsImpl.class);
  private static final String DECIMAL_FORMAT = "%.1f";
  private static final String HAS_TEST = "hasTest";
  private static final String QUESTIONS_COUNT = "questionsCount";

  private final PromotionAnalytics promotionAnalytics;
  private final CourseRepository courseRepository;
  private final CourseAssessmentRepository courseAssessmentRepository;
  private final CourseTestRepository courseTestRepository;
  private final CourseEnrollmentRepository courseEnrollmentRepository;
  private final CourseGroupRepository courseGroupRepository;
  private final CourseAuditRepository courseAuditRepository;

  private AccessIdentityManagement accessIdentityManagement;

  private Collection<CourseCategory> categories;

  public PromotionReportsImpl(CourseRepository courseRepository, PromotionAnalytics promotionAnalytics, CourseCategoryRepository courseCategoryRepository,
      CourseAssessmentRepository courseAssessmentRepository, CourseTestRepository courseTestRepository,
      CourseEnrollmentRepository courseEnrollmentRepository, CourseGroupRepository courseGroupRepository,
      CourseAuditRepository courseAuditRepository)
  {
    this.courseRepository = Objects.requireNonNull(courseRepository, "CourseRepository cannot be null!");
    this.promotionAnalytics = Objects.requireNonNull(promotionAnalytics, "Promotion Analytics Service cannot be null!");
    this.courseAssessmentRepository = Objects.requireNonNull(courseAssessmentRepository, "CourseAssessmentRepository cannot be null!");
    this.courseTestRepository = Objects.requireNonNull(courseTestRepository, "CourseTestRepository cannot be null!");
    this.courseEnrollmentRepository = Objects.requireNonNull(courseEnrollmentRepository, "CourseEnrollmentRepository cannot be null!");
    this.courseGroupRepository = Objects.requireNonNull(courseGroupRepository, "CourseGroupRepository cannot be null!");
    this.courseAuditRepository = Objects.requireNonNull(courseAuditRepository, "CourseAuditRepository cannot be null!");
    this.categories = Objects.requireNonNull(courseCategoryRepository, "CourseCategoryRepository cannot be null!")
        .listAll(new CompanyId(CategoryConstants.COMPANY_ID));
  }

  @Inject
  public void setAccessIdentityManagement(AccessIdentityManagement accessIdentityManagement)
  {
    this.accessIdentityManagement = accessIdentityManagement;
  }

  @Override
  public List<CourseReport> generateCourseReports(String groupId)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<CourseReport> generateCourseReports(String groupId, String categoryName, Map<String, Object> courseProperties)
  {
    Date startDate = (Date) courseProperties.get(PromotionConstants.PROPERTY_START_DATE);
    Date endDate = (Date) courseProperties.get(PromotionConstants.PROPERTY_END_DATE);

    List<Course> allCourses = getCourseReportsCategories(categoryName, startDate, endDate, courseProperties);
    return toResult(allCourses, groupId);
  }

  @Override
  public List<CourseReport> generateCourseReports(String groupId, Map<String, Object> courseProperties)
  {
    Date startDate = (Date) courseProperties.get(PromotionConstants.PROPERTY_START_DATE);
    Date endDate = (Date) courseProperties.get(PromotionConstants.PROPERTY_END_DATE);

    List<Course> allCourses = getCourses(startDate, endDate, courseProperties);

    return toResult(allCourses, groupId);
  }

  private List<Course> getCourses(Date startDate, Date endDate, Map<String, Object> courseProperties)
  {
    String currentUsername = accessIdentityManagement.getCurrentUsername();
    String currentUserGroup = accessIdentityManagement.getUserDepartmentId(currentUsername);
    Set<String> groups = accessIdentityManagement.getSubDepartments(currentUserGroup);
    String role = accessIdentityManagement.getRole(currentUsername);

    if (LmsRole.valueOf(role.toUpperCase()) == LmsRole.LMS_ADMIN)
    {
      Set<String> groupCourseIds = new HashSet<>();
      for (String group : groups)
      {
        Set<String> courseIds = courseGroupRepository.listGroupCourses(group)
            .stream().map(courseGroup -> courseGroup.getCourseId().getId()).collect(Collectors.toSet());
        groupCourseIds.addAll(courseIds);
      }

      if (courseProperties.get(PromotionConstants.PROPERTY_STATE) == null)
      {
        return courseRepository.getCourseList(PublishStatus.PUBLISHED, startDate, endDate)
            .stream().filter(course -> groupCourseIds.contains(course.getCourseId().getId())).collect(Collectors.toList());
      }
      else
      {
        return courseRepository.getCourseList(PublishStatus.PUBLISHED, startDate, endDate,
            String.valueOf(courseProperties.get(PromotionConstants.PROPERTY_STATE)))
            .stream().filter(course -> groupCourseIds.contains(course.getCourseId().getId())).collect(Collectors.toList());
      }
    }
    else
    {
      List<CourseId> enrollments = courseEnrollmentRepository.getEnrollmentList(new LearnerId(currentUsername)).stream()
          .map(CourseEnrollment::getCourseId).collect(Collectors.toList());
      List<CourseId> readerships = courseAuditRepository.listAll(new LearnerId(currentUsername)).stream()
          .map(CourseAudit::getCourseId).collect(Collectors.toList());
      // Supervisor's course report here!
      if (courseProperties.get(PromotionConstants.PROPERTY_STATE) == null)
      {
        return courseRepository.getCourseList(PublishStatus.PUBLISHED, startDate, endDate).stream()
            .filter(course -> enrollments.contains(course.getCourseId())|| readerships.contains(course.getCourseId()))
            .collect(Collectors.toList());
      }
      else
      {
        return courseRepository.getCourseList(PublishStatus.PUBLISHED, startDate, endDate, String.valueOf(courseProperties.get(PromotionConstants.PROPERTY_STATE)))
            .stream().filter(course -> enrollments.contains(course.getCourseId())|| readerships.contains(course.getCourseId()))
            .collect(Collectors.toList());
      }
    }
  }

  private List<Course> getCourseReportsCategories(String categoryName, Date startDate, Date endDate,
      Map<String, Object> courseProperties)
  {
    CourseCategory category = getCourseCategory(categoryName);
    CourseCategoryId categoryId = category != null ? category.getCategoryId() : null;
    String currentUsername = accessIdentityManagement.getCurrentUsername();
    String currentUserGroup = accessIdentityManagement.getUserDepartmentId(currentUsername);
    Set<String> groups = accessIdentityManagement.getSubDepartments(currentUserGroup);
    String role = accessIdentityManagement.getRole(currentUsername);

    if (LmsRole.valueOf(role.toUpperCase()) == LmsRole.LMS_ADMIN)
    {
      Set<String> groupCourseIds = new HashSet<>();
      for (String group : groups)
      {
        Set<String> courseIds = courseGroupRepository.listGroupCourses(group)
            .stream().map(courseGroup -> courseGroup.getCourseId().getId()).collect(Collectors.toSet());
        groupCourseIds.addAll(courseIds);
      }
      if (courseProperties.get(PromotionConstants.PROPERTY_STATE) == null)
      {
        return courseRepository.getCourseList(categoryId, PublishStatus.PUBLISHED, startDate, endDate)
            .stream().filter(course -> groupCourseIds.contains(course.getCourseId().getId())).collect(Collectors.toList());
      }
      else
      {
        return courseRepository.getCourseList(categoryId, PublishStatus.PUBLISHED, startDate, endDate,
            String.valueOf(courseProperties.get(PromotionConstants.PROPERTY_STATE))).stream()
            .filter(course -> groupCourseIds.contains(course.getCourseId().getId())).collect(Collectors.toList());
      }
    }
    else
    {
      final CourseCategoryId id = categoryId;

      if (courseProperties.get(PromotionConstants.PROPERTY_STATE) == null)
      {
        return courseRepository.getCourseList(groups, startDate, endDate).stream().filter(course -> course.getCourseCategoryId().equals(id))
            .collect(Collectors.toList());
      }
      else
      {
        return courseRepository.getCourseList(groups, startDate, endDate, String.valueOf(courseProperties.get(PromotionConstants.PROPERTY_STATE)))
            .stream().filter(course -> course.getCourseCategoryId().equals(id))
            .collect(Collectors.toList());
      }
    }
  }

  private List<CourseReport> toResult(List<Course> allCourses, String groupId)
  {
    List<CourseReport> courseReports = new ArrayList<>();
    for (Course course : allCourses)
    {
      courseReports.add(getCourseReport(course, groupId));
    }
    return courseReports;
  }

  private CourseReport getCourseReport(Course course, String groupId)
  {
    Set<PromotionAnalyticData> promotionAnalyticData = promotionAnalytics.getPromotionAnalyticData(course.getCourseContentId().getId());

    CourseReport courseReport = new CourseReport(course);
    if (course.getCourseDetail().getProperties().get(HAS_TEST) != null && course.getCourseDetail().getProperties().get(HAS_TEST).equals(true))
    {
      courseReport.putReportData(QUESTIONS_COUNT, getQuestionsCount(course.getCourseId()));
    }
    else
    {
      courseReport.putReportData(QUESTIONS_COUNT, 0);
    }

    List<CourseEnrollment> enrollments = courseEnrollmentRepository.getEnrollmentList(course.getCourseId());
    List<String> enrolledLearners = enrollments.stream().map(courseEnrollment -> courseEnrollment.getLearnerId().getId()).collect(Collectors.toList());
    promotionAnalyticData.removeIf(analytic -> !enrolledLearners.contains(analytic.getContentId()));
    Set<String> groupUsers = getGroupUsers(groupId);

    for (String user : enrolledLearners)
    {
      if (groupUsers.contains(user))
      {
        courseReport.addLearner(new LearnerId(user));
      }
    }

    float dataSampleSize = courseReport.getEnrolledLearners().size();
    courseReport.putReportData("totalEnrollment", courseReport.getEnrolledLearners().size());

    CourseCategory category = getCourseCategory(courseReport.getRootEntity().getCourseCategoryId().getId());
    if (category != null)
    {
      courseReport.putReportData(PromotionConstants.REPORT_FIELD_PROMO_CATEGORY, category.getName());
    }

    if (promotionAnalyticData.isEmpty())
    {
      return courseReport;
    }

    float overallScore = 0;
    int totalViewCount = 0;
    int feedbackCount = 0;
    int analyticDataSampleSize = 0;

    for (PromotionAnalyticData datum : promotionAnalyticData)
    {
      if(groupUsers.contains(datum.getContentId()))
      {
        overallScore += datum.getScore() != null ? datum.getScore() : 0;

        if (!StringUtils.isBlank(datum.getFeedback()))
        {
          feedbackCount += 1;
        }
        totalViewCount += datum.getInteractionsCount() != 0 ? 1 : 0;
        analyticDataSampleSize += datum.getStatus();
      }
    }

    float status = dataSampleSize == 0 ? 0 : Float.parseFloat(String.format(DECIMAL_FORMAT, (analyticDataSampleSize / dataSampleSize)));
    courseReport.putReportData(PromotionConstants.REPORT_FIELD_STATUS, status > 100 ? 100.0f : status);
    courseReport.putReportData(PromotionConstants.REPORT_FIELD_SCORE, dataSampleSize == 0 ? 0 :
        Float.parseFloat(String.format(DECIMAL_FORMAT, (overallScore / dataSampleSize))));
    courseReport.putReportData(PromotionConstants.REPORT_FIELD_FEEDBACK, feedbackCount);
    courseReport.putReportData(PromotionConstants.REPORT_FIELD_VIEWS, totalViewCount);

    return courseReport;
  }

  private Integer getQuestionsCount(CourseId courseId)
  {
    try
    {
      Assessment assessment = courseAssessmentRepository.get(courseId);

      List<TestId> courseTests = assessment.getCourseTests();

      int questionsCount = 0;

      for (TestId testId : courseTests)
      {
        Test test = courseTestRepository.get(testId);
        questionsCount += test.getQuestions().size();
      }

      return questionsCount;
    }
    catch (LMSRepositoryException e)
    {
      LOGGER.error(e.getMessage(), e);
      return 0;
    }
  }

  private Set<String> getGroupUsers(String groupId)
  {
    return accessIdentityManagement.getLearners(groupId);
  }

  private CourseCategory getCourseCategory(String input)
  {
    for (CourseCategory category : categories)
    {
      if (category.getName().equals(input) || category.getCategoryId().getId().equals(input))
      {
        return category;
      }
    }
    return null;
  }
}

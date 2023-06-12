package mn.erin.lms.legacy.infrastructure.unitel.report;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import mn.erin.lms.legacy.domain.lms.model.course.CompanyId;
import mn.erin.lms.legacy.domain.lms.model.course.Course;
import mn.erin.lms.legacy.domain.lms.model.course.CourseCategory;
import mn.erin.lms.legacy.domain.lms.model.course.CourseId;
import mn.erin.lms.legacy.domain.lms.model.course.PublishStatus;
import mn.erin.lms.legacy.domain.lms.model.enrollment.CourseEnrollment;
import mn.erin.lms.legacy.domain.lms.model.enrollment.LearnerId;
import mn.erin.lms.legacy.domain.lms.model.report.CourseReport;
import mn.erin.lms.legacy.domain.lms.repository.CourseCategoryRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseEnrollmentRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseRepository;
import mn.erin.lms.legacy.domain.lms.service.CourseActivityReportService;
import mn.erin.lms.legacy.domain.scorm.constants.DataModelConstants;
import mn.erin.lms.legacy.domain.scorm.model.DataModel;
import mn.erin.lms.legacy.domain.scorm.model.RuntimeData;
import mn.erin.lms.legacy.domain.scorm.model.SCORMTime;
import mn.erin.lms.legacy.domain.scorm.repository.RuntimeDataRepository;
import mn.erin.lms.legacy.domain.unitel.CategoryConstants;
import mn.erin.lms.legacy.domain.unitel.PromotionConstants;
import mn.erin.lms.legacy.domain.unitel.model.PromotionAnalyticData;

/**
 * @author Munkh
 */
public class PromotionActivityReportsImpl implements CourseActivityReportService
{
  private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
  private final CourseRepository courseRepository;
  private final CourseEnrollmentRepository courseEnrollmentRepository;
  private RuntimeDataRepository runtimeDataRepository;
  private Collection<CourseCategory> categories;

  public PromotionActivityReportsImpl(CourseCategoryRepository courseCategoryRepository,
      RuntimeDataRepository runtimeDataRepository, CourseRepository courseRepository,
      CourseEnrollmentRepository courseEnrollmentRepository)
  {
    this.runtimeDataRepository = runtimeDataRepository;
    this.courseRepository = courseRepository;
    this.courseEnrollmentRepository = courseEnrollmentRepository;
    this.categories = Objects.requireNonNull(courseCategoryRepository, "CourseCategoryRepository cannot be null!")
        .listAll(new CompanyId(CategoryConstants.COMPANY_ID));
  }

  @Override
  public List<CourseReport> generateCourseActivityReports(String learnerId, Map<String, Object> courseProperties)
  {
    Date startDate = (Date) courseProperties.get(PromotionConstants.PROPERTY_START_DATE);
    Date endDate = (Date) courseProperties.get(PromotionConstants.PROPERTY_END_DATE);

    List<Course> enrolledCourses = getCourses(startDate, endDate, learnerId);

    List<RuntimeData> runtimeData = runtimeDataRepository.listRuntimeData(learnerId);
    Map<String, List<RuntimeData>> sortedByContent = sortByContentId(runtimeData);

    List<CourseReport> courseReports = new ArrayList<>();

    for (Course course : enrolledCourses)
    {
      if(course.getCourseContentId() == null)
      {
        continue;
      }
      String contentId = course.getCourseContentId().getId();
      List<RuntimeData> data = sortedByContent.get(contentId);
      PromotionAnalyticData promotionAnalyticData = convert(learnerId, data);
      CourseReport courseReport = getCourseReport(course);
      fillPromotionReport(courseReport, promotionAnalyticData);
      courseReports.add(courseReport);
    }

    return courseReports;
  }

  private void fillPromotionReport(CourseReport courseReport, PromotionAnalyticData analytics)
  {
    if (analytics == null)
    {
      fillEmptyPromotionReport(courseReport);
      return;
    }

    courseReport.putReportData("status", analytics.getStatus() == null ? 0.0f : analytics.getStatus());
    courseReport.putReportData("views", analytics.getInteractionsCount() == null ? 0 : analytics.getInteractionsCount());
    courseReport.putReportData("score", analytics.getScore() == null ? 0 : analytics.getScore());
    courseReport.putReportData("feedback", analytics.getFeedback() == null ? 0 : 1);
    courseReport.putReportData("spentTime", analytics.getTotalTime() == null ? "00:00:00" : analytics.getTotalTime());
    courseReport.putReportData("firstViewDate", analytics.getInitialLaunchDate() == null ? "" :
        dateFormat.format(analytics.getInitialLaunchDate()));
    courseReport.putReportData("lastViewDate", analytics.getLastLaunchDate() == null ? "" :
        dateFormat.format(analytics.getLastLaunchDate()));
  }

  private void fillEmptyPromotionReport(CourseReport courseReport)
  {
    courseReport.putReportData("status", 0.0f);
    courseReport.putReportData("views", 0);
    courseReport.putReportData("score", 0);
    courseReport.putReportData("feedback", 0);
    courseReport.putReportData("spentTime", "00:00:00");
    courseReport.putReportData("firstViewDate", "");
    courseReport.putReportData("lastViewDate", "");
  }

  private Map<String, List<RuntimeData>> sortByContentId(List<RuntimeData> allRuntimeData)
  {
    Map<String, List<RuntimeData>> sorted = new HashMap<>();

    Set<String> contentIds = allRuntimeData.stream().map(datum -> datum.getSco().getRootEntity().getScormContentId().getId())
        .collect(Collectors.toSet());

    for (String contentId : contentIds)
    {
      List<RuntimeData> data = allRuntimeData.stream().filter(datum -> contentId.equals(datum.getSco().getRootEntity().getScormContentId().getId()))
          .collect(Collectors.toList());
      sorted.put(contentId, data);
    }

    return sorted;
  }

  private List<Course> getCourses(Date startDate, Date endDate, String learnerId)
  {
    List<Course> allCourses = courseRepository.getCourseList(PublishStatus.PUBLISHED, startDate, endDate);
    List<CourseId> enrolledCourses = courseEnrollmentRepository.getEnrollmentList(new LearnerId(learnerId)).stream()
        .map(CourseEnrollment::getCourseId).collect(Collectors.toList());

    return allCourses.stream().filter(course -> enrolledCourses.contains(course.getCourseId())).collect(Collectors.toList());
  }

  private List<CourseReport> toResult(List<Course> enrolledCourses)
  {
    List<CourseReport> courseReports = new ArrayList<>();
    for (Course course : enrolledCourses)
    {
      courseReports.add(getCourseReport(course));
    }
    return courseReports;
  }

  private CourseReport getCourseReport(Course course)
  {
    CourseReport courseReport = new CourseReport(course);

    CourseCategory category = getCourseCategory(course.getCourseCategoryId().getId());

    if (category != null)
    {
      courseReport.putReportData(PromotionConstants.REPORT_FIELD_PROMO_CATEGORY, category.getName());
    }

    return courseReport;
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

  protected PromotionAnalyticData convert(String user, List<RuntimeData> runtimeData)
  {
    if(runtimeData == null)
    {
      return null;
    }
    PromotionAnalyticData.Builder builder = new PromotionAnalyticData.Builder();
    builder = builder.ofContentId(user);

    float progress = 0;
    SCORMTime totalTime = new SCORMTime("PT0.0S");
    Date initialLaunchDate = null;
    Date lastLaunchDate = null;
    Integer score = null;
    Integer maxScore = null;
    Integer totalEnrollment = 0;
    int scoSize = runtimeData.size();
    int interactionsCount = 0;

    for (RuntimeData datum : runtimeData)
    {
      Map<DataModel, Serializable> data = datum.getData();
      if (!datum.getSco().getName().equalsIgnoreCase("асуулга") &&
          !datum.getSco().getName().equalsIgnoreCase("тест"))
      {
        for (Map.Entry<DataModel, Serializable> dataEntry : data.entrySet())
        {
          switch (dataEntry.getKey().getName())
          {
          case DataModelConstants.CMI_PROGRESS_MEASURE:
            progress += Float.parseFloat((String) dataEntry.getValue());
            break;
//          case DataModelConstants.CMI_SCORE_RAW:
//            String value = (String) dataEntry.getValue();
//            score = "unknown".equals(value) ? null : Integer.parseInt(value);
//            break;
//          case DataModelConstants.CMI_SCORE_MAX:
//            String maxScoreValue = (String) dataEntry.getValue();
//            maxScore = "unknown".equals(maxScoreValue) ? null : Integer.parseInt(maxScoreValue);
//            break;
          case "erin.date.initial_launch":
            Date initDate = getDate((String) dataEntry.getValue());
            if ((initialLaunchDate == null) || (initDate != null && initialLaunchDate.getTime() > initDate.getTime()))
            {
              initialLaunchDate = initDate;
            }
            break;
          case "erin.date.last_launch":
            Date endDate = getDate((String) dataEntry.getValue());
            if (lastLaunchDate == null || (endDate != null && lastLaunchDate.getTime() < endDate.getTime()))
            {
              lastLaunchDate = endDate;
            }
            break;
          case DataModelConstants.CMI_TOTAL_TIME:
            totalTime.add(new SCORMTime((String) dataEntry.getValue()));
            break;
          case DataModelConstants.CMI_INTERACTIONS_COUNT:
            int count = Integer.parseInt((String) dataEntry.getValue());
            interactionsCount = Math.max(count, interactionsCount);
            break;
          default:
            break;
          }
        }
      }
      else if (datum.getSco().getName().equalsIgnoreCase("тест"))
      {
        for (Map.Entry<DataModel, Serializable> dataEntry : data.entrySet())
        {
          if (DataModelConstants.CMI_SCORE_RAW.equalsIgnoreCase(dataEntry.getKey().getName()))
          {
            String value = (String) dataEntry.getValue();
            score = "unknown".equals(value) ? null : Integer.parseInt(value);
          }
          if (DataModelConstants.CMI_SCORE_MAX.equalsIgnoreCase(dataEntry.getKey().getName()))
          {
            String maxScoreValue = (String) dataEntry.getValue();
            maxScore = "unknown".equals(maxScoreValue) ? null : Integer.parseInt(maxScoreValue);
          }
        }
        scoSize -= 1;
      }
      else
      {
        for (Map.Entry<DataModel, Serializable> dataEntry : data.entrySet())
        {
          if ("cmi.comments_from_learner.1.comment".equals(dataEntry.getKey().getName()))
          {
            builder = builder.withFeedback((String) dataEntry.getValue());
          }
          if (DataModelConstants.CMI_TOTAL_TIME.equals(dataEntry.getKey().getName()))
          {
            totalTime.add(new SCORMTime((String) dataEntry.getValue()));
          }
        }
        scoSize -= 1;
      }
    }

    builder = builder.withTotalTime(SCORMTime.convertToHumanReadableTime(totalTime.getValue()));

    builder = builder.withScore(score);
    builder = builder.havingMaxScore(maxScore);
    builder = builder.withTotalEnrollment(totalEnrollment);
    builder = builder.startedAt(initialLaunchDate);
    builder = builder.lastLaunchedAt(lastLaunchDate);
    builder = builder.withStatus(progress / scoSize);
    builder = builder.havingInteractionsCount(interactionsCount);
    return builder.build();
  }

  private Date getDate(String dateStringRepresentation)
  {
    try
    {
      SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
      return formatter.parse(dateStringRepresentation);
    }
    catch (ParseException e)
    {
      return null;
    }
  }
}

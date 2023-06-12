package mn.erin.lms.jarvis.domain.report.usecase;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.model.EntityId;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.jarvis.domain.report.model.CourseReport;
import mn.erin.lms.jarvis.domain.report.model.analytics.CourseAnalyticData;
import mn.erin.lms.base.domain.model.course.Course;
import mn.erin.lms.base.aim.organization.DepartmentId;
import mn.erin.lms.base.aim.user.Author;
import mn.erin.lms.base.aim.user.Instructor;
import mn.erin.lms.base.aim.user.LearnerId;
import mn.erin.lms.base.aim.user.Manager;
import mn.erin.lms.base.aim.user.Supervisor;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.usecase.course.CourseUseCase;
import mn.erin.lms.jarvis.domain.report.service.CourseActivityReportService;
import mn.erin.lms.jarvis.domain.report.usecase.analytics.GetCourseAnalytics;
import mn.erin.lms.jarvis.domain.report.usecase.analytics.dto.CourseAnalyticsDto;
import mn.erin.lms.jarvis.domain.report.usecase.analytics.dto.GetCourseAnalyticsInput;
import mn.erin.lms.base.domain.usecase.certificate.GetReceivedCertificates;
import mn.erin.lms.base.domain.usecase.certificate.dto.ReceivedCertificateDto;
import mn.erin.lms.jarvis.domain.report.usecase.dto.CourseReportDto;
import mn.erin.lms.jarvis.domain.report.usecase.dto.ReportFilter;
import mn.erin.lms.jarvis.domain.service.JarvisLmsServiceRegistry;

/**
 * @author Munkh
 */
@Authorized(users = { Author.class, Manager.class, Instructor.class, Supervisor.class})
public class GenerateCourseActivityReports extends CourseUseCase<ReportFilter, List<CourseReportDto>>
{
  private static final String EMPTY_SURVEY_STRING = "singleChoice|multiChoice|fillInBlank";
  private final CourseActivityReportService courseActivityReportService;
  private final GetCourseAnalytics getCourseAnalytics;
  private final GetReceivedCertificates getReceivedCertificates;

  public GenerateCourseActivityReports(LmsRepositoryRegistry lmsRepositoryRegistry,
      JarvisLmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
    this.courseActivityReportService = lmsServiceRegistry.getCourseActivityReportService();
    getCourseAnalytics = new GetCourseAnalytics(lmsServiceRegistry.getCourseAnalyticsService());
    getReceivedCertificates = new GetReceivedCertificates(lmsRepositoryRegistry, lmsServiceRegistry);
  }

  @Override
  public List<CourseReportDto> execute(ReportFilter input) throws UseCaseException
  {
    Validate.notNull(input);
    DepartmentId departmentId = DepartmentId.valueOf(input.getDepartmentId());
    List<CourseReport> courseReports;

    if (!StringUtils.isBlank(input.getLearnerId()))
    {
      courseReports = courseActivityReportService.generateCourseReports(departmentId, LearnerId.valueOf(input.getLearnerId()), input.getStartDate(), input.getEndDate());
    }
    else
    {
      return new ArrayList<>();
    }

    if (courseReports.isEmpty())
    {
      return new ArrayList<>();
    }

    return toOutput(courseReports, input);
  }

  private List<CourseReportDto> toOutput(List<CourseReport> courseReports, ReportFilter filter) throws UseCaseException
  {
    List<CourseReportDto> result = new ArrayList<>();

    GetCourseAnalyticsInput input;
    CourseAnalyticsDto courseAnalytics;
    CourseAnalyticData analyticData;

    List<ReceivedCertificateDto> certificates = getReceivedCertificates.execute(filter.getLearnerId());
    try
    {
      for(CourseReport courseReport : courseReports)
      {
        Course course = getCourse(courseReport.getCourseId());

        input = new GetCourseAnalyticsInput(course.getCourseId().getId(), filter.getDepartmentId(), filter.getStartDate(), filter.getEndDate());

        courseAnalytics = getCourseAnalytics.execute(input);
        if (courseAnalytics.getData() == null)
        {
          continue;
        }
        analyticData = (CourseAnalyticData) courseAnalytics.getData().get(filter.getLearnerId());

        courseReport.putReportData("certification", "");
        // Put certification in data if learner has it
        for (ReceivedCertificateDto certificate : certificates)
        {
          if (certificate.getCertificateId().equals(course.getCertificateId()))
          {
            courseReport.putReportData("certification", certificate.getDate());
            break;
          }
        }

        courseReport.putReportData("category", course.getCourseCategoryName());
        fillCourseReport(courseReport, analyticData);

        result.add(getCourseReportDto(courseReport.getReportData(), courseReport.getEnrolledLearners(), course));
      }
    }
    catch (UseCaseException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }

    return result;
  }

  private void fillCourseReport(CourseReport courseReport, CourseAnalyticData analytics)
  {
    if(analytics == null)
    {
      fillEmptyCourseReport(courseReport);
      return;
    }
    // assessment?
    courseReport.putReportData("status", analytics.getStatus() == null ? 0 : analytics.getStatus());
    courseReport.putReportData("views", analytics.getInteractionsCount() == null ? 0 : analytics.getInteractionsCount());
    courseReport.putReportData("score", analytics.getScore() == null ? 0 : analytics.getScore());
    courseReport.putReportData("spentTime", analytics.getTotalTime() == null ? "00:00:00" : analytics.getTotalTime());
    courseReport.putReportData("firstViewDate", analytics.getInitialLaunchDate() == null ? "" :
        analytics.getInitialLaunchDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm")));
    courseReport.putReportData("lastViewDate", analytics.getLastLaunchDate() == null ? "" :
        analytics.getLastLaunchDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm")));
    courseReport.putReportData("survey", analytics.getFeedback() == null ||
        EMPTY_SURVEY_STRING.equals(analytics.getFeedback()) ? 0 : 1);
  }

  private void fillEmptyCourseReport(CourseReport courseReport)
  {
    courseReport.putReportData("status", 0);
    courseReport.putReportData("views", 0);
    courseReport.putReportData("score", 0);
    courseReport.putReportData("spentTime", "00:00:00");
    courseReport.putReportData("firstViewDate", "");
    courseReport.putReportData("lastViewDate", "");
    courseReport.putReportData("survey", 0);
  }

  private CourseReportDto getCourseReportDto(Map<String, Object> reportData, Set<LearnerId> enrolledLearners,
      Course course)
  {
    Set<String> learners = enrolledLearners.stream().map(EntityId::getId).collect(Collectors.toSet());

    return new CourseReportDto.Builder(course.getCourseId().getId())
        .withName(course.getCourseDetail().getTitle())
        .withAuthor(course.getAuthorId().getId())
        .withEnrolledLearners(learners)
        .withReportData(reportData)
        .withProperties(course.getCourseDetail().getProperties())
        .createdAt(course.getCourseDetail().getDateInfo().getCreatedDate().toLocalDate())
        .build();
  }
}

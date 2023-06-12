package mn.erin.lms.jarvis.domain.report.usecase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.base.model.EntityId;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.base.domain.model.category.CourseCategoryId;
import mn.erin.lms.base.domain.model.course.Course;
import mn.erin.lms.base.aim.organization.DepartmentId;
import mn.erin.lms.base.aim.user.Author;
import mn.erin.lms.base.aim.user.Instructor;
import mn.erin.lms.base.aim.user.LearnerId;
import mn.erin.lms.base.aim.user.Manager;
import mn.erin.lms.base.aim.user.Supervisor;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.aim.LmsDepartmentService;
import mn.erin.lms.base.domain.usecase.course.CourseUseCase;
import mn.erin.lms.jarvis.domain.report.model.CourseReport;
import mn.erin.lms.jarvis.domain.report.service.CourseReportService;
import mn.erin.lms.jarvis.domain.report.usecase.dto.CourseReportDto;
import mn.erin.lms.jarvis.domain.report.usecase.dto.ReportFilter;
import mn.erin.lms.jarvis.domain.service.JarvisLmsServiceRegistry;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Authorized(users = { Author.class, Manager.class, Instructor.class, Supervisor.class })
public class GenerateCourseReports extends CourseUseCase<ReportFilter, List<CourseReportDto>>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(GenerateCourseReports.class);

  private final CourseReportService courseReportService;
  private LmsDepartmentService departmentService;

  private static final String HAS_TEACHER = "teacher";

  public GenerateCourseReports(LmsRepositoryRegistry lmsRepositoryRegistry,
      JarvisLmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
    this.courseReportService = lmsServiceRegistry.getCourseReportService();
    this.departmentService = lmsServiceRegistry.getDepartmentService();
  }

  @Override
  public List<CourseReportDto> execute(ReportFilter input) throws UseCaseException
  {
    Validate.notNull(input);
    DepartmentId departmentId = DepartmentId.valueOf(input.getDepartmentId());
    List<CourseReport> courseReports;

    if (StringUtils.isBlank(input.getCategoryId()) && StringUtils.isBlank(input.getCourseType()))
    {
      courseReports = courseReportService.generateCourseReports(departmentId, input.getStartDate(), input.getEndDate());
    }
    else if (StringUtils.isBlank(input.getCategoryId()) && !StringUtils.isBlank(input.getCourseType()))
    {
      courseReports = courseReportService.generateCourseReports(departmentId, getCourseType(input.getCourseType()), input.getStartDate(),
          input.getEndDate());
    }
    else if (!StringUtils.isBlank(input.getCategoryId()) && StringUtils.isBlank(input.getCourseType()))
    {
      CourseCategoryId courseCategoryId = CourseCategoryId.valueOf(input.getCategoryId());
      courseReports = courseReportService.generateCourseReports(departmentId, courseCategoryId, input.getStartDate(), input.getEndDate());
    }
    else
    {
      CourseCategoryId courseCategoryId = CourseCategoryId.valueOf(input.getCategoryId());
      courseReports = courseReportService
          .generateCourseReports(departmentId, courseCategoryId, getCourseType(input.getCourseType()), input.getStartDate(), input.getEndDate());
    }

    return toOutput(courseReports);
  }

  private List<CourseReportDto> toOutput(List<CourseReport> courseReports)
  {
    List<CourseReportDto> result = new ArrayList<>();
    List<CourseReportDto> filteredCourseReportResults = new ArrayList<>();
    for (CourseReport courseReport : courseReports)
    {
      try
      {
        Course course = getCourse(courseReport.getCourseId());
        result.add(getCourseReportDto(courseReport.getReportData(), courseReport.getEnrolledLearners(), course));
      }
      catch (UseCaseException e)
      {
        LOGGER.error(e.getMessage(), e);
      }
    }
    for (CourseReportDto report : result)
    {
      if (report.getCourseProperties().get(HAS_TEACHER) == null)
      {
        filteredCourseReportResults.add(report);
      }
    }
    return filteredCourseReportResults;
  }

  private CourseReportDto getCourseReportDto(Map<String, Object> reportData, Set<LearnerId> enrolledLearners,
      Course course)
  {
    Set<String> learners = enrolledLearners.stream().map(EntityId::getId).collect(Collectors.toSet());
    String courseCategoryName = course.getCourseCategoryName();
    DepartmentId courseDepartment = course.getCourseDepartmentRelation().getCourseDepartment();
    String departmentName = departmentService.getDepartmentName(courseDepartment.getId());
    course.getCourseDetail().addProperty("categoryName", courseCategoryName);
    course.getCourseDetail().addProperty("courseGroup", departmentName);
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

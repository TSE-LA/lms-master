package mn.erin.lms.unitel.domain.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.lms.base.domain.model.category.CourseCategoryId;
import mn.erin.lms.base.domain.model.course.Course;
import mn.erin.lms.base.domain.model.course.CourseType;
import mn.erin.lms.base.aim.organization.DepartmentId;
import mn.erin.lms.base.aim.user.LmsUser;
import mn.erin.lms.base.domain.repository.CourseRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.aim.LmsDepartmentService;
import mn.erin.lms.base.aim.LmsUserService;
import mn.erin.lms.unitel.domain.model.report.CourseReport;
import mn.erin.lms.unitel.domain.model.user.UnitelAdmin;
import mn.erin.lms.unitel.domain.model.user.UnitelManager;
import mn.erin.lms.unitel.domain.model.user.UnitelSupervisor;
import mn.erin.lms.unitel.domain.repository.CourseReportRepository;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class CourseReportServiceImpl implements CourseReportService
{
  private static final Logger LOGGER = LoggerFactory.getLogger(CourseReportServiceImpl.class);

  private final LmsUserService lmsUserService;
  private final LmsDepartmentService departmentService;

  private CourseRepository courseRepository;
  private CourseReportRepository courseReportRepository;

  public CourseReportServiceImpl(LmsUserService lmsUserService, LmsDepartmentService departmentService)
  {
    this.lmsUserService = lmsUserService;
    this.departmentService = departmentService;
  }

  @Inject
  public void setCourseRepository(CourseRepository courseRepository)
  {
    this.courseRepository = courseRepository;
  }

  @Inject
  public void setCourseReportRepository(CourseReportRepository courseReportRepository)
  {
    this.courseReportRepository = courseReportRepository;
  }

  @Override
  public List<CourseReport> generateCourseReports(DepartmentId departmentId, LocalDate startDate, LocalDate endDate)
  {
    List<Course> courses = getCourses(departmentId, startDate, endDate);
    return generateCourseReport(courses, departmentId);
  }

  @Override
  public List<Course> generateCourseReportWithoutRuntimeData(DepartmentId departmentId, LocalDate startDate, LocalDate endDate)
  {
    List<Course> courses = getCoursesWithoutRuntime(departmentId, startDate, endDate);
    courses.removeIf(res -> res.getCourseDetail().getProperties().get("address") == null);
    for (Course course : courses)
    {
      Map<String, String> properties = course.getCourseDetail().getProperties();

      String departmentName = departmentService.getDepartmentName(course.getCourseDepartmentRelation().getCourseDepartment().getId());
      properties.put("belongingDepartmentName", departmentName);
    }
    return courses;
  }

  @Override
  public List<CourseReport> generateCourseReports(DepartmentId departmentId, CourseType courseType, LocalDate startDate, LocalDate endDate)
  {
    List<Course> courses = getCourses(departmentId, courseType, startDate, endDate);
    return generateCourseReport(courses, departmentId);
  }

  @Override
  public List<CourseReport> generateCourseReports(DepartmentId departmentId, CourseCategoryId courseCategoryId, CourseType courseType, LocalDate startDate,
      LocalDate endDate)
  {
    List<Course> courses = getCourses(departmentId, courseCategoryId, courseType, startDate, endDate);
    return generateCourseReport(courses, departmentId);
  }

  @Override
  public List<Course> generateCourseReportsWithoutRuntimeData(DepartmentId departmentId, CourseCategoryId courseCategoryId, LocalDate startDate,
      LocalDate endDate)
  {
    List<Course> courses = getCoursesWithoutRuntime(departmentId, courseCategoryId, startDate, endDate);
    courses.removeIf(res -> res.getCourseDetail().getProperties().get("address") == null);
    for (Course course : courses)
    {
      String departmentName = departmentService.getDepartmentName(course.getCourseDepartmentRelation().getCourseDepartment().getId());
      Map<String, String> properties = course.getCourseDetail().getProperties();
      properties.put("belongingDepartmentName", departmentName);
    }
    return courses;
  }

  @Override
  public List<CourseReport> generateCourseReports(DepartmentId departmentId, CourseCategoryId courseCategoryId, LocalDate startDate,
      LocalDate endDate)
  {
    List<Course> courses = getCourses(departmentId, courseCategoryId, startDate, endDate);
    return generateCourseReport(courses, departmentId);
  }

  private List<CourseReport> generateCourseReport(List<Course> courses, DepartmentId departmentId)
  {
    List<CourseReport> reports = new ArrayList<>();

    for (Course course : courses)
    {
      try
      {
        reports.add(courseReportRepository.find(course.getCourseId(), departmentId));
      }
      catch (LmsRepositoryException e)
      {
        LOGGER.error("Couldn't get report data", e);
      }
    }

    return reports;
  }

  private List<Course> getCourses(DepartmentId departmentId, LocalDate startDate, LocalDate endDate)
  {
    LmsUser lmsUser = lmsUserService.getCurrentUser();
    Set<String> departments = departmentService.getSubDepartments(departmentId.getId());
    departments.addAll(departmentService.getParentDepartments(departmentId.getId()));

    if (lmsUser instanceof UnitelAdmin)
    {
      List<Course> courses = new ArrayList<>();
      departments.forEach(department -> courses.addAll(courseRepository.listAll(DepartmentId.valueOf(department), startDate, endDate)));
      return getCoursesWithoutDuplicates(courses);
    }
    else if (lmsUser instanceof UnitelSupervisor || lmsUser instanceof UnitelManager)
    {
      List<Course> courses = new ArrayList<>();
      departments.forEach(department -> courses.addAll(courseRepository.listAllByEnrolledDepartments(department, lmsUser.getId().getId(), startDate, endDate)));
      return getCoursesWithoutDuplicates(courses);
    }
    else
    {
      return Collections.emptyList();
    }
  }

  private List<Course> getCoursesWithoutRuntime(DepartmentId departmentId, LocalDate startDate, LocalDate endDate)
  {
    LmsUser lmsUser = lmsUserService.getCurrentUser();
    Set<String> departments = departmentService.getSubDepartments(departmentId.getId());

    if (lmsUser instanceof UnitelAdmin)
    {
      List<Course> courses = new ArrayList<>();
      departments.forEach(department -> courses.addAll(courseRepository.listAll(DepartmentId.valueOf(department), startDate, endDate)));
      return getCoursesWithoutDuplicates(courses);
    }
    else if (lmsUser instanceof UnitelSupervisor || lmsUser instanceof UnitelManager)
    {
      List<Course> courses = new ArrayList<>();
      departments.forEach(department -> courses.addAll(courseRepository.listAllByEnrolledDepartments(department, lmsUser.getId().getId(), startDate, endDate)));
      return getCoursesWithoutDuplicates(courses);
    }
    else
    {
      return Collections.emptyList();
    }
  }

  private List<Course> getCourses(DepartmentId departmentId, CourseType courseType, LocalDate startDate, LocalDate endDate)
  {
    LmsUser lmsUser = lmsUserService.getCurrentUser();
    Set<String> departments = departmentService.getSubDepartments(departmentId.getId());
    departments.addAll(departmentService.getParentDepartments(departmentId.getId()));

    if (lmsUser instanceof UnitelAdmin)
    {
      List<Course> courses = new ArrayList<>();
      departments.forEach(department -> courses.addAll(courseRepository.listAll(DepartmentId.valueOf(department), courseType, startDate, endDate)));
      return getCoursesWithoutDuplicates(courses);
    }
    else if (lmsUser instanceof UnitelSupervisor || lmsUser instanceof UnitelManager)
    {
      List<Course> courses = new ArrayList<>();
      departments.forEach(
          department -> courses.addAll(courseRepository.listAllByEnrolledDepartments(department, lmsUser.getId().getId(), courseType, startDate, endDate)));
      return getCoursesWithoutDuplicates(courses);
    }
    else
    {
      return Collections.emptyList();
    }
  }

  private List<Course> getCourses(DepartmentId departmentId, CourseCategoryId courseCategoryId, LocalDate startDate,
      LocalDate endDate)
  {
    LmsUser lmsUser = lmsUserService.getCurrentUser();
    Set<String> departments = departmentService.getSubDepartments(departmentId.getId());
    departments.addAll(departmentService.getParentDepartments(departmentId.getId()));

    if (lmsUser instanceof UnitelAdmin)
    {
      List<Course> courses = new ArrayList<>();
      departments.forEach(
          department -> courses.addAll(courseRepository.listAll(DepartmentId.valueOf(department), courseCategoryId, startDate, endDate)));
      return getCoursesWithoutDuplicates(courses);
    }
    else if (lmsUser instanceof UnitelSupervisor || lmsUser instanceof UnitelManager)
    {
      List<Course> courses = new ArrayList<>();
      departments.forEach(department -> courses
          .addAll(courseRepository.listAllByEnrolledDepartments(department, lmsUser.getId().getId(), courseCategoryId, startDate, endDate)));
      return getCoursesWithoutDuplicates(courses);
    }
    else
    {
      return Collections.emptyList();
    }
  }
  private List<Course> getCoursesWithoutRuntime(DepartmentId departmentId, CourseCategoryId courseCategoryId, LocalDate startDate,
      LocalDate endDate)
  {
    LmsUser lmsUser = lmsUserService.getCurrentUser();
    Set<String> departments = departmentService.getSubDepartments(departmentId.getId());

    if (lmsUser instanceof UnitelAdmin)
    {
      List<Course> courses = new ArrayList<>();
      departments.forEach(
          department -> courses.addAll(courseRepository.listAll(DepartmentId.valueOf(department), courseCategoryId, startDate, endDate)));
      return getCoursesWithoutDuplicates(courses);
    }
    else if (lmsUser instanceof UnitelSupervisor || lmsUser instanceof UnitelManager)
    {
      List<Course> courses = new ArrayList<>();
      departments.forEach(department -> courses
          .addAll(courseRepository.listAllByEnrolledDepartments(department, lmsUser.getId().getId(), courseCategoryId, startDate, endDate)));
      return getCoursesWithoutDuplicates(courses);
    }
    else
    {
      return Collections.emptyList();
    }
  }

  private List<Course> getCourses(DepartmentId departmentId, CourseCategoryId courseCategoryId, CourseType courseType,
      LocalDate startDate, LocalDate endDate)
  {
    LmsUser lmsUser = lmsUserService.getCurrentUser();
    Set<String> departments = departmentService.getSubDepartments(departmentId.getId());
    departments.addAll(departmentService.getParentDepartments(departmentId.getId()));

    if (lmsUser instanceof UnitelAdmin)
    {
      List<Course> courses = new ArrayList<>();
      departments.forEach(department -> courses.addAll(courseRepository.listAll(DepartmentId.valueOf(department), courseCategoryId,
          courseType, startDate, endDate)));
      return getCoursesWithoutDuplicates(courses);
    }
    else if (lmsUser instanceof UnitelSupervisor || lmsUser instanceof UnitelManager)
    {
      List<Course> courses = new ArrayList<>();
      departments
          .forEach(department -> courses
              .addAll(courseRepository.listAllByEnrolledDepartments(department, lmsUser.getId().getId(), courseCategoryId, courseType, startDate, endDate)));
      return getCoursesWithoutDuplicates(courses);
    }
    else
    {
      return Collections.emptyList();
    }
  }

  @NotNull
  private List<Course> getCoursesWithoutDuplicates(List<Course> courses)
  {
    Set<String> duplicateSet = new HashSet<>();
    List<Course> removedDuplicates = courses.stream().filter(e -> duplicateSet.add(e.getCourseId().getId())).collect(Collectors.toList());
    removedDuplicates = new ArrayList<>(removedDuplicates);
    return removedDuplicates;
  }
}

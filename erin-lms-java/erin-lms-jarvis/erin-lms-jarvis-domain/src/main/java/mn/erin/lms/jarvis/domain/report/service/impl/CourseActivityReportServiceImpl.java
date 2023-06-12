package mn.erin.lms.jarvis.domain.report.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.lms.base.aim.user.LmsEmployee;
import mn.erin.lms.base.domain.model.category.CourseCategoryId;
import mn.erin.lms.base.domain.model.course.Course;
import mn.erin.lms.base.domain.model.course.CourseId;
import mn.erin.lms.base.domain.model.enrollment.CourseEnrollment;
import mn.erin.lms.base.aim.organization.DepartmentId;
import mn.erin.lms.base.aim.organization.OrganizationId;
import mn.erin.lms.base.aim.user.LearnerId;
import mn.erin.lms.base.aim.user.LmsUser;
import mn.erin.lms.base.domain.repository.CourseCategoryRepository;
import mn.erin.lms.base.domain.repository.CourseEnrollmentRepository;
import mn.erin.lms.base.domain.repository.CourseRepository;
import mn.erin.lms.base.aim.LmsDepartmentService;
import mn.erin.lms.base.aim.LmsUserService;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.service.OrganizationIdProvider;
import mn.erin.lms.jarvis.domain.report.model.CourseReport;
import mn.erin.lms.jarvis.domain.report.repository.CourseReportRepository;

/**
 *
 * @author Munkh
 */
public class CourseActivityReportServiceImpl implements mn.erin.lms.jarvis.domain.report.service.CourseActivityReportService
{
  private static final Logger LOGGER = LoggerFactory.getLogger(CourseActivityReportServiceImpl.class);

  private final LmsUserService userService;
  private final LmsDepartmentService departmentService;
  private final OrganizationIdProvider organizationIdProvider;

  private CourseRepository courseRepository;
  private CourseReportRepository courseReportRepository;
  private CourseEnrollmentRepository courseEnrollmentRepository;
  private CourseCategoryRepository courseCategoryRepository;

  public CourseActivityReportServiceImpl(LmsUserService userService, LmsDepartmentService departmentService, OrganizationIdProvider organizationIdProvider)
  {
    this.userService = userService;
    this.departmentService = departmentService;
    this.organizationIdProvider = organizationIdProvider;
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

  @Inject
  public void setCourseEnrollmentRepository(CourseEnrollmentRepository courseEnrollmentRepository)
  {
    this.courseEnrollmentRepository = courseEnrollmentRepository;
  }

  @Inject
  public void setCourseCategoryRepository(CourseCategoryRepository courseCategoryRepository)
  {
    this.courseCategoryRepository = courseCategoryRepository;
  }

  @Override
  public List<CourseReport> generateCourseReports(DepartmentId departmentId, LearnerId learnerId, LocalDate startDate, LocalDate endDate)
  {
    List<Course> courses = getCourses(departmentId, learnerId, startDate, endDate);

    List<CourseReport> reports = new ArrayList<>();
    for(Course course: courses)
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

  private List<Course> getCourses(DepartmentId departmentId, LearnerId learnerId, LocalDate startDate, LocalDate endDate)
  {
    LmsUser lmsUser = userService.getCurrentUser();

    if (!(lmsUser instanceof LmsEmployee))
    {
      List<Course> courses = new ArrayList<>(courseRepository.listAllByEnrolledDepartments(departmentId.getId(), learnerId.getId(), startDate, endDate));
      Set<String> parentDepartments = departmentService.getParentDepartments(departmentId.getId());
      for(String parentDepartment: parentDepartments)
      {
        courses.addAll(courseRepository.listAllByEnrolledDepartments(parentDepartment, learnerId.getId(), startDate, endDate));
      }
      List<CourseId> enrolledCourses = courseEnrollmentRepository.listAll(learnerId).stream().map(CourseEnrollment::getCourseId).collect(Collectors.toList());
      courses.removeIf(course -> !enrolledCourses.contains(course.getCourseId()));

      Set<String> categories = this.courseCategoryRepository.listAll(OrganizationId.valueOf(organizationIdProvider.getOrganizationId()),
          CourseCategoryId.valueOf("online-course")).stream().map(category -> category.getCourseCategoryId().getId()).collect(Collectors.toSet());
      courses.removeIf(course -> !categories.contains(course.getCourseCategoryId().getId()));

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

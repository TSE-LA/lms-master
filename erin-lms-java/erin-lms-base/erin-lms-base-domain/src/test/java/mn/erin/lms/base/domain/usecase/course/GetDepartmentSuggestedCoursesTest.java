package mn.erin.lms.base.domain.usecase.course;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.LmsDepartmentService;
import mn.erin.lms.base.aim.organization.DepartmentId;
import mn.erin.lms.base.aim.user.AuthorId;
import mn.erin.lms.base.domain.model.category.CourseCategoryId;
import mn.erin.lms.base.domain.model.content.CourseContent;
import mn.erin.lms.base.domain.model.content.CourseContentId;
import mn.erin.lms.base.domain.model.course.Course;
import mn.erin.lms.base.domain.model.course.CourseDepartmentRelation;
import mn.erin.lms.base.domain.model.course.CourseDetail;
import mn.erin.lms.base.domain.model.course.CourseId;
import mn.erin.lms.base.domain.model.course.DateInfo;
import mn.erin.lms.base.domain.repository.CourseCategoryRepository;
import mn.erin.lms.base.domain.repository.CourseContentRepository;
import mn.erin.lms.base.domain.repository.CourseRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.course.dto.CourseDto;
import mn.erin.lms.base.domain.usecase.course.dto.GetCoursesInput;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @author Dashnyam Bayarsaikhan
 */
public class GetDepartmentSuggestedCoursesTest
{
  private final int TOTAL = 10;
  private LmsDepartmentService lmsDepartmentService;
  private CourseContentRepository courseContentRepository;
  private GetDepartmentSuggestedCourses getDepartmentSuggestedCourses;
  private CourseRepository courseRepository;

  private List<Course> courses = new ArrayList<>();
  private List<CourseDto> courseDtos = new ArrayList<>();
  private Set<String> departments = new HashSet<>();

  @Before
  public void setUp() throws Exception
  {
    LmsRepositoryRegistry lmsRepositoryRegistry = mock(LmsRepositoryRegistry.class);
    LmsServiceRegistry lmsServiceRegistry = mock(LmsServiceRegistry.class);
    lmsDepartmentService = mock(LmsDepartmentService.class);
    courseContentRepository = mock(CourseContentRepository.class);
    courseRepository = mock(CourseRepository.class);
    CourseCategoryRepository courseCategoryRepository = mock(CourseCategoryRepository.class);
    when(lmsRepositoryRegistry.getCourseRepository()).thenReturn(courseRepository);
    when(lmsServiceRegistry.getDepartmentService()).thenReturn(lmsDepartmentService);
    when(lmsRepositoryRegistry.getCourseContentRepository()).thenReturn(courseContentRepository);
    when(courseCategoryRepository.getCourseCategoryNameById(Mockito.any())).thenReturn("CategoryName");

    courses = new ArrayList<>();
    courseDtos = new ArrayList<>();
    departments = new HashSet<>();
    for (int i = 0; i < TOTAL; i++)
    {
      Course course = new Course(CourseId.valueOf("courseId" + i), CourseCategoryId.valueOf("categoryId"), new CourseDetail("Title"),
          AuthorId.valueOf("authorId"),
          new CourseDepartmentRelation(DepartmentId.valueOf("departmentId")));

      DateInfo dateInfo = new DateInfo();
      dateInfo.setCreatedDate(LocalDateTime.now());
      dateInfo.setModifiedDate(LocalDateTime.now());
      dateInfo.setPublishDate(LocalDateTime.now());
      course.getCourseDetail().setDateInfo(dateInfo);
      course.setCourseCategoryName("CategoryName");
      courses.add(course);
      departments.add(course.getCourseDepartmentRelation().getCourseDepartment().getId());
      courseDtos.add(CourseTestUtils.generateCourseDto(course, course.getCourseDetail()));
    }

    getDepartmentSuggestedCourses = new GetDepartmentSuggestedCourses(lmsRepositoryRegistry, lmsServiceRegistry);
  }

  @Test(expected = NullPointerException.class)
  public void inputNull_throwsNullPointerException() throws UseCaseException
  {
    getDepartmentSuggestedCourses.execute(null);
  }

  @Test(expected = NullPointerException.class)
  public void get_course_input_null_throws_null_pointer_exception() throws UseCaseException
  {
    getDepartmentSuggestedCourses.execute(new GetCoursesInput(null));
  }

  @Test
  public void assert_whenCoursesFound() throws UseCaseException, LmsRepositoryException
  {
    CourseContent courseContent = new CourseContent(CourseContentId.valueOf("courseContentId"));

    when(lmsDepartmentService.getSubDepartments(lmsDepartmentService.getCurrentDepartmentId())).thenReturn(departments);
    when(courseRepository.listAll(any(), (DepartmentId) any())).thenReturn(courses);
    when(courseContentRepository.fetchById(any())).thenReturn(courseContent);

    GetCoursesInput getCoursesInput = new GetCoursesInput("categoryId");
    getCoursesInput.setCourseCount(TOTAL);
    List<CourseDto> returnedCourses = getDepartmentSuggestedCourses.execute(getCoursesInput);

    assertEquals(TOTAL, returnedCourses.size());
  }

  @Test
  public void assertZero_whenCourseNotFound() throws UseCaseException, LmsRepositoryException
  {
    CourseContent courseContent = new CourseContent(CourseContentId.valueOf("courseContentId"));
    List<Course> courses = new ArrayList<>();

    when(lmsDepartmentService.getSubDepartments(lmsDepartmentService.getCurrentDepartmentId())).thenReturn(departments);
    when(courseRepository.listAll(any(), (DepartmentId) any())).thenReturn(courses);
    when(courseContentRepository.fetchById(any())).thenReturn(courseContent);

    GetCoursesInput getCoursesInput = new GetCoursesInput("categoryId");
    getCoursesInput.setCourseCount(TOTAL);
    List<CourseDto> returnedCourses = getDepartmentSuggestedCourses.execute(getCoursesInput);

    assertEquals(0, returnedCourses.size());
  }

  @Test(expected = NullPointerException.class)
  public void coursesNull_throwsNullPointerException() throws UseCaseException, LmsRepositoryException
  {
    CourseContent courseContent = new CourseContent(CourseContentId.valueOf("courseContentId"));
    when(lmsDepartmentService.getSubDepartments(lmsDepartmentService.getCurrentDepartmentId())).thenReturn(departments);
    when(courseRepository.listAll(any(), (DepartmentId) any())).thenReturn(null);
    when(courseContentRepository.fetchById(any())).thenReturn(courseContent);
    GetCoursesInput getCoursesInput = new GetCoursesInput("categoryId");
    getCoursesInput.setCourseCount(TOTAL);
    getDepartmentSuggestedCourses.execute(getCoursesInput);
  }

  @Test
  public void assertZero_whenCoursesEmpty() throws UseCaseException, LmsRepositoryException
  {
    CourseContent courseContent = new CourseContent(CourseContentId.valueOf("courseContentId"));
    List<Course> emptyCourses = new ArrayList<>();
    when(lmsDepartmentService.getSubDepartments(lmsDepartmentService.getCurrentDepartmentId())).thenReturn(departments);
    when(courseRepository.listAll(any(), (DepartmentId) any())).thenReturn(emptyCourses);
    when(courseContentRepository.fetchById(any())).thenReturn(courseContent);
    GetCoursesInput getCoursesInput = new GetCoursesInput("categoryId");
    getCoursesInput.setCourseCount(TOTAL);
    List<CourseDto> returnedCourses = getDepartmentSuggestedCourses.execute(getCoursesInput);
    Assert.assertEquals(0, returnedCourses.size());
  }
}
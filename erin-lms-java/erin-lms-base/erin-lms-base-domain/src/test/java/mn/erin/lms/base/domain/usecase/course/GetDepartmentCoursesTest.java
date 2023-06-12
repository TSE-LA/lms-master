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
import mn.erin.lms.base.domain.model.content.CourseModule;
import mn.erin.lms.base.domain.model.course.Course;
import mn.erin.lms.base.domain.model.course.CourseDepartmentRelation;
import mn.erin.lms.base.domain.model.course.CourseDetail;
import mn.erin.lms.base.domain.model.course.CourseId;
import mn.erin.lms.base.domain.model.course.CourseType;
import mn.erin.lms.base.domain.model.course.DateInfo;
import mn.erin.lms.base.domain.model.course.PublishStatus;
import mn.erin.lms.base.domain.repository.CourseAssessmentRepository;
import mn.erin.lms.base.domain.repository.CourseCategoryRepository;
import mn.erin.lms.base.domain.repository.CourseContentRepository;
import mn.erin.lms.base.domain.repository.CourseRepository;
import mn.erin.lms.base.domain.repository.CourseSuggestedUsersRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.CourseTypeResolver;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.course.dto.CourseDto;
import mn.erin.lms.base.domain.usecase.course.dto.GetCoursesInput;

/**
 * @author Dashnyam Bayarsaikhan
 */
public class GetDepartmentCoursesTest
{
  private final int TOTAL_COURSES = 10;

  private GetDepartmentCourses getDepartmentCourses;
  private GetCoursesInput getCoursesInput;
  private CourseRepository courseRepository;
  private LmsDepartmentService lmsDepartmentService;
  protected CourseTypeResolver courseTypeResolver;
  private CourseContentRepository courseContentRepository;
  protected CourseSuggestedUsersRepository courseSuggestedUsersRepository;
  private List<Course> courses = new ArrayList<>();
  private List<CourseDto> courseDtos = new ArrayList<>();
  private Set<String> departments = new HashSet<>();

  @Before
  public void setUp() throws Exception
  {
    LmsRepositoryRegistry lmsRepositoryRegistry = Mockito.mock(LmsRepositoryRegistry.class);
    LmsServiceRegistry lmsServiceRegistry = Mockito.mock(LmsServiceRegistry.class);
    courseRepository = Mockito.mock(CourseRepository.class);
    lmsDepartmentService = Mockito.mock(LmsDepartmentService.class);
    courseTypeResolver = Mockito.mock(CourseTypeResolver.class);
    courseContentRepository = Mockito.mock(CourseContentRepository.class);
    CourseAssessmentRepository courseAssessmentRepository = Mockito.mock(CourseAssessmentRepository.class);
    courseSuggestedUsersRepository = Mockito.mock(CourseSuggestedUsersRepository.class);
    CourseCategoryRepository courseCategoryRepository = Mockito.mock(CourseCategoryRepository.class);
    Mockito.when(lmsServiceRegistry.getDepartmentService()).thenReturn(lmsDepartmentService);
    Mockito.when(lmsServiceRegistry.getCourseTypeResolver()).thenReturn(courseTypeResolver);
    Mockito.when(lmsRepositoryRegistry.getCourseRepository()).thenReturn(courseRepository);
    Mockito.when(lmsRepositoryRegistry.getCourseAssessmentRepository()).thenReturn(courseAssessmentRepository);
    Mockito.when(lmsRepositoryRegistry.getCourseSuggestedUsersRepository()).thenReturn(courseSuggestedUsersRepository);
    Mockito.when(lmsRepositoryRegistry.getCourseContentRepository()).thenReturn(courseContentRepository);
    Mockito.when(lmsRepositoryRegistry.getCourseCategoryRepository()).thenReturn(courseCategoryRepository);
    Mockito.when(courseCategoryRepository.getCourseCategoryNameById(Mockito.any())).thenReturn("CategoryName");
    String CATEGORY_ID = "CategoryId";
    getCoursesInput = new GetCoursesInput(CATEGORY_ID);

    courses = new ArrayList<>();
    courseDtos = new ArrayList<>();
    departments = new HashSet<>();
    for (int i = 0; i < TOTAL_COURSES; i++)
    {
      Course course = new Course(CourseId.valueOf("courseId" + i), CourseCategoryId.valueOf("categoryId"), new CourseDetail("Title"),
          AuthorId.valueOf("authorId"),
          new CourseDepartmentRelation(DepartmentId.valueOf("departmentId")));

      DateInfo dateInfo = new DateInfo();
      dateInfo.setCreatedDate(LocalDateTime.now());
      dateInfo.setModifiedDate(LocalDateTime.now());
      dateInfo.setPublishDate(LocalDateTime.now());
      course.getCourseDetail().setDateInfo(dateInfo);
      courses.add(course);
      departments.add(course.getCourseDepartmentRelation().getCourseDepartment().getId());
      courseDtos.add(CourseTestUtils.generateCourseDto(course, course.getCourseDetail()));
    }

    getDepartmentCourses = new GetDepartmentCourses(lmsRepositoryRegistry, lmsServiceRegistry);
  }

  @Test(expected = NullPointerException.class)
  public void inputIsNull() throws UseCaseException
  {
    getDepartmentCourses.execute(null);
  }

  @Test(expected = NullPointerException.class)
  public void getDepartmentCourseInputIsNull_throwsNullPointerException() throws UseCaseException
  {
    getDepartmentCourses.execute(new GetCoursesInput(null));
  }

  @Test
  public void assert_whenPublishStatusAndCourseTypeIsPresent() throws UseCaseException, LmsRepositoryException
  {

    getCoursesInput.setCourseType("online-course");
    String PUBLISH_STATUS_UNPUBLISHED = "UNPUBLISHED";
    getCoursesInput.setPublishStatus(PUBLISH_STATUS_UNPUBLISHED);
    CourseCategoryId categoryId = courses.get(0).getCourseCategoryId();
    CourseType courseType = courses.get(0).getCourseType();
    Mockito.when(
        courseRepository.listAll((CourseCategoryId) Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(courses);
    Mockito.when(lmsDepartmentService.getSubDepartments(lmsDepartmentService.getCurrentDepartmentId())).thenReturn(departments);
    Mockito.when(courseRepository.fetchById(Mockito.any())).thenReturn(courses.get(0));
    List<CourseModule> courseModules = new ArrayList<>();
    CourseContent courseContent = new CourseContent(CourseContentId.valueOf("courseContentId"));
    Mockito.when(courseContentRepository.fetchById(Mockito.any())).thenReturn(courseContent);
    List<CourseDto> returnedCourses = getDepartmentCourses.execute(getCoursesInput);

    Assert.assertEquals(TOTAL_COURSES, returnedCourses.size());
  }

  @Test
  public void assert_publishStatusIsPresentCourseTypeisNotPresent() throws LmsRepositoryException, UseCaseException
  {
    getCoursesInput.setCourseType(null);
    String PUBLISH_STATUS_PUBLISHED = "PUBLISHED";
    getCoursesInput.setPublishStatus(PUBLISH_STATUS_PUBLISHED);
    CourseCategoryId categoryId = courses.get(0).getCourseCategoryId();
    CourseType courseType = courses.get(0).getCourseType();
    Mockito.when(
        courseRepository.listAll(Mockito.any(), Mockito.any(PublishStatus.class), (DepartmentId) Mockito.any())).thenReturn(courses);
    Mockito.when(lmsDepartmentService.getSubDepartments(lmsDepartmentService.getCurrentDepartmentId())).thenReturn(departments);
    Mockito.when(courseRepository.fetchById(Mockito.any())).thenReturn(courses.get(0));
    List<CourseModule> courseModules = new ArrayList<>();
    CourseContent courseContent = new CourseContent(CourseContentId.valueOf("courseContentId"));
    Mockito.when(courseContentRepository.fetchById(Mockito.any())).thenReturn(courseContent);
    List<CourseDto> returnedCourses = getDepartmentCourses.execute(getCoursesInput);

    Assert.assertEquals(TOTAL_COURSES, returnedCourses.size());
  }

  @Test
  public void assert_publishStatusIsNotPresent() throws LmsRepositoryException, UseCaseException
  {
    getCoursesInput.setCourseType("online-course");
    getCoursesInput.setPublishStatus(null);
    CourseCategoryId categoryId = courses.get(0).getCourseCategoryId();
    CourseType courseType = courses.get(0).getCourseType();
    Mockito.when(
        courseRepository.listAll(Mockito.any(), (CourseType) Mockito.any(),(DepartmentId) Mockito.any())).thenReturn(courses);
    Mockito.when(lmsDepartmentService.getSubDepartments(lmsDepartmentService.getCurrentDepartmentId())).thenReturn(departments);
    Mockito.when(courseRepository.fetchById(Mockito.any())).thenReturn(courses.get(0));
    List<CourseModule> courseModules = new ArrayList<>();
    CourseContent courseContent = new CourseContent(CourseContentId.valueOf("courseContentId"));
    Mockito.when(courseContentRepository.fetchById(Mockito.any())).thenReturn(courseContent);
    List<CourseDto> returnedCourses = getDepartmentCourses.execute(getCoursesInput);

    Assert.assertEquals(TOTAL_COURSES, returnedCourses.size());
  }

  @Test
  public void assert_publishStatusCourseTypeIsNotPresent() throws LmsRepositoryException, UseCaseException
  {
    getCoursesInput.setCourseType(null);
    getCoursesInput.setPublishStatus(null);
    CourseCategoryId categoryId = courses.get(0).getCourseCategoryId();
    CourseType courseType = courses.get(0).getCourseType();
    Mockito.when(
        courseRepository.listAll(Mockito.any(), (DepartmentId) Mockito.any())).thenReturn(courses);
    Mockito.when(lmsDepartmentService.getSubDepartments(lmsDepartmentService.getCurrentDepartmentId())).thenReturn(departments);
    Mockito.when(courseRepository.fetchById(Mockito.any())).thenReturn(courses.get(0));
    List<CourseModule> courseModules = new ArrayList<>();
    CourseContent courseContent = new CourseContent(CourseContentId.valueOf("courseContentId"));
    Mockito.when(courseContentRepository.fetchById(Mockito.any())).thenReturn(courseContent);
    List<CourseDto> returnedCourses = getDepartmentCourses.execute(getCoursesInput);

    Assert.assertEquals(TOTAL_COURSES, returnedCourses.size());
  }

  @Test(expected = NullPointerException.class)
  public void coursesNull_throwsNullPointerException() throws LmsRepositoryException, UseCaseException
  {
    getCoursesInput.setCourseType(null);
    getCoursesInput.setPublishStatus(null);
    CourseCategoryId categoryId = courses.get(0).getCourseCategoryId();
    CourseType courseType = courses.get(0).getCourseType();
    Mockito.when(
        courseRepository.listAll(Mockito.any(), (DepartmentId) Mockito.any())).thenReturn(null);
    Mockito.when(lmsDepartmentService.getSubDepartments(lmsDepartmentService.getCurrentDepartmentId())).thenReturn(departments);
    Mockito.when(courseRepository.fetchById(Mockito.any())).thenReturn(courses.get(0));
    List<CourseModule> courseModules = new ArrayList<>();
    CourseContent courseContent = new CourseContent(CourseContentId.valueOf("courseContentId"));
    Mockito.when(courseContentRepository.fetchById(Mockito.any())).thenReturn(courseContent);
    List<CourseDto> returnedCourses = getDepartmentCourses.execute(getCoursesInput);
  }
  @Test
  public void assertZero_whenCoursesEmpty() throws LmsRepositoryException, UseCaseException
  {
    getCoursesInput.setCourseType(null);
    getCoursesInput.setPublishStatus(null);

    List<Course> emptyCourse = new ArrayList<>();
    Mockito.when(
        courseRepository.listAll(Mockito.any(), (DepartmentId) Mockito.any())).thenReturn(emptyCourse);
    Mockito.when(lmsDepartmentService.getSubDepartments(lmsDepartmentService.getCurrentDepartmentId())).thenReturn(departments);
    Mockito.when(courseRepository.fetchById(Mockito.any())).thenReturn(courses.get(0));
    List<CourseModule> courseModules = new ArrayList<>();
    CourseContent courseContent = new CourseContent(CourseContentId.valueOf("courseContentId"));
    Mockito.when(courseContentRepository.fetchById(Mockito.any())).thenReturn(courseContent);
    List<CourseDto> returnedCourses = getDepartmentCourses.execute(getCoursesInput);
    Assert.assertEquals(0, returnedCourses.size());
  }
}
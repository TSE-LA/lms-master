package mn.erin.lms.base.domain.usecase.course;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.organization.DepartmentId;
import mn.erin.lms.base.aim.user.AuthorId;
import mn.erin.lms.base.aim.user.LearnerId;
import mn.erin.lms.base.domain.model.category.CourseCategoryId;
import mn.erin.lms.base.domain.model.course.Course;
import mn.erin.lms.base.domain.model.course.CourseDepartmentRelation;
import mn.erin.lms.base.domain.model.course.CourseDetail;
import mn.erin.lms.base.domain.model.course.CourseId;
import mn.erin.lms.base.domain.model.course.CourseType;
import mn.erin.lms.base.domain.model.course.DateInfo;
import mn.erin.lms.base.domain.model.course.PublishStatus;
import mn.erin.lms.base.domain.model.enrollment.CourseEnrollment;
import mn.erin.lms.base.domain.model.online_course.EmployeeType;
import mn.erin.lms.base.domain.model.online_course.ManagerType;
import mn.erin.lms.base.domain.model.online_course.SupervisorType;
import mn.erin.lms.base.domain.repository.CourseCategoryRepository;
import mn.erin.lms.base.domain.repository.CourseEnrollmentRepository;
import mn.erin.lms.base.domain.repository.CourseRepository;
import mn.erin.lms.base.domain.repository.CourseSuggestedUsersRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.CourseTypeResolver;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.service.ProgressTrackingService;
import mn.erin.lms.base.domain.usecase.course.dto.CourseDto;
import mn.erin.lms.base.domain.usecase.course.dto.GetCoursesInput;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @author Dashnyam Bayarsaikhan
 */
public class GetEnrolledCoursesTest
{
  private final int TOTAL = 10;

  private CourseEnrollmentRepository courseEnrollmentRepository;
  private CourseRepository courseRepository;
  protected CourseTypeResolver courseTypeResolver;
  private GetEnrolledCourses getEnrolledCourses;

  private List<Course> courses = new ArrayList<>();
  private List<CourseDto> courseDtos = new ArrayList<>();
  private Set<String> departments = new HashSet<>();

  @Before
  public void setUp() throws Exception
  {
    LmsServiceRegistry lmsServiceRegistry = mock(LmsServiceRegistry.class);
    LmsRepositoryRegistry lmsRepositoryRegistry = mock(LmsRepositoryRegistry.class);
    courseEnrollmentRepository = mock(CourseEnrollmentRepository.class);
    courseRepository = mock(CourseRepository.class);
    courseTypeResolver = mock(CourseTypeResolver.class);
    CourseSuggestedUsersRepository courseSuggestedUsersRepository = mock(CourseSuggestedUsersRepository.class);
    CourseCategoryRepository courseCategoryRepository = mock(CourseCategoryRepository.class);
    ProgressTrackingService progressTrackingService = mock(ProgressTrackingService.class);

    when(lmsServiceRegistry.getCourseTypeResolver()).thenReturn(courseTypeResolver);
    when(lmsRepositoryRegistry.getCourseEnrollmentRepository()).thenReturn(courseEnrollmentRepository);
    when(lmsRepositoryRegistry.getCourseRepository()).thenReturn(courseRepository);
    when(lmsRepositoryRegistry.getCourseSuggestedUsersRepository()).thenReturn(courseSuggestedUsersRepository);
    when(lmsRepositoryRegistry.getCourseCategoryRepository()).thenReturn(courseCategoryRepository);
    when(courseCategoryRepository.getCourseCategoryNameById(any())).thenReturn("CategoryName");
    when(lmsServiceRegistry.getProgressTrackingService()).thenReturn(progressTrackingService);
    when(progressTrackingService.getLearnerProgress(any(), any())).thenReturn(5.2F);

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
      course.setCourseType(new EmployeeType());
      courses.add(course);
      departments.add(course.getCourseDepartmentRelation().getCourseDepartment().getId());
      courseDtos.add(CourseTestUtils.generateCourseDto(course, course.getCourseDetail()));
    }

    getEnrolledCourses = new GetEnrolledCourses(lmsRepositoryRegistry, lmsServiceRegistry);
  }

  @Test(expected = NullPointerException.class)
  public void inputIsNull_throwsNullPointerException() throws UseCaseException
  {
    getEnrolledCourses.execute(null);
  }

  @Test(expected = NullPointerException.class)
  public void whenGetCoursesInputIsNull_throwsNullPointerException() throws UseCaseException
  {
    getEnrolledCourses.execute(new GetCoursesInput(null));
  }

  @Test
  public void assertOne_whencourseTypeIsNotNull_roleEqualsToEmployee() throws UseCaseException
  {
    //    Publish Status is present
    GetCoursesInput getCoursesInput = new GetCoursesInput("categoryId");
    getCoursesInput.setRole("EMPLOYEE");
    getCoursesInput.setLearnerId("learnerId");
    getCoursesInput.setCourseType("MANAGER");
    getCoursesInput.setPublishStatus("PUBLISHED");

    List<CourseEnrollment> courseEnrollments = new ArrayList<>();

    courseEnrollments.add(new CourseEnrollment(CourseId.valueOf("courseId1"), LearnerId.valueOf("learnerId")));

    when(courseEnrollmentRepository.listAll((LearnerId) any())).thenReturn(courseEnrollments);
    when(courseRepository.listAll(any(), any(), (PublishStatus) any()))
        .thenReturn(courses);

    List<CourseDto> returnedCourses = getEnrolledCourses.execute(getCoursesInput);
    assertEquals(1, returnedCourses.size());
  }

  @Test
  public void assertZero_whenAddressIsNullCourseTypeIsManagerOrSupervisor() throws UseCaseException
  {
    //    Publish Status is present
    GetCoursesInput getCoursesInput = new GetCoursesInput("categoryId");
    getCoursesInput.setRole("EMPLOYEE");
    getCoursesInput.setLearnerId("learnerId");
    getCoursesInput.setCourseType("MANAGER");
    getCoursesInput.setPublishStatus("PUBLISHED");

    List<CourseEnrollment> courseEnrollments = new ArrayList<>();

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
      course.setCourseType(i % 2 == 0 ? new SupervisorType() : new ManagerType());
      courses.add(course);
      departments.add(course.getCourseDepartmentRelation().getCourseDepartment().getId());
      courseDtos.add(CourseTestUtils.generateCourseDto(course, course.getCourseDetail()));
    }

    courseEnrollments.add(new CourseEnrollment(CourseId.valueOf("courseId1"), LearnerId.valueOf("learnerId")));

    when(courseEnrollmentRepository.listAll((LearnerId) any())).thenReturn(courseEnrollments);
    when(courseRepository.listAll(any(), any(), (PublishStatus) any()))
        .thenReturn(courses);

    List<CourseDto> returnedCourses = getEnrolledCourses.execute(getCoursesInput);
    assertEquals(0, returnedCourses.size());
  }

  @Test
  public void assert_whenAddressIsNullCourseTypeIsEmpolyeePublishStatusNull() throws UseCaseException
  {
    //    Publish Status is present
    GetCoursesInput getCoursesInput = new GetCoursesInput("categoryId");
    getCoursesInput.setRole("EMPLOYEE");
    getCoursesInput.setLearnerId("learnerId");
    getCoursesInput.setCourseType("MANAGER");
    getCoursesInput.setPublishStatus(null);

    List<CourseEnrollment> courseEnrollments = new ArrayList<>();

    courseEnrollments.add(new CourseEnrollment(CourseId.valueOf("courseId1"), LearnerId.valueOf("learnerId")));
    courseEnrollments.add(new CourseEnrollment(CourseId.valueOf("courseId2"), LearnerId.valueOf("learnerId")));
    courseEnrollments.add(new CourseEnrollment(CourseId.valueOf("courseId3"), LearnerId.valueOf("learnerId")));

    when(courseEnrollmentRepository.listAll((LearnerId) any())).thenReturn(courseEnrollments);
    when(courseRepository.listAll(any(), (CourseType) any()))
        .thenReturn(courses);

    List<CourseDto> returnedCourses = getEnrolledCourses.execute(getCoursesInput);
    assertEquals(3, returnedCourses.size());
  }

  @Test
  public void assert_whenAddressIsNullCourseTypeIsEmpolyeeCourseTypeIsNull() throws UseCaseException
  {
    //    Publish Status is present
    GetCoursesInput getCoursesInput = new GetCoursesInput("categoryId");
    getCoursesInput.setRole("EMPLOYEE");
    getCoursesInput.setLearnerId("learnerId");
    getCoursesInput.setCourseType(null);
    getCoursesInput.setPublishStatus("PUBLISHED");

    List<CourseEnrollment> courseEnrollments = new ArrayList<>();

    courseEnrollments.add(new CourseEnrollment(CourseId.valueOf("courseId1"), LearnerId.valueOf("learnerId")));
    courseEnrollments.add(new CourseEnrollment(CourseId.valueOf("courseId2"), LearnerId.valueOf("learnerId")));
    courseEnrollments.add(new CourseEnrollment(CourseId.valueOf("courseId3"), LearnerId.valueOf("learnerId")));

    when(courseEnrollmentRepository.listAll((LearnerId) any())).thenReturn(courseEnrollments);
    when(courseRepository.listAll(any(), (PublishStatus) any()))
        .thenReturn(courses);

    List<CourseDto> returnedCourses = getEnrolledCourses.execute(getCoursesInput);
    assertEquals(3, returnedCourses.size());
  }

  @Test
  public void assert_whenAddressIsNullCourseTypeIsManager() throws UseCaseException
  {
    //    Publish Status is present
    GetCoursesInput getCoursesInput = new GetCoursesInput("categoryId");
    getCoursesInput.setRole("SUPERVISOR");
    getCoursesInput.setLearnerId("learnerId");
    getCoursesInput.setCourseType(null);
    getCoursesInput.setPublishStatus("PUBLISHED");

    List<CourseEnrollment> courseEnrollments = new ArrayList<>();

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
      course.setCourseType(new ManagerType());
      courses.add(course);
      departments.add(course.getCourseDepartmentRelation().getCourseDepartment().getId());
      courseDtos.add(CourseTestUtils.generateCourseDto(course, course.getCourseDetail()));
    }

    courseEnrollments.add(new CourseEnrollment(CourseId.valueOf("courseId1"), LearnerId.valueOf("learnerId")));
    courseEnrollments.add(new CourseEnrollment(CourseId.valueOf("courseId2"), LearnerId.valueOf("learnerId")));
    courseEnrollments.add(new CourseEnrollment(CourseId.valueOf("courseId3"), LearnerId.valueOf("learnerId")));

    when(courseEnrollmentRepository.listAll((LearnerId) any())).thenReturn(courseEnrollments);
    when(courseRepository.listAll(any(), (PublishStatus) any()))
        .thenReturn(courses);

    List<CourseDto> returnedCourses = getEnrolledCourses.execute(getCoursesInput);
    assertEquals(0, returnedCourses.size());
  }

  @Test
  public void assert_whenCourseTypeIsManager() throws UseCaseException
  {
    //    Publish Status is present
    GetCoursesInput getCoursesInput = new GetCoursesInput("categoryId");
    getCoursesInput.setRole("SUPERVISOR");
    getCoursesInput.setLearnerId("learnerId");
    getCoursesInput.setCourseType("courseType");
    getCoursesInput.setPublishStatus("PUBLISHED");

    List<CourseEnrollment> courseEnrollments = new ArrayList<>();

    courseEnrollments.add(new CourseEnrollment(CourseId.valueOf("courseId1"), LearnerId.valueOf("learnerId")));
    courseEnrollments.add(new CourseEnrollment(CourseId.valueOf("courseId2"), LearnerId.valueOf("learnerId")));
    courseEnrollments.add(new CourseEnrollment(CourseId.valueOf("courseId3"), LearnerId.valueOf("learnerId")));

    when(courseEnrollmentRepository.listAll((LearnerId) any())).thenReturn(courseEnrollments);
    when(courseRepository.listAll(any(), any(), (PublishStatus) any()))
        .thenReturn(courses);

    List<CourseDto> returnedCourses = getEnrolledCourses.execute(getCoursesInput);
    assertEquals(3, returnedCourses.size());
  }

  @Test
  public void coursesEmpty_throwsNullPointerException() throws UseCaseException
  {
    //    Publish Status is present
    GetCoursesInput getCoursesInput = new GetCoursesInput("categoryId");
    getCoursesInput.setRole("SUPERVISOR");
    getCoursesInput.setLearnerId("learnerId");
    getCoursesInput.setCourseType("courseType");
    getCoursesInput.setPublishStatus("PUBLISHED");

    List<CourseEnrollment> courseEnrollments = new ArrayList<>();
    List<Course> emptyCourses = new ArrayList<>();
    courseEnrollments.add(new CourseEnrollment(CourseId.valueOf("courseId1"), LearnerId.valueOf("learnerId")));
    courseEnrollments.add(new CourseEnrollment(CourseId.valueOf("courseId2"), LearnerId.valueOf("learnerId")));
    courseEnrollments.add(new CourseEnrollment(CourseId.valueOf("courseId3"), LearnerId.valueOf("learnerId")));

    when(courseEnrollmentRepository.listAll((LearnerId) any())).thenReturn(courseEnrollments);
    when(courseRepository.listAll(any(), any(), (PublishStatus) any()))
        .thenReturn(emptyCourses);

    List<CourseDto> returnedCourses = getEnrolledCourses.execute(getCoursesInput);
    Assert.assertEquals(0, returnedCourses.size());
  }
}
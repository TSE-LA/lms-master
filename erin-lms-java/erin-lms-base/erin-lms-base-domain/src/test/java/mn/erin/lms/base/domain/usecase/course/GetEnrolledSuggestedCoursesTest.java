package mn.erin.lms.base.domain.usecase.course;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.organization.DepartmentId;
import mn.erin.lms.base.aim.user.AuthorId;
import mn.erin.lms.base.aim.user.LearnerId;
import mn.erin.lms.base.domain.model.category.CourseCategoryId;
import mn.erin.lms.base.domain.model.content.CourseContent;
import mn.erin.lms.base.domain.model.content.CourseContentId;
import mn.erin.lms.base.domain.model.course.Course;
import mn.erin.lms.base.domain.model.course.CourseDepartmentRelation;
import mn.erin.lms.base.domain.model.course.CourseDetail;
import mn.erin.lms.base.domain.model.course.CourseId;
import mn.erin.lms.base.domain.model.course.DateInfo;
import mn.erin.lms.base.domain.model.enrollment.CourseEnrollment;
import mn.erin.lms.base.domain.repository.CourseContentRepository;
import mn.erin.lms.base.domain.repository.CourseEnrollmentRepository;
import mn.erin.lms.base.domain.repository.CourseRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.service.ProgressTrackingService;
import mn.erin.lms.base.domain.usecase.course.dto.CourseDto;
import mn.erin.lms.base.domain.usecase.course.dto.GetCoursesInput;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @author Dashnyam Bayarsaikhan
 */

public class GetEnrolledSuggestedCoursesTest
{
  private final int TOTAL = 10;

  private CourseContentRepository courseContentRepository;
  private CourseRepository courseRepository;
  private GetEnrolledSuggestedCourses getEnrolledSuggestedCourses;
  private CourseEnrollmentRepository courseEnrollmentRepository;

  private List<Course> courses = new ArrayList<>();

  @Before
  public void setUp() throws Exception
  {
    LmsRepositoryRegistry lmsRepositoryRegistry = mock(LmsRepositoryRegistry.class);
    LmsServiceRegistry lmsServiceRegistry = mock(LmsServiceRegistry.class);
    courseEnrollmentRepository = mock(CourseEnrollmentRepository.class);
    courseContentRepository = mock(CourseContentRepository.class);
    courseRepository = mock(CourseRepository.class);
    ProgressTrackingService progressTrackingService = mock(ProgressTrackingService.class);

    when(lmsServiceRegistry.getProgressTrackingService()).thenReturn(progressTrackingService);
    when(progressTrackingService.getLearnerProgress(any(), any())).thenReturn(5.5F);
    when(lmsRepositoryRegistry.getCourseRepository()).thenReturn(courseRepository);
    when(lmsRepositoryRegistry.getCourseEnrollmentRepository()).thenReturn(courseEnrollmentRepository);
    when(lmsRepositoryRegistry.getCourseContentRepository()).thenReturn(courseContentRepository);

    List<CourseEnrollment> courseEnrollmentList = new ArrayList<>();
    courseEnrollmentList.add(new CourseEnrollment(CourseId.valueOf("courseId1"), LearnerId.valueOf("learnerId")));
    courseEnrollmentList.add(new CourseEnrollment(CourseId.valueOf("courseId2"), LearnerId.valueOf("learnerId")));
    courseEnrollmentList.add(new CourseEnrollment(CourseId.valueOf("courseId3"), LearnerId.valueOf("learnerId")));

    when(courseEnrollmentRepository.listAll((LearnerId) any())).thenReturn(courseEnrollmentList);

    courses = new ArrayList<>();
    List<CourseDto> courseDtos = new ArrayList<>();
    Set<String> departments = new HashSet<>();
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
      //      course.setCourseType(new EmployeeType());
      courses.add(course);
      departments.add(course.getCourseDepartmentRelation().getCourseDepartment().getId());
      courseDtos.add(CourseTestUtils.generateCourseDto(course, course.getCourseDetail()));
    }

    getEnrolledSuggestedCourses = new GetEnrolledSuggestedCourses(lmsRepositoryRegistry, lmsServiceRegistry);
  }

  @Test(expected = NullPointerException.class)
  public void inputIsNull_throwsNullPointerException() throws UseCaseException
  {
    getEnrolledSuggestedCourses.execute(null);
  }

  @Test(expected = NullPointerException.class)
  public void whenGetCoursesInputIsNull_throwsNullPointerException() throws UseCaseException
  {
    getEnrolledSuggestedCourses.execute(new GetCoursesInput(null));
  }

  @Test
  public void assertZero_whenCourseCategoryIdNotEqualToInputCategoryId() throws LmsRepositoryException, UseCaseException
  {
    GetCoursesInput getCoursesInput = new GetCoursesInput("categoryIdNotEqual");
    getCoursesInput.setCourseCount(10);
    getCoursesInput.setLearnerId("learnerId");
    CourseContent courseContent = new CourseContent(CourseContentId.valueOf("courseContentId"));

    when(courseRepository.fetchById(any())).thenReturn(courses.get(0));
    when(courseContentRepository.fetchById(any())).thenReturn(courseContent);

    List<CourseDto> returnedCourses = getEnrolledSuggestedCourses.execute(getCoursesInput);
    assertEquals(0, returnedCourses.size());
  }

  @Test

  public void assert_courseCategoryIdEqualToInputCategoryId() throws LmsRepositoryException, UseCaseException
  {
    GetCoursesInput getCoursesInput = new GetCoursesInput("categoryId");
    getCoursesInput.setCourseCount(10);
    getCoursesInput.setLearnerId("learnerId");
    CourseContent courseContent = new CourseContent(CourseContentId.valueOf("courseContentId"));

    when(courseRepository.fetchById(any())).thenReturn(courses.get(1));
    when(courseContentRepository.fetchById(any())).thenReturn(courseContent);

    List<CourseDto> returnedCourses = getEnrolledSuggestedCourses.execute(getCoursesInput);
    assertEquals(3, returnedCourses.size());
  }

  @Test
  public void assert_inputCourseCountIsLowerThanCourseSize() throws LmsRepositoryException, UseCaseException
  {
    GetCoursesInput getCoursesInput = new GetCoursesInput("categoryId");
    getCoursesInput.setCourseCount(0);
    getCoursesInput.setLearnerId("learnerId");
    CourseContent courseContent = new CourseContent(CourseContentId.valueOf("courseContentId"));

    when(courseRepository.fetchById(any())).thenReturn(courses.get(1));
    when(courseContentRepository.fetchById(any())).thenReturn(courseContent);

    List<CourseDto> returnedCourses = getEnrolledSuggestedCourses.execute(getCoursesInput);
    assertEquals(0, returnedCourses.size());
  }
}
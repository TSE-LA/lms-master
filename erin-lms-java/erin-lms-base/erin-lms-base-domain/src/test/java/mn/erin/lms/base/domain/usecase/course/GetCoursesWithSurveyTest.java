package mn.erin.lms.base.domain.usecase.course;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.organization.DepartmentId;
import mn.erin.lms.base.aim.user.AuthorId;
import mn.erin.lms.base.domain.model.assessment.AssessmentId;
import mn.erin.lms.base.domain.model.category.CourseCategoryId;
import mn.erin.lms.base.domain.model.course.Course;
import mn.erin.lms.base.domain.model.course.CourseDepartmentRelation;
import mn.erin.lms.base.domain.model.course.CourseDetail;
import mn.erin.lms.base.domain.model.course.CourseId;
import mn.erin.lms.base.domain.model.course.DateInfo;
import mn.erin.lms.base.domain.repository.CourseCategoryRepository;
import mn.erin.lms.base.domain.repository.CourseRepository;
import mn.erin.lms.base.domain.repository.CourseSuggestedUsersRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.course.dto.CourseDto;

/**
 * @author Dashnyam Bayarsaikhan
 */
public class GetCoursesWithSurveyTest
{
  private GetCoursesWithSurvey getCoursesWithSurvey;
  private CourseRepository courseRepository;
  private CourseSuggestedUsersRepository courseSuggestedUsersRepository;

  @Before
  public void setUp() throws Exception
  {
    LmsRepositoryRegistry lmsRepositoryRegistry = Mockito.mock(LmsRepositoryRegistry.class);
    LmsServiceRegistry lmsServiceRegistry = Mockito.mock(LmsServiceRegistry.class);
    courseRepository = Mockito.mock(CourseRepository.class);
    courseSuggestedUsersRepository = Mockito.mock(CourseSuggestedUsersRepository.class);
    CourseCategoryRepository courseCategoryRepository = Mockito.mock(CourseCategoryRepository.class);
    Mockito.when(lmsRepositoryRegistry.getCourseRepository()).thenReturn(courseRepository);
    Mockito.when(lmsRepositoryRegistry.getCourseSuggestedUsersRepository()).thenReturn(courseSuggestedUsersRepository);
    Mockito.when(lmsRepositoryRegistry.getCourseCategoryRepository()).thenReturn(courseCategoryRepository);
    String CATEGORY_NAME = "CategoryName";
    Mockito.when(courseCategoryRepository.getCourseCategoryNameById(Mockito.any())).thenReturn(CATEGORY_NAME);
    getCoursesWithSurvey = new GetCoursesWithSurvey(lmsRepositoryRegistry, lmsServiceRegistry);
  }

  @Test(expected = NullPointerException.class)
  public void whenInputIsNull() throws UseCaseException
  {
    getCoursesWithSurvey.execute(null);
  }

  @Test(expected = NullPointerException.class)
  public void whenCoursesNull_throwsNullPointerException() throws UseCaseException
  {
    Mockito.when(courseRepository.listAll(AssessmentId.valueOf("AssessmentId"))).thenReturn(null);
    List<CourseDto> returnedCourses = getCoursesWithSurvey.execute("AssessmentId");
  }

  @Test
  public void getCourseWithSurvey_success() throws UseCaseException
  {
    Set<String> suggestedUsers = new HashSet<>(Arrays.asList("user1", "user2", "user3"));
    List<Course> courses = new ArrayList<>();
    List<CourseDto> courseDtos = new ArrayList<>();

    Course mockCourse = Mockito.mock(Course.class);
    CourseDetail courseDetail = Mockito.mock(CourseDetail.class);
    DateInfo dateInfo = new DateInfo();
    courseDetail.setDateInfo(dateInfo);
    int MAX_COURSES = 10;
    for (int i = 0; i < MAX_COURSES; i++)
    {
      Course course = new Course(CourseId.valueOf("courseId" + i), CourseCategoryId.valueOf("categoryId"), new CourseDetail("Title"),
          AuthorId.valueOf("authorId"),
          new CourseDepartmentRelation(DepartmentId.valueOf("departmentId")));
      courses.add(course);
      DateInfo dateInfo1 = new DateInfo();
      dateInfo1.setCreatedDate(LocalDateTime.now());
      dateInfo1.setModifiedDate(LocalDateTime.now());
      dateInfo1.setPublishDate(LocalDateTime.now());
      course.getCourseDetail().setDateInfo(dateInfo1);
      courseDtos.add(CourseTestUtils.generateCourseDto(course, course.getCourseDetail()));
    }
    Mockito.when(mockCourse.getCourseDetail()).thenReturn(courseDetail);
    Mockito.when(courseRepository.listAll(AssessmentId.valueOf("AssessmentId"))).thenReturn(courses);
    Mockito.when(courseSuggestedUsersRepository.getSuggestedUsers("courseId")).thenReturn(suggestedUsers);
    List<CourseDto> returnedCourses = getCoursesWithSurvey.execute("AssessmentId");

    Assert.assertEquals(MAX_COURSES, returnedCourses.size());
  }

  @Test(expected = NullPointerException.class)
  public void coursesNull_throwsNullPointerException() throws UseCaseException
  {
    Set<String> suggestedUsers = new HashSet<>();
    suggestedUsers.addAll(Arrays.asList(new String[] { "user1", "user2", "user3" }));

    Course mockCourse = Mockito.mock(Course.class);
    CourseDetail courseDetail = Mockito.mock(CourseDetail.class);
    DateInfo dateInfo = new DateInfo();
    courseDetail.setDateInfo(dateInfo);

    Mockito.when(mockCourse.getCourseDetail()).thenReturn(courseDetail);
    Mockito.when(courseRepository.listAll(AssessmentId.valueOf("AssessmentId"))).thenReturn(null);
    Mockito.when(courseSuggestedUsersRepository.getSuggestedUsers("courseId")).thenReturn(suggestedUsers);
    getCoursesWithSurvey.execute("AssessmentId");
  }

  @Test
  public void assertZero_whenCoursesEmpty() throws UseCaseException
  {
    Set<String> suggestedUsers = new HashSet<>();
    suggestedUsers.addAll(Arrays.asList(new String[] { "user1", "user2", "user3" }));
    List<Course> courses = new ArrayList<>();
    Course mockCourse = Mockito.mock(Course.class);
    CourseDetail courseDetail = Mockito.mock(CourseDetail.class);
    DateInfo dateInfo = new DateInfo();
    courseDetail.setDateInfo(dateInfo);

    Mockito.when(mockCourse.getCourseDetail()).thenReturn(courseDetail);
    Mockito.when(courseRepository.listAll(AssessmentId.valueOf("AssessmentId"))).thenReturn(courses);
    Mockito.when(courseSuggestedUsersRepository.getSuggestedUsers("courseId")).thenReturn(suggestedUsers);
    List<CourseDto> retunedCourses = getCoursesWithSurvey.execute("AssessmentId");
    Assert.assertEquals(0, retunedCourses.size());
  }
}
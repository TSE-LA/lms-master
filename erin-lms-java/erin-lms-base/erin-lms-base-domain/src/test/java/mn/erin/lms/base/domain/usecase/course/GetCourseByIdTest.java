package mn.erin.lms.base.domain.usecase.course;

import java.time.LocalDateTime;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.organization.DepartmentId;
import mn.erin.lms.base.aim.user.AuthorId;
import mn.erin.lms.base.domain.model.category.CourseCategoryId;
import mn.erin.lms.base.domain.model.course.Course;
import mn.erin.lms.base.domain.model.course.CourseDepartmentRelation;
import mn.erin.lms.base.domain.model.course.CourseDetail;
import mn.erin.lms.base.domain.model.course.CourseId;
import mn.erin.lms.base.domain.model.course.DateInfo;
import mn.erin.lms.base.domain.repository.CourseCategoryRepository;
import mn.erin.lms.base.domain.repository.CourseRepository;
import mn.erin.lms.base.domain.repository.CourseSuggestedUsersRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.course.dto.CourseDto;

/**
 * @author Temuulen Naranbold
 */
@RunWith(MockitoJUnitRunner.class)
public class GetCourseByIdTest
{
  public static final String COURSE_ID = "courseId";
  public static final String COURSE_DETAIL_TITLE = "title";
  public static final String AUTHOR_ID = "authorId";
  public static final String DEPARTMENT_ID = "departmentId";
  public static final String COURSE_CATEGORY_ID = "courseCategoryId";

  GetCourseById getCourseById;
  private CourseDto expectedCourseDto;

  @Before
  public void setUp() throws LmsRepositoryException
  {
    LmsRepositoryRegistry lmsRepositoryRegistry = Mockito.mock(LmsRepositoryRegistry.class);
    LmsServiceRegistry lmsServiceRegistry = Mockito.mock(LmsServiceRegistry.class);

    CourseCategoryRepository courseCategoryRepository = Mockito.mock(CourseCategoryRepository.class);
    CourseRepository courseRepository = Mockito.mock(CourseRepository.class);
    CourseSuggestedUsersRepository courseSuggestedUsersRepository = Mockito.mock(CourseSuggestedUsersRepository.class);

    Mockito.when(lmsRepositoryRegistry.getCourseCategoryRepository()).thenReturn(courseCategoryRepository);
    Mockito.when(lmsRepositoryRegistry.getCourseRepository()).thenReturn(courseRepository);
    Mockito.when(lmsRepositoryRegistry.getCourseSuggestedUsersRepository()).thenReturn(courseSuggestedUsersRepository);

    Course course = new Course(CourseId.valueOf(COURSE_ID), CourseCategoryId.valueOf(COURSE_CATEGORY_ID),
        new CourseDetail(COURSE_DETAIL_TITLE), AuthorId.valueOf(AUTHOR_ID),
        new CourseDepartmentRelation(DepartmentId.valueOf(DEPARTMENT_ID)));

    DateInfo dateInfo = new DateInfo();
    dateInfo.setCreatedDate(LocalDateTime.now());
    dateInfo.setModifiedDate(LocalDateTime.now());
    dateInfo.setPublishDate(LocalDateTime.now());

    CourseDetail courseDetail = new CourseDetail(COURSE_DETAIL_TITLE);
    courseDetail.setDateInfo(dateInfo);

    Course existingCourse = new Course(CourseId.valueOf(COURSE_ID), CourseCategoryId.valueOf(COURSE_CATEGORY_ID),
        courseDetail, AuthorId.valueOf(AUTHOR_ID), new CourseDepartmentRelation(DepartmentId.valueOf(DEPARTMENT_ID)));

    Mockito.when(courseRepository.fetchById(Mockito.any())).thenReturn(existingCourse);
    Mockito.when(courseSuggestedUsersRepository.getSuggestedUsers(Mockito.any())).thenReturn(Collections.emptySet());
    Mockito.when(courseCategoryRepository.getCourseCategoryNameById(Mockito.any())).thenReturn("CategoryName");

    expectedCourseDto = CourseTestUtils.generateCourseDto(course, courseDetail);
    getCourseById = new GetCourseById(lmsRepositoryRegistry, lmsServiceRegistry);
  }

  @Test
  public void whenCourseDtoIsReturned() throws UseCaseException
  {
    Assert.assertTrue(CourseTestUtils.isEqual(expectedCourseDto, getCourseById.execute(COURSE_ID)));
  }

  @Test(expected = NullPointerException.class)
  public void whenInputIsNull_throws_UseCaseException() throws UseCaseException
  {
    getCourseById.execute(null);
  }
}
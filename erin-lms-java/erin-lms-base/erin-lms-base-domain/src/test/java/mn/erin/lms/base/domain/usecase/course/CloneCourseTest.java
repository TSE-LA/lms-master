package mn.erin.lms.base.domain.usecase.course;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.LmsDepartmentService;
import mn.erin.lms.base.aim.organization.DepartmentId;
import mn.erin.lms.base.aim.user.AuthorId;
import mn.erin.lms.base.domain.model.assessment.CourseAssessment;
import mn.erin.lms.base.domain.model.category.CourseCategoryId;
import mn.erin.lms.base.domain.model.content.CourseContent;
import mn.erin.lms.base.domain.model.content.CourseContentId;
import mn.erin.lms.base.domain.model.course.Course;
import mn.erin.lms.base.domain.model.course.CourseDepartmentRelation;
import mn.erin.lms.base.domain.model.course.CourseDetail;
import mn.erin.lms.base.domain.model.course.CourseId;
import mn.erin.lms.base.domain.model.course.DateInfo;
import mn.erin.lms.base.domain.repository.CourseAssessmentRepository;
import mn.erin.lms.base.domain.repository.CourseCategoryRepository;
import mn.erin.lms.base.domain.repository.CourseContentRepository;
import mn.erin.lms.base.domain.repository.CourseRepository;
import mn.erin.lms.base.domain.repository.CourseSuggestedUsersRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.CourseTypeResolver;
import mn.erin.lms.base.domain.service.LmsFileSystemService;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.service.ThumbnailService;
import mn.erin.lms.base.domain.service.impl.OnlineCourseTypeResolver;
import mn.erin.lms.base.domain.usecase.course.dto.CloneCourseInput;
import mn.erin.lms.base.domain.usecase.course.dto.CourseDto;
import mn.erin.lms.base.domain.usecase.course.dto.CreateCourseInput;

/**
 * @author Galsan Bayart.
 */
public class CloneCourseTest
{
  private CloneCourse cloneCourse;

  private final CreateCourseInput createCourseInput = new CreateCourseInput("Title", "CategoryId", new HashMap<>());
  private final CloneCourseInput cloneCourseInput = new CloneCourseInput("ExistingCourseId", createCourseInput);

  private CourseDto expectedDto;

  private static CourseRepository courseRepository;
  private static CourseAssessmentRepository courseAssessmentRepository;

  @Before
  public void setUp() throws Exception
  {
    LmsRepositoryRegistry lmsRepositoryRegistry = Mockito.mock(LmsRepositoryRegistry.class);
    LmsServiceRegistry lmsServiceRegistry = Mockito.mock(LmsServiceRegistry.class);

    CourseCategoryRepository courseCategoryRepository = Mockito.mock(CourseCategoryRepository.class);
    courseRepository = Mockito.mock(CourseRepository.class);
    ThumbnailService thumbnailService = Mockito.mock(ThumbnailService.class);
    LmsDepartmentService lmsDepartmentService = Mockito.mock(LmsDepartmentService.class);
    LmsFileSystemService lmsFileSystemService = Mockito.mock(LmsFileSystemService.class);
    courseAssessmentRepository = Mockito.mock(CourseAssessmentRepository.class);
    CourseContentRepository courseContentRepository = Mockito.mock(CourseContentRepository.class);
    CourseSuggestedUsersRepository courseSuggestedUsersRepository = Mockito.mock(CourseSuggestedUsersRepository.class);

    Mockito.when(lmsServiceRegistry.getLmsFileSystemService()).thenReturn(lmsFileSystemService);
    Mockito.when(lmsRepositoryRegistry.getCourseCategoryRepository()).thenReturn(courseCategoryRepository);
    Mockito.when(lmsServiceRegistry.getThumbnailService()).thenReturn(thumbnailService);
    Mockito.when(lmsServiceRegistry.getDepartmentService()).thenReturn(lmsDepartmentService);
    Mockito.when(lmsRepositoryRegistry.getCourseRepository()).thenReturn(courseRepository);
    Mockito.when(lmsRepositoryRegistry.getCourseAssessmentRepository()).thenReturn(courseAssessmentRepository);
    Mockito.when(lmsRepositoryRegistry.getCourseContentRepository()).thenReturn(courseContentRepository);
    Mockito.when(lmsRepositoryRegistry.getCourseSuggestedUsersRepository()).thenReturn(courseSuggestedUsersRepository);

    CourseTypeResolver courseTypeResolver = new OnlineCourseTypeResolver();
    Mockito.when(lmsServiceRegistry.getCourseTypeResolver()).thenReturn(courseTypeResolver);


    DateInfo dateInfo = new DateInfo();
    dateInfo.setCreatedDate(LocalDateTime.now());
    dateInfo.setModifiedDate(LocalDateTime.now());
    dateInfo.setPublishDate(LocalDateTime.now());

    CourseDetail courseDetail = new CourseDetail("Title");
    courseDetail.setDateInfo(dateInfo);
    courseDetail.setHasAssessment(true);

    Course existingCourse = new Course(CourseId.valueOf("CourseId"), CourseCategoryId.valueOf("CourseCategoryId"),
        courseDetail, AuthorId.valueOf("AuthorId"), new CourseDepartmentRelation(DepartmentId.valueOf("CourseDepartmentID")));

    existingCourse.setAssessmentId("assessmentId");

    Course createdCourse = new Course(CourseId.valueOf("CourseId"), CourseCategoryId.valueOf("CourseCategoryId"),
        courseDetail, AuthorId.valueOf("AuthorId"), new CourseDepartmentRelation(DepartmentId.valueOf("CourseDepartmentID")));

    Mockito.when(courseCategoryRepository.exists(Mockito.any())).thenReturn(true);
    Mockito.when(lmsDepartmentService.getCurrentDepartmentId()).thenReturn("DepartmentId");
    Mockito.when(courseRepository.fetchById(Mockito.any())).thenReturn(existingCourse);
    Mockito.when(courseAssessmentRepository.fetchById(Mockito.any())).thenReturn(new CourseAssessment(CourseId.valueOf("CourseId"), Collections.emptySet()));
    Mockito.when(courseAssessmentRepository.create(Mockito.any(), Mockito.any())).thenReturn(null);
    Mockito.when(courseContentRepository.fetchById(Mockito.any())).thenReturn(new CourseContent(CourseContentId.valueOf("CourseContentId")));
    Mockito.when(courseSuggestedUsersRepository.getSuggestedUsers(Mockito.any())).thenReturn(Collections.emptySet());
    Mockito.when(courseCategoryRepository.getCourseCategoryNameById(Mockito.any())).thenReturn("CategoryName");

    Mockito.when(courseRepository.create(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(
        createdCourse);

    Mockito.when(courseRepository.update(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(createdCourse);

    createCourseInput.setCourseType("EMPLOYEE");

    cloneCourse = new CloneCourse(lmsRepositoryRegistry, lmsServiceRegistry);

    expectedDto = CourseTestUtils.generateCourseDto(existingCourse, courseDetail);
  }

  @Test
  public void whenSuccess() throws UseCaseException
  {
    Assert.assertTrue(CourseTestUtils.isEqual(expectedDto, cloneCourse.execute(cloneCourseInput)));
  }

  @Test(expected = NullPointerException.class)
  public void whenInputIsNull_throwsNullPointerException() throws UseCaseException
  {
    cloneCourse.execute(null);
  }

  @Test(expected = UseCaseException.class)
  public void whenUpdatingCourseFails_logs_and_throwsUseCaseException() throws LmsRepositoryException, UseCaseException
  {
    Mockito.when(courseRepository.update(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenThrow(
        LmsRepositoryException.class);
    cloneCourse.execute(cloneCourseInput);
  }

  @Test
  public void whenCourseAssessmentCouldNotBeFound_logsAndContinues() throws LmsRepositoryException, UseCaseException
  {
    Mockito.when(courseAssessmentRepository.fetchById(Mockito.any())).thenThrow(LmsRepositoryException.class);
    cloneCourse.execute(cloneCourseInput);

    Assert.assertTrue(CourseTestUtils.isEqual(expectedDto, cloneCourse.execute(cloneCourseInput)));
  }

}
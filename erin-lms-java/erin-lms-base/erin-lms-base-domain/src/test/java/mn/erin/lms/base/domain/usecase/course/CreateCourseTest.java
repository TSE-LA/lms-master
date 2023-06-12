package mn.erin.lms.base.domain.usecase.course;

import java.util.HashMap;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.LmsDepartmentService;
import mn.erin.lms.base.aim.organization.DepartmentId;
import mn.erin.lms.base.aim.user.AuthorId;
import mn.erin.lms.base.domain.model.category.CourseCategoryId;
import mn.erin.lms.base.domain.model.course.Course;
import mn.erin.lms.base.domain.model.course.CourseDepartmentRelation;
import mn.erin.lms.base.domain.model.course.CourseDetail;
import mn.erin.lms.base.domain.model.course.CourseId;
import mn.erin.lms.base.domain.repository.CourseCategoryRepository;
import mn.erin.lms.base.domain.repository.CourseRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.CourseTypeResolver;
import mn.erin.lms.base.domain.service.LmsFileSystemService;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.service.ThumbnailService;
import mn.erin.lms.base.domain.service.impl.OnlineCourseTypeResolver;
import mn.erin.lms.base.domain.usecase.course.dto.CreateCourseInput;
import mn.erin.lms.base.domain.usecase.course.dto.CreateCourseOutput;

/**
 * @author Galsan Bayart.
 */
public class CreateCourseTest
{
  public static final String COURSE_CATEGORY_ID = "CategoryId";
  public static final String DEFAULT_COURSE_TYPE = "EMPLOYEE";
  public static final String COURSE_ID = "COURSE_ID";
  public static final String TITLE = "TITLE";
  public static final String DOES_NOT_EXIST_COURSE_TYPE = "Does not exist course type";

  private CourseCategoryRepository courseCategoryRepository;

  private CreateCourse createCourse;
  private final CreateCourseInput createCourseInput = new CreateCourseInput("Title", COURSE_CATEGORY_ID, new HashMap<>());

  private static CourseRepository courseRepository;
  @Before
  public void setUp() throws Exception
  {
    LmsRepositoryRegistry lmsRepositoryRegistry = Mockito.mock(LmsRepositoryRegistry.class);
    LmsServiceRegistry lmsServiceRegistry = Mockito.mock(LmsServiceRegistry.class);

    this.courseCategoryRepository = Mockito.mock(CourseCategoryRepository.class);
    LmsDepartmentService lmsDepartmentService = Mockito.mock(LmsDepartmentService.class);
    LmsFileSystemService lmsFileSystemService = Mockito.mock(LmsFileSystemService.class);
    ThumbnailService thumbnailService = Mockito.mock(ThumbnailService.class);

    CourseTypeResolver courseTypeResolver = new OnlineCourseTypeResolver();
    courseRepository = Mockito.mock(CourseRepository.class);

    Mockito.when(lmsRepositoryRegistry.getCourseCategoryRepository()).thenReturn(courseCategoryRepository);
    Mockito.when(lmsServiceRegistry.getDepartmentService()).thenReturn(lmsDepartmentService);
    Mockito.when(lmsServiceRegistry.getLmsFileSystemService()).thenReturn(lmsFileSystemService);
    Mockito.when(lmsServiceRegistry.getThumbnailService()).thenReturn(thumbnailService);

    Mockito.when(lmsServiceRegistry.getCourseTypeResolver()).thenReturn(courseTypeResolver);
    Mockito.when(lmsRepositoryRegistry.getCourseRepository()).thenReturn(courseRepository);

    createCourse = new CreateCourse(lmsRepositoryRegistry, lmsServiceRegistry);

    createCourseInput.setDescription("Description");
    createCourseInput.setCourseType(DEFAULT_COURSE_TYPE);
    createCourseInput.setAssessmentId("AssessmentId");
    createCourseInput.setCertificateId("CertificateId");

    Mockito.when(courseCategoryRepository.exists(CourseCategoryId.valueOf(COURSE_CATEGORY_ID))).thenReturn(true);
    Mockito.when(thumbnailService.getDefaultThumbnailUrl()).thenReturn("DefaultThumbnailUrl");
    Mockito.when(lmsDepartmentService.getCurrentDepartmentId()).thenReturn("CurrentDepartmentId");

    Mockito.when(courseCategoryRepository.getCourseCategoryNameById(CourseCategoryId.valueOf(COURSE_CATEGORY_ID))).thenReturn("CourseCategoryName");

    Course course = new Course(CourseId.valueOf(COURSE_ID), CourseCategoryId.valueOf(COURSE_CATEGORY_ID), new CourseDetail(TITLE),
        AuthorId.valueOf("AuthorId"), new CourseDepartmentRelation(DepartmentId.valueOf("DepartmentId")));

    Mockito.when(courseRepository.create(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(
        course);

    Mockito.when(lmsFileSystemService.createCourseFolder(COURSE_ID, TITLE)).thenReturn("CourseFolderId");

  }

  @Test
  public void whenSuccess() throws UseCaseException
  {
    Assert.assertTrue(isEqual(createCourse.execute(createCourseInput)));
  }

  @Test(expected = NullPointerException.class)
  public void whenInputNull_throwsNullPointerException() throws UseCaseException
  {
    createCourse.execute(null);
  }

  @Test(expected = UseCaseException.class)
  public void whenCategoryDoesntExist_throwsUseCaseException() throws UseCaseException
  {
    Mockito.when(courseCategoryRepository.exists(CourseCategoryId.valueOf(COURSE_CATEGORY_ID))).thenReturn(false);
    createCourse.execute(createCourseInput);
  }

  @Test(expected = UseCaseException.class)
  public void whenCourseTypeDoesNotExist_throwsUseCaseException() throws UseCaseException
  {
    createCourseInput.setCourseType(DOES_NOT_EXIST_COURSE_TYPE);
    createCourse.execute(createCourseInput);
  }

  @Test(expected = UseCaseException.class)
  public void whenCreatingCourseFails_throwsUseCaseException() throws UseCaseException, LmsRepositoryException
  {
    Mockito.when(courseRepository.create(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenThrow(LmsRepositoryException.class);
    createCourse.execute(createCourseInput);
  }

  @Test
  public void whenGettingCourseCategoryFails_logs() throws UseCaseException, LmsRepositoryException
  {
    Mockito.when(courseCategoryRepository.getCourseCategoryNameById(Mockito.any())).thenThrow(LmsRepositoryException.class);
    Assert.assertTrue(isEqual(createCourse.execute(createCourseInput)));
  }

  @Test(expected = UseCaseException.class)
  public void whenCourseCategoryDoesNotExist_throwsUseCaseException() throws UseCaseException
  {
    Mockito.when(courseCategoryRepository.exists(CourseCategoryId.valueOf(COURSE_CATEGORY_ID))).thenReturn(false);
    createCourse.execute(createCourseInput);
  }

  @Test(expected = UseCaseException.class)
  public void whenUpdatingCourseFails_throwsUseCaseException() throws UseCaseException, LmsRepositoryException
  {
    Mockito.when(courseRepository.update(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenThrow(LmsRepositoryException.class);
    createCourse.execute(createCourseInput);
  }

  private boolean isEqual(CreateCourseOutput output)
  {
    return output.getCourseId().equals(COURSE_ID);
  }

}
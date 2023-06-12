package mn.erin.lms.base.domain.usecase.course;

import java.util.HashSet;
import org.junit.Before;
import org.junit.Test;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.LmsDepartmentService;
import mn.erin.lms.base.domain.model.course.Course;
import mn.erin.lms.base.domain.model.course.CourseId;
import mn.erin.lms.base.domain.model.course.PublishStatus;
import mn.erin.lms.base.domain.model.online_course.EmployeeType;
import mn.erin.lms.base.domain.repository.CourseRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.CoursePublisher;
import mn.erin.lms.base.domain.service.CourseTypeResolver;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.service.LmsTaskScheduler;
import mn.erin.lms.base.domain.service.NotificationService;
import mn.erin.lms.base.domain.service.TaskCancellationResult;
import mn.erin.lms.base.domain.service.UnknownCourseTypeException;
import mn.erin.lms.base.domain.usecase.course.dto.PublishCourseInput;

import static mn.erin.lms.base.domain.usecase.course.CourseTestUtils.generateCourse;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Temuulen Naranbold
 */
public class PublishCourseTest
{
  private static final String DEPARTMENT_ID = "departmentId";
  private static final String COURSE_ID = "courseId";

  private LmsRepositoryRegistry lmsRepositoryRegistry;
  private LmsServiceRegistry lmsServiceRegistry;
  private LmsTaskScheduler lmsTaskScheduler;
  private LmsDepartmentService lmsDepartmentService;
  private CoursePublisher coursePublisher;
  private CourseRepository courseRepository;
  private NotificationService notificationService;
  private CourseTypeResolver courseTypeResolver;

  private PublishCourse publishCourse;

  @Before
  public void setUp() throws LmsRepositoryException, UnknownCourseTypeException
  {
    lmsRepositoryRegistry = mock(LmsRepositoryRegistry.class);
    lmsServiceRegistry = mock(LmsServiceRegistry.class);
    lmsTaskScheduler = mock(LmsTaskScheduler.class);
    lmsDepartmentService = mock(LmsDepartmentService.class);
    coursePublisher = mock(CoursePublisher.class);
    courseRepository = mock(CourseRepository.class);
    notificationService = mock(NotificationService.class);
    courseTypeResolver = mock(CourseTypeResolver.class);

    when(lmsServiceRegistry.getLmsScheduler()).thenReturn(lmsTaskScheduler);
    when(lmsServiceRegistry.getDepartmentService()).thenReturn(lmsDepartmentService);
    when(lmsServiceRegistry.getCoursePublisher()).thenReturn(coursePublisher);
    when(lmsRepositoryRegistry.getCourseRepository()).thenReturn(courseRepository);
    when(lmsServiceRegistry.getNotificationService()).thenReturn(notificationService);
    when(lmsServiceRegistry.getCourseTypeResolver()).thenReturn(courseTypeResolver);

    TaskCancellationResult taskResult = new TaskCancellationResult();
    taskResult.complete(true);
    taskResult.cancelled(false);
    when(lmsTaskScheduler.cancel(anyString())).thenReturn(taskResult);
    when(lmsDepartmentService.getDepartmentId(anyString())).thenReturn(DEPARTMENT_ID);
    Course course = generateCourse();
    when(courseRepository.fetchById(CourseId.valueOf(COURSE_ID))).thenReturn(course);
    when(courseRepository.update(
        course.getCourseId(), course.getCourseCategoryId(),
        course.getCourseDetail(), course.getCourseType(),
        course.getAssessmentId(), course.getCertificateId())).thenReturn(course);
    when(courseTypeResolver.resolve(anyString())).thenReturn(new EmployeeType());
    when(courseRepository.updateByDepartment(course.getCourseId(),
        course.getCourseDepartmentRelation(), PublishStatus.PUBLISHED)).thenReturn(course);
    when(lmsDepartmentService.getDepartmentName(anyString())).thenReturn("departmentName");

    publishCourse = new PublishCourse(lmsRepositoryRegistry, lmsServiceRegistry);
  }

  @Test(expected = NullPointerException.class)
  public void whenInput_isNull() throws UseCaseException
  {
    publishCourse.execute(null);
  }

  @Test(expected = NullPointerException.class)
  public void whenCourseId_isNull() throws UseCaseException
  {
    PublishCourseInput input = new PublishCourseInput(null, new HashSet<>(), new HashSet<>());
    publishCourse.execute(input);
  }

  @Test(expected = IllegalArgumentException.class)
  public void whenCourseId_isBlank() throws UseCaseException
  {
    PublishCourseInput input = new PublishCourseInput("", new HashSet<>(), new HashSet<>());
    publishCourse.execute(input);
  }

  @Test(expected = NullPointerException.class)
  public void whenPublishDate_isNull() throws UseCaseException
  {
    PublishCourseInput input = new PublishCourseInput(COURSE_ID, new HashSet<>(), new HashSet<>());
    input.setPublishDate(null);
    publishCourse.execute(input);
  }
}

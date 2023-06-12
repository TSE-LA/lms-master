package mn.erin.lms.base.domain.usecase.enrollment;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.LmsDepartmentService;
import mn.erin.lms.base.aim.user.LearnerId;
import mn.erin.lms.base.domain.model.course.Course;
import mn.erin.lms.base.domain.model.course.CourseId;
import mn.erin.lms.base.domain.model.course.PublishStatus;
import mn.erin.lms.base.domain.repository.CourseEnrollmentRepository;
import mn.erin.lms.base.domain.repository.CourseRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.service.NotificationService;
import mn.erin.lms.base.domain.service.ProgressTrackingService;
import mn.erin.lms.base.domain.usecase.enrollment.dto.UpdateCourseEnrollmentsInput;

import static mn.erin.lms.base.domain.usecase.course.CourseTestUtils.generateCourse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Temuulen Naranbold
 */
public class UpdateCourseEnrollmentsTest
{
  private static final String COURSE_ID = "courseId";
  private static final String DEPARTMENT_ID = "departmentId";
  private LmsDepartmentService lmsDepartmentService;
  private CourseRepository courseRepository;
  private CourseEnrollmentRepository courseEnrollmentRepository;
  private Course course;

  private UpdateCourseEnrollments updateCourseEnrollments;

  @Before
  public void setUp() throws LmsRepositoryException
  {
    LmsRepositoryRegistry lmsRepositoryRegistry = mock(LmsRepositoryRegistry.class);
    LmsServiceRegistry lmsServiceRegistry = mock(LmsServiceRegistry.class);
    ProgressTrackingService progressTrackingService = mock(ProgressTrackingService.class);
    NotificationService notificationService = mock(NotificationService.class);
    lmsDepartmentService = mock(LmsDepartmentService.class);
    courseRepository = mock(CourseRepository.class);
    courseEnrollmentRepository = mock(CourseEnrollmentRepository.class);


    when(lmsServiceRegistry.getDepartmentService()).thenReturn(lmsDepartmentService);
    when(lmsServiceRegistry.getProgressTrackingService()).thenReturn(progressTrackingService);
    when(lmsRepositoryRegistry.getCourseRepository()).thenReturn(courseRepository);
    when(lmsRepositoryRegistry.getCourseEnrollmentRepository()).thenReturn(courseEnrollmentRepository);
    when(lmsServiceRegistry.getNotificationService()).thenReturn(notificationService);
    course = generateCourse();
    when(courseRepository.fetchById(CourseId.valueOf(COURSE_ID))).thenReturn(course);
    when(lmsDepartmentService.getDepartmentId(DEPARTMENT_ID)).thenReturn(DEPARTMENT_ID);
    Set<String> departments = new HashSet<>();
    departments.add(DEPARTMENT_ID);
    when(lmsDepartmentService.getSubDepartments(DEPARTMENT_ID)).thenReturn(departments);
    Set<String> learners = new HashSet<>();
    learners.add("learner");
    when(lmsDepartmentService.getLearners(DEPARTMENT_ID)).thenReturn(learners);
    when(courseRepository.update(CourseId.valueOf(COURSE_ID), course.getCourseDepartmentRelation())).thenReturn(course);

    updateCourseEnrollments = new UpdateCourseEnrollments(lmsRepositoryRegistry, lmsServiceRegistry);
  }

  @Test(expected = NullPointerException.class)
  public void whenInput_isNull() throws UseCaseException
  {
    updateCourseEnrollments.execute(null);
  }

  @Test(expected = NullPointerException.class)
  public void whenCourseId_isNull() throws UseCaseException
  {
    UpdateCourseEnrollmentsInput input = new UpdateCourseEnrollmentsInput(null,
        Collections.emptySet(), Collections.emptySet(), "");
    updateCourseEnrollments.execute(input);
  }

  @Test(expected = IllegalArgumentException.class)
  public void whenCourseId_isBlank() throws UseCaseException
  {
    UpdateCourseEnrollmentsInput input = new UpdateCourseEnrollmentsInput("",
        Collections.emptySet(), Collections.emptySet(), "");
    updateCourseEnrollments.execute(input);
  }

  @Test(expected = UseCaseException.class)
  public void whenCourse_notFound() throws LmsRepositoryException, UseCaseException
  {
    UpdateCourseEnrollmentsInput input = new UpdateCourseEnrollmentsInput(COURSE_ID,
        Collections.emptySet(), Collections.emptySet(), "");
    when(courseRepository.fetchById(course.getCourseId())).thenThrow(LmsRepositoryException.class);
    updateCourseEnrollments.execute(input);
  }

  @Test
  public void whenAutoEnrollFalse() throws UseCaseException
  {
    UpdateCourseEnrollmentsInput input = new UpdateCourseEnrollmentsInput(COURSE_ID,
        Collections.emptySet(), Collections.emptySet(), "");
    input.setAutoChildDepartmentEnroll(false);

    verify(lmsDepartmentService, times(0)).getSubDepartments(anyString());
    updateCourseEnrollments.execute(input);
  }

  @Test
  public void whenAutoEnrollTrue() throws UseCaseException
  {
    Set<String> assignedDepartments = new HashSet<>();
    assignedDepartments.add(DEPARTMENT_ID);
    UpdateCourseEnrollmentsInput input = new UpdateCourseEnrollmentsInput(COURSE_ID,
        assignedDepartments, Collections.emptySet(), "");
    input.setAutoChildDepartmentEnroll(true);
    updateCourseEnrollments.execute(input);
    verify(lmsDepartmentService, times(1)).getSubDepartments(DEPARTMENT_ID);
  }

  @Test
  public void whenAutoEnrollFalse_withCourseType() throws UseCaseException
  {
    UpdateCourseEnrollmentsInput input = new UpdateCourseEnrollmentsInput(COURSE_ID,
        Collections.emptySet(), Collections.emptySet(), "LMS_USER");
    input.setAutoChildDepartmentEnroll(false);

    verify(lmsDepartmentService, times(0)).getSubDepartments(anyString());
    updateCourseEnrollments.updateEnrollmentAndCourseType(input);
  }

  @Test
  public void whenAutoEnrollTrue_withCourseType() throws UseCaseException
  {
    Set<String> assignedDepartments = new HashSet<>();
    assignedDepartments.add(DEPARTMENT_ID);
    UpdateCourseEnrollmentsInput input = new UpdateCourseEnrollmentsInput(COURSE_ID,
        assignedDepartments, Collections.emptySet(), "");
    input.setAutoChildDepartmentEnroll(true);
    updateCourseEnrollments.updateEnrollmentAndCourseType(input);
    verify(lmsDepartmentService, times(1)).getSubDepartments(DEPARTMENT_ID);
  }

  @Test
  public void whenCourse_statusUnPublished() throws UseCaseException
  {
    Set<String> learners = new HashSet<>();
    learners.add("learner");
    UpdateCourseEnrollmentsInput input = new UpdateCourseEnrollmentsInput(COURSE_ID,
        Collections.emptySet(), learners, "LMS_USER");
    input.setAutoChildDepartmentEnroll(false);
    course.getCourseDetail().changePublishStatus(PublishStatus.UNPUBLISHED);
    verify(courseEnrollmentRepository, times(0)).save(any());
    updateCourseEnrollments.execute(input);
  }

  @Test
  public void whenCourse_statusPublished() throws UseCaseException
  {
    Set<String> learners = new HashSet<>();
    learners.add("learner");
    UpdateCourseEnrollmentsInput input = new UpdateCourseEnrollmentsInput(COURSE_ID,
        Collections.emptySet(), learners, "LMS_USER");
    input.setAutoChildDepartmentEnroll(false);
    course.getCourseDetail().changePublishStatus(PublishStatus.PUBLISHED);
    updateCourseEnrollments.execute(input);
    verify(courseEnrollmentRepository, times(1)).save(any());
  }

  @Test
  public void whenCourse_statusUnPublished_withCourseType() throws UseCaseException
  {
    Set<String> learners = new HashSet<>();
    learners.add("learner");
    UpdateCourseEnrollmentsInput input = new UpdateCourseEnrollmentsInput(COURSE_ID,
        Collections.emptySet(), learners, "LMS_USER");
    input.setAutoChildDepartmentEnroll(false);
    course.getCourseDetail().changePublishStatus(PublishStatus.UNPUBLISHED);
    verify(courseEnrollmentRepository, times(0)).save(any());
    verify(courseEnrollmentRepository, times(0)).delete(any(), any());
    updateCourseEnrollments.updateEnrollmentAndCourseType(input);
  }

  @Test
  public void whenCourse_statusPublished_withCourseType() throws UseCaseException
  {
    Set<String> learners = new HashSet<>();
    learners.add("learner");
    UpdateCourseEnrollmentsInput input = new UpdateCourseEnrollmentsInput(COURSE_ID,
        Collections.emptySet(), learners, "LMS_USER");
    input.setAutoChildDepartmentEnroll(false);
    course.getCourseDetail().changePublishStatus(PublishStatus.PUBLISHED);
    Set<LearnerId> learnerIds = new HashSet<>();
    learnerIds.add(LearnerId.valueOf("user"));
    course.getCourseDepartmentRelation().setAssignedLearners(learnerIds);
    updateCourseEnrollments.updateEnrollmentAndCourseType(input);
    verify(courseEnrollmentRepository, times(1)).save(any());
    verify(courseEnrollmentRepository, times(1)).delete(any(), any());
  }
}

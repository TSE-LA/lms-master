package mn.erin.lms.base.domain.usecase.course;

import org.junit.Before;
import org.junit.Test;

import mn.erin.domain.base.model.person.PersonId;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.dms.repository.DMSRepositoryException;
import mn.erin.lms.base.aim.LmsUserService;
import mn.erin.lms.base.aim.organization.OrganizationId;
import mn.erin.lms.base.aim.user.LmsAdmin;
import mn.erin.lms.base.domain.model.category.CourseCategory;
import mn.erin.lms.base.domain.model.category.CourseCategoryId;
import mn.erin.lms.base.domain.model.course.Course;
import mn.erin.lms.base.domain.repository.AssessmentRepository;
import mn.erin.lms.base.domain.repository.ClassroomCourseAttendanceRepository;
import mn.erin.lms.base.domain.repository.CourseAssessmentRepository;
import mn.erin.lms.base.domain.repository.CourseCategoryRepository;
import mn.erin.lms.base.domain.repository.CourseContentRepository;
import mn.erin.lms.base.domain.repository.CourseEnrollmentRepository;
import mn.erin.lms.base.domain.repository.CourseRepository;
import mn.erin.lms.base.domain.repository.CourseSuggestedUsersRepository;
import mn.erin.lms.base.domain.repository.LearnerCertificateRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.repository.QuizRepository;
import mn.erin.lms.base.domain.repository.task.ScheduledTaskRepository;
import mn.erin.lms.base.domain.service.LmsFileSystemService;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.service.ProgressTrackingService;
import mn.erin.lms.base.domain.usecase.assessment.DeleteCourseAssessment;
import mn.erin.lms.base.domain.usecase.assessment.UpdateAssessmentStatus;
import mn.erin.lms.base.domain.usecase.assessment.dto.UpdateAssessmentStatusInput;
import mn.erin.lms.base.domain.usecase.certificate.DeleteLearnerCertificate;

import static mn.erin.lms.base.domain.usecase.course.CourseTestUtils.generateCourse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DeleteCourseTest
{
  private static final String FOLDER_ID = "folderId";

  private CourseRepository courseRepository;
  private LmsFileSystemService lmsFileSystemService;
  private CourseCategoryRepository courseCategoryRepository;
  private ClassroomCourseAttendanceRepository classroomCourseAttendanceRepository;
  private ScheduledTaskRepository scheduledTaskRepository;
  private Course course;
  private UpdateAssessmentStatus updateAssessmentStatus;

  private DeleteCourse deleteCourse;

  @Before
  public void setUp() throws LmsRepositoryException, DMSRepositoryException, UseCaseException
  {
    LmsRepositoryRegistry lmsRepositoryRegistry = mock(LmsRepositoryRegistry.class);
    LmsServiceRegistry lmsServiceRegistry = mock(LmsServiceRegistry.class);
    LearnerCertificateRepository learnerCertificateRepository = mock(LearnerCertificateRepository.class);
    CourseAssessmentRepository courseAssessmentRepository = mock(CourseAssessmentRepository.class);
    QuizRepository quizRepository = mock(QuizRepository.class);
    CourseContentRepository courseContentRepository = mock(CourseContentRepository.class);
    CourseEnrollmentRepository courseEnrollmentRepository = mock(CourseEnrollmentRepository.class);
    ProgressTrackingService progressTrackingService = mock(ProgressTrackingService.class);
    CourseSuggestedUsersRepository courseSuggestedUsersRepository = mock(CourseSuggestedUsersRepository.class);
    LmsUserService lmsUserService = mock(LmsUserService.class);
    DeleteLearnerCertificate deleteLearnerCertificate = mock(DeleteLearnerCertificate.class);
    DeleteCourseAssessment deleteCourseAssessment = mock(DeleteCourseAssessment.class);
    AssessmentRepository assessmentRepository = mock(AssessmentRepository.class);
    courseRepository = mock(CourseRepository.class);
    lmsFileSystemService = mock(LmsFileSystemService.class);
    courseCategoryRepository = mock(CourseCategoryRepository.class);
    classroomCourseAttendanceRepository = mock(ClassroomCourseAttendanceRepository.class);
    scheduledTaskRepository = mock(ScheduledTaskRepository.class);
    updateAssessmentStatus = mock(UpdateAssessmentStatus.class);


    when(lmsRepositoryRegistry.getCourseRepository()).thenReturn(courseRepository);
    when(lmsRepositoryRegistry.getLearnerCertificateRepository()).thenReturn(learnerCertificateRepository);
    when(lmsServiceRegistry.getLmsFileSystemService()).thenReturn(lmsFileSystemService);
    when(lmsRepositoryRegistry.getCourseAssessmentRepository()).thenReturn(courseAssessmentRepository);
    when(lmsRepositoryRegistry.getQuizRepository()).thenReturn(quizRepository);
    when(lmsRepositoryRegistry.getCourseContentRepository()).thenReturn(courseContentRepository);
    when(lmsRepositoryRegistry.getCourseEnrollmentRepository()).thenReturn(courseEnrollmentRepository);
    when(lmsServiceRegistry.getProgressTrackingService()).thenReturn(progressTrackingService);
    when(lmsRepositoryRegistry.getCourseSuggestedUsersRepository()).thenReturn(courseSuggestedUsersRepository);
    when(lmsRepositoryRegistry.getCourseCategoryRepository()).thenReturn(courseCategoryRepository);
    when(lmsRepositoryRegistry.getClassroomAttendanceRepository()).thenReturn(classroomCourseAttendanceRepository);
    when(lmsRepositoryRegistry.getScheduledTaskRepository()).thenReturn(scheduledTaskRepository);
    when(lmsServiceRegistry.getLmsUserService()).thenReturn(lmsUserService);
    when(lmsRepositoryRegistry.getAssessmentRepository()).thenReturn(assessmentRepository);

    course = generateCourse();
    when(courseRepository.fetchById(course.getCourseId())).thenReturn(course);
    when(lmsUserService.getCurrentUser()).thenReturn(new LmsAdmin(new PersonId("admin")));
    when(deleteLearnerCertificate.execute(course.getCourseId().getId())).thenReturn(true);
    when(deleteCourseAssessment.execute(course.getCourseId().getId())).thenReturn(true);
    when(courseContentRepository.exists(course.getCourseId())).thenReturn(true);
    when(courseContentRepository.delete(course.getCourseId())).thenReturn(true);
    when(courseEnrollmentRepository.deleteAll(course.getCourseId())).thenReturn(true);
    when(progressTrackingService.deleteCourseData(course.getCourseId().getId())).thenReturn(true);
    when(courseSuggestedUsersRepository.deleteSuggestedUser(course.getCourseId().getId())).thenReturn(true);
    CourseCategory courseCategory = new CourseCategory(
        OrganizationId.valueOf("org"), course.getCourseCategoryId(),
        CourseCategoryId.valueOf("classroom-course"), "category", true);
    when(courseCategoryRepository.getById(course.getCourseCategoryId())).thenReturn(courseCategory);
    when(classroomCourseAttendanceRepository.deleteCourseAttendance(course.getCourseId())).thenReturn(true);
    when(lmsFileSystemService.getCourseFolderId(course.getCourseId().getId())).thenReturn(FOLDER_ID);
    when(lmsFileSystemService.deleteFolder(FOLDER_ID)).thenReturn(true);
    when(courseRepository.delete(course.getCourseId())).thenReturn(true);
    when(scheduledTaskRepository.exists(course.getCourseId().getId())).thenReturn(true);

    deleteCourse = new DeleteCourse(lmsRepositoryRegistry, lmsServiceRegistry);
  }

  @Test(expected = NullPointerException.class)
  public void whenCourseId_isNull() throws UseCaseException
  {
    deleteCourse.execute(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void whenCourseId_isBlank() throws UseCaseException
  {
    deleteCourse.execute("");
  }

  @Test(expected = UseCaseException.class)
  public void whenCourse_notFound() throws LmsRepositoryException, UseCaseException
  {
    when(courseRepository.fetchById(course.getCourseId())).thenThrow(LmsRepositoryException.class);
    deleteCourse.execute(course.getCourseId().getId());
  }

  @Test
  public void whenCourse_isClassroom() throws UseCaseException
  {
    deleteCourse.execute(course.getCourseId().getId());
    verify(classroomCourseAttendanceRepository, times(1)).deleteCourseAttendance(course.getCourseId());
  }

  @Test
  public void whenCourse_isNotClassroom() throws UseCaseException, LmsRepositoryException
  {
    CourseCategory courseCategory = new CourseCategory(
        OrganizationId.valueOf("org"), course.getCourseCategoryId(),
        CourseCategoryId.valueOf("online-course"), "category", true);
    when(courseCategoryRepository.getById(course.getCourseCategoryId())).thenReturn(courseCategory);
    deleteCourse.execute(course.getCourseId().getId());
    verify(classroomCourseAttendanceRepository, times(0)).deleteCourseAttendance(course.getCourseId());
  }

  @Test
  public void whenCourseFolder_isNotNull() throws UseCaseException
  {
    deleteCourse.execute(course.getCourseId().getId());
    verify(lmsFileSystemService, times(1)).deleteFolder(FOLDER_ID);
  }

  @Test
  public void whenCourseFolder_isNull() throws UseCaseException
  {
    when(lmsFileSystemService.getCourseFolderId(course.getCourseId().getId())).thenReturn(null);
    verify(lmsFileSystemService, times(0)).deleteFolder(null);
    deleteCourse.execute(course.getCourseId().getId());
  }

  @Test
  public void whenCourse_hasNotAssessment() throws LmsRepositoryException, UseCaseException
  {
    Course course1 = generateCourse();
    course1.setAssessmentId(null);
    when(courseRepository.fetchById(course1.getCourseId())).thenReturn(course1);
    deleteCourse.execute(course1.getCourseId().getId());
    verify(updateAssessmentStatus, times(0)).execute(new UpdateAssessmentStatusInput(course1.getAssessmentId(), false));
  }

  @Test
  public void whenCourse_hasScheduledTask() throws UseCaseException
  {
    deleteCourse.execute(course.getCourseId().getId());
    verify(scheduledTaskRepository, times(1)).remove(course.getCourseId().getId());
  }

  @Test
  public void whenCourse_hasNotScheduledTask() throws UseCaseException
  {
    when(scheduledTaskRepository.exists(course.getCourseId().getId())).thenReturn(false);
    deleteCourse.execute(course.getCourseId().getId());
    verify(scheduledTaskRepository, times(0)).remove(course.getCourseId().getId());
  }
}
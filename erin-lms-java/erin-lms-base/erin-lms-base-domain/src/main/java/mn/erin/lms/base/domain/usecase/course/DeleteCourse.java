package mn.erin.lms.base.domain.usecase.course;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.base.domain.model.category.CourseCategory;
import mn.erin.lms.base.domain.model.course.Course;
import mn.erin.lms.base.domain.model.course.CourseId;
import mn.erin.lms.base.aim.user.Author;
import mn.erin.lms.base.domain.repository.CourseAssessmentRepository;
import mn.erin.lms.base.domain.repository.CourseContentRepository;
import mn.erin.lms.base.domain.repository.CourseEnrollmentRepository;
import mn.erin.lms.base.domain.repository.CourseSuggestedUsersRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.repository.QuizRepository;
import mn.erin.lms.base.domain.service.LmsFileSystemService;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.service.ProgressTrackingService;
import mn.erin.lms.base.domain.usecase.assessment.DeleteCourseAssessment;
import mn.erin.lms.base.domain.usecase.assessment.UpdateAssessmentStatus;
import mn.erin.lms.base.domain.usecase.assessment.dto.UpdateAssessmentStatusInput;
import mn.erin.lms.base.domain.usecase.certificate.DeleteLearnerCertificate;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Authorized(users = { Author.class })
public class DeleteCourse extends CourseUseCase<String, Boolean>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(DeleteCourse.class);
  private static final String CLASSROOM_CATEGORY_PARENT_ID = "classroom-course";
  private CourseId courseId;

  public DeleteCourse(LmsRepositoryRegistry lmsRepositoryRegistry, LmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
  }

  @Override
  public Boolean execute(String input) throws UseCaseException
  {
    courseId = CourseId.valueOf(input);
    Course course = getCourse(courseId);
    boolean isDeleted = false;
    if (course != null)
    {
      isDeleted = deleteCourse(course);
    }
    return isDeleted;
  }

  private boolean deleteCourse(Course course)
  {
    deleteLearnerCertificate();
    deleteCourseAssessment();
    deleteCourseContent();
    deleteEnrollments();
    deleteLearnerTracks();
    deleteSuggestedUsers();
    deleteClassroomCourseAttendance(course);

    LmsFileSystemService lmsFileSystemService = lmsServiceRegistry.getLmsFileSystemService();
    if (lmsFileSystemService.getCourseFolderId(courseId.getId()) != null)
    {
      lmsFileSystemService.deleteFolder(lmsFileSystemService.getCourseFolderId(courseId.getId()));
    }
    boolean isDeleted = courseRepository.delete(course.getCourseId());
    if (!StringUtils.isBlank(course.getAssessmentId()))
    {
      deactivateAssessment(course.getAssessmentId());
    }
    if (lmsRepositoryRegistry.getScheduledTaskRepository().exists(courseId.getId()))
    {
      lmsRepositoryRegistry.getScheduledTaskRepository().remove(courseId.getId());
    }
    return isDeleted;
  }

  private void deleteLearnerCertificate()
  {
    DeleteLearnerCertificate deleteLearnerCertificate = new DeleteLearnerCertificate(lmsRepositoryRegistry, lmsServiceRegistry);
    try
    {
      deleteLearnerCertificate.execute(courseId.getId());
    }
    catch (UseCaseException e)
    {
      LOGGER.error("Failed to delete learner certificates: [{}]", courseId.getId());
    }
  }

  private void deleteCourseAssessment()
  {
    CourseAssessmentRepository courseAssessmentRepository = lmsRepositoryRegistry.getCourseAssessmentRepository();
    QuizRepository quizRepository = lmsRepositoryRegistry.getQuizRepository();
    DeleteCourseAssessment deleteCourseAssessment = new DeleteCourseAssessment(courseAssessmentRepository, quizRepository);

    try
    {
      deleteCourseAssessment.execute(courseId.getId());
    }
    catch (UseCaseException e)
    {
      LOGGER.error(e.getMessage(), e);
    }
  }

  private void deleteCourseContent()
  {
    CourseContentRepository courseContentRepository = lmsRepositoryRegistry.getCourseContentRepository();
    if (courseContentRepository.exists(courseId))
    {
      boolean isCourseContentDeleted = courseContentRepository.delete(courseId);

      if (!isCourseContentDeleted)
      {
        LOGGER.error("Failed to delete the content of the course: [{}]", courseId.getId());
      }
    }
  }

  private void deleteEnrollments()
  {
    CourseEnrollmentRepository courseEnrollmentRepository = lmsRepositoryRegistry.getCourseEnrollmentRepository();
    boolean isDeleted = courseEnrollmentRepository.deleteAll(courseId);

    if (!isDeleted)
    {
      LOGGER.error("Failed to delete the enrollments of the course: [{}]", courseId.getId());
    }
  }

  private void deleteLearnerTracks()
  {
    ProgressTrackingService progressTrackingService = lmsServiceRegistry.getProgressTrackingService();
    boolean isDeleted = progressTrackingService.deleteCourseData(courseId.getId());

    if (!isDeleted)
    {
      LOGGER.error("Failed to delete track data of the course: [{}]", courseId.getId());
    }
  }

  private void deleteSuggestedUsers()
  {
    CourseSuggestedUsersRepository courseSuggestedUsersRepository = lmsRepositoryRegistry.getCourseSuggestedUsersRepository();
    boolean isDeleted = courseSuggestedUsersRepository.deleteSuggestedUser(courseId.getId());
    if (!isDeleted)
    {
      LOGGER.error("Failed to delete suggested users of the course: [{}]", courseId.getId());
    }
  }

  private void deactivateAssessment(String assessmentId)
  {
    UpdateAssessmentStatus updateAssessmentStatus = new UpdateAssessmentStatus(lmsRepositoryRegistry, lmsServiceRegistry);

    try
    {
      updateAssessmentStatus.execute(new UpdateAssessmentStatusInput(assessmentId, false));
    }
    catch (UseCaseException e)
    {
      LOGGER.error("Failed to deactivate assessment of the course: [{}]", courseId.getId());
    }
  }

  private void deleteClassroomCourseAttendance(Course course)
  {
    try
    {
      CourseCategory courseCategory = lmsRepositoryRegistry.getCourseCategoryRepository().getById(course.getCourseCategoryId());
      if (courseCategory.getParentCategoryId().getId().equals(CLASSROOM_CATEGORY_PARENT_ID))
      {
        lmsRepositoryRegistry.getClassroomAttendanceRepository().deleteCourseAttendance(courseId);
      }
    }
    catch (LmsRepositoryException e)
    {
      LOGGER.error(e.getMessage());
    }
  }
}

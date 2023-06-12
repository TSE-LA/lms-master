package mn.erin.lms.base.domain.usecase.course.classroom;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.domain.model.category.CourseCategoryId;
import mn.erin.lms.base.domain.model.classroom_course.Attendance;
import mn.erin.lms.base.domain.model.course.Course;
import mn.erin.lms.base.domain.model.course.CourseDetail;
import mn.erin.lms.base.domain.model.course.CourseId;
import mn.erin.lms.base.domain.model.course.CourseType;
import mn.erin.lms.base.domain.repository.ClassroomCourseAttendanceRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.CourseContentCreator;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.service.UnknownCourseTypeException;
import mn.erin.lms.base.domain.usecase.certificate.GenerateCertificate;
import mn.erin.lms.base.domain.usecase.certificate.GetCertificateById;
import mn.erin.lms.base.domain.usecase.certificate.ReceiveCertificate;
import mn.erin.lms.base.domain.usecase.certificate.dto.CertificateDto;
import mn.erin.lms.base.domain.usecase.certificate.dto.GenerateCertificateInput;
import mn.erin.lms.base.domain.usecase.certificate.dto.ReceiveCertificateInput;
import mn.erin.lms.base.domain.usecase.course.CourseUseCase;
import mn.erin.lms.base.domain.usecase.course.CreateLearnerCourseHistory;
import mn.erin.lms.base.domain.usecase.course.classroom.dto.CloseClassroomCourseInput;
import mn.erin.lms.base.domain.usecase.course.dto.LearnerAttendanceInput;
import mn.erin.lms.base.domain.usecase.course.dto.LearnerCourseHistoryInput;
import mn.erin.lms.base.domain.usecase.notification.SendAssessmentLinkNotification;

/**
 * @author Erdenetulga
 */
public class CloseClassroomCourse extends CourseUseCase<CloseClassroomCourseInput, Void>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(CloseClassroomCourse.class);
  public static final String LEARNER_CERTIFICATES = "Learner-Certificates/";
  public static final String STATE = "state";
  public static final String DONE = "DONE";
  public static final String ENROLLMENT_COUNT = "enrollmentCount";
  public static final String ORIGINAL_TITLE = "originalTitle";

  private CourseContentCreator courseContentCreator;
  private ClassroomCourseAttendanceRepository classroomCourseAttendanceRepository;

  public CloseClassroomCourse(LmsRepositoryRegistry lmsRepositoryRegistry,
      LmsServiceRegistry lmsServiceRegistry,
      ClassroomCourseAttendanceRepository classroomCourseAttendanceRepository,
      CourseContentCreator courseContentCreator)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
    this.classroomCourseAttendanceRepository = classroomCourseAttendanceRepository;
    this.courseContentCreator = courseContentCreator;
  }

  @Override
  public Void execute(CloseClassroomCourseInput closeClassroomCourseInput) throws UseCaseException
  {
    Validate.notNull(closeClassroomCourseInput);
    Course course = getCourse(CourseId.valueOf(closeClassroomCourseInput.getCourseId()));
    try
    {
      if (!closeClassroomCourseInput.getAttendances().isEmpty() && course.getCertificateId() != null)
      {
        generateCertificate(closeClassroomCourseInput, course);
      }

      close(closeClassroomCourseInput, course);
      return null;
    }
    catch (LmsRepositoryException | UnknownCourseTypeException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }
  }

  private void generateCertificate(CloseClassroomCourseInput closeClassroomCourseInput, Course course)
  {
    try
    {
      for (LearnerAttendanceInput attendance : closeClassroomCourseInput.getAttendances())
      {
        if (attendance.isPresent())
        {
          CertificateDto certificateDto = new GetCertificateById(lmsRepositoryRegistry, lmsServiceRegistry).execute(course.getCertificateId());
          String targetPath = LEARNER_CERTIFICATES + course.getCourseId().getId() + '/' + attendance.getLearnerId();
          GenerateCertificateInput input = new GenerateCertificateInput(
              certificateDto.getId(), certificateDto.getName(),
              attendance.getLearnerId(), closeClassroomCourseInput.getCourseId(),
              course.getCourseDetail().getTitle(), targetPath, "certificate"
          );
          GenerateCertificate generateCertificate = new GenerateCertificate(lmsServiceRegistry, lmsRepositoryRegistry);
          String generatedCertificateId = generateCertificate.execute(input);

          ReceiveCertificateInput receiveCertificateInput = new ReceiveCertificateInput(
              attendance.getLearnerId(),
              course.getCourseId().getId(),
              course.getCertificateId(),
              generatedCertificateId);
          new ReceiveCertificate(lmsRepositoryRegistry, lmsServiceRegistry).execute(receiveCertificateInput);
        }
      }
    }
    catch (UseCaseException e)
    {
      LOGGER.error("failed to generate certificate of this = [{}] course!", course.getCourseId().getId());
    }
  }

  private void close(CloseClassroomCourseInput closeClassroomCourseInput, Course course) throws UnknownCourseTypeException, LmsRepositoryException
  {
    if (course.getAssessmentId() != null)
    {
      boolean assessmentScormContent = courseContentCreator.createContent(course);
      if (assessmentScormContent)
      {
        updateCourse(closeClassroomCourseInput, course);
      }
    }
    else
    {
      updateCourse(closeClassroomCourseInput, course);
    }

    CreateLearnerCourseHistory createLearnerCourseHistory = new CreateLearnerCourseHistory(lmsRepositoryRegistry, lmsServiceRegistry);

    for (LearnerAttendanceInput input : closeClassroomCourseInput.getAttendances())
    {
      try
      {
        if (input.isPresent())
        {
          createLearnerCourseHistory.execute(new LearnerCourseHistoryInput(course.getCourseId(), input.getLearnerId()));
        }
      }
      catch (UseCaseException e)
      {
        LOGGER.error("Failed to create the learner course history, learner: [] with course ID []", input.getLearnerId(), course.getCourseId().getId());
      }
    }

  }

  private void updateCourse(CloseClassroomCourseInput input, Course course) throws UnknownCourseTypeException, LmsRepositoryException
  {
    CourseType courseType = courseTypeResolver.resolve(course.getCourseType().getType());
    String assessmentId = course.getAssessmentId();
    String certificateId = course.getCertificateId();
    CourseDetail courseDetail = course.getCourseDetail();
    courseDetail.getProperties().put(STATE, DONE);

    int enrollmentCount = (int) classroomCourseAttendanceRepository.findByCourseId(course.getCourseId()).getAttendances().stream()
        .filter(Attendance::isPresent).count();
    courseDetail.getProperties().put(ENROLLMENT_COUNT, Integer.toString(enrollmentCount));
    courseDetail.getProperties().put(ORIGINAL_TITLE, course.getCourseDetail().getTitle());

    courseRepository
        .update(CourseId.valueOf(input.getCourseId()),
            CourseCategoryId.valueOf(course.getCourseCategoryId().getId()),
            courseDetail, courseType,
            assessmentId,
            certificateId);
    if (course.getCourseDetail().hasAssessment())
    {
      try
      {
        SendAssessmentLinkNotification sendAssessmentLinkNotification = new SendAssessmentLinkNotification(lmsRepositoryRegistry, lmsServiceRegistry,
            classroomCourseAttendanceRepository);
        sendAssessmentLinkNotification.execute(input.getCourseId());
      }
      catch (UseCaseException e)
      {
        LOGGER.error(String.format("failed to send assessment link this %s course!", course.getCourseId().getId()));
      }
    }
  }
}



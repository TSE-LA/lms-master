package mn.erin.lms.base.domain.usecase.certificate;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.lms.base.aim.user.Manager;
import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.base.domain.model.certificate.LearnerCertificate;
import mn.erin.lms.base.domain.model.course.Course;
import mn.erin.lms.base.aim.user.Instructor;
import mn.erin.lms.base.aim.user.Learner;
import mn.erin.lms.base.aim.user.LearnerId;
import mn.erin.lms.base.aim.user.Supervisor;
import mn.erin.lms.base.domain.repository.CourseRepository;
import mn.erin.lms.base.domain.repository.LearnerCertificateRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.LmsUseCase;
import mn.erin.lms.base.domain.usecase.certificate.dto.ReceivedCertificateDto;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Authorized(users = {Learner.class, Supervisor.class,  Manager.class, Instructor.class })
public class GetReceivedCertificates extends LmsUseCase<String, List<ReceivedCertificateDto>>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(GetReceivedCertificates.class);

  public static final String ORIGINAL_TITLE = "originalTitle";

  private final LearnerCertificateRepository learnerCertificateRepository;
  private final CourseRepository courseRepository;

  public GetReceivedCertificates(LmsRepositoryRegistry lmsRepositoryRegistry,
      LmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
    this.learnerCertificateRepository = lmsRepositoryRegistry.getLearnerCertificateRepository();
    this.courseRepository = lmsRepositoryRegistry.getCourseRepository();
  }

  @Override
  protected List<ReceivedCertificateDto> executeImpl(String learnerId)
  {
    List<LearnerCertificate> learnerCertificates = learnerCertificateRepository.listAll(LearnerId.valueOf(learnerId));

    List<ReceivedCertificateDto> result = new ArrayList<>();
    for (LearnerCertificate learnerCertificate : learnerCertificates)
    {
      try
      {
        result.add(toDto(learnerCertificate));
      }
      catch (LmsRepositoryException e)
      {
        LOGGER.error(e.getMessage(), e);
      }
    }
    return result;
  }

  private ReceivedCertificateDto toDto(LearnerCertificate learnerCertificate) throws LmsRepositoryException
  {
    String courseName;
    Course course = courseRepository.fetchById(learnerCertificate.getCourseId());
    courseName = course.getCourseDetail().getTitle();

    ReceivedCertificateDto dto = new ReceivedCertificateDto();
    dto.setCertificateId(learnerCertificate.getCertificateId().getId());
    dto.setCourseId(learnerCertificate.getCourseId().getId());
    dto.setLearnerId(learnerCertificate.getLearnerId().getId());
    dto.setCourseName(courseName);
    String courseType;
    String classRoomCourseAddress = course.getCourseDetail().getProperties().get("address");
    if (classRoomCourseAddress != null)
    {
      courseType = "ClassroomCourse";
    }
    else
    {
      courseType = "OnlineCourse";
    }
    dto.setCourseType(courseType);
    dto.setDate(DateTimeFormatter.ISO_DATE.format(learnerCertificate.getReceivedDate()));

    return dto;
  }
}

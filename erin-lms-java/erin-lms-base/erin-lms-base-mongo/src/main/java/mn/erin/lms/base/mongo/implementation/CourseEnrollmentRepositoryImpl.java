package mn.erin.lms.base.mongo.implementation;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import mn.erin.lms.base.domain.model.course.CourseId;
import mn.erin.lms.base.domain.model.enrollment.CourseEnrollment;
import mn.erin.lms.base.domain.model.enrollment.EnrollmentState;
import mn.erin.lms.base.aim.user.LearnerId;
import mn.erin.lms.base.domain.repository.CourseEnrollmentRepository;
import mn.erin.lms.base.mongo.document.enrollment.MongoCourseEnrollment;
import mn.erin.lms.base.mongo.document.enrollment.MongoEnrollmentState;
import mn.erin.lms.base.mongo.repository.MongoCourseEnrollmentRepository;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class CourseEnrollmentRepositoryImpl implements CourseEnrollmentRepository
{
  private static final String ENROLLMENT_ID_DELIMITER = "-";

  private final MongoCourseEnrollmentRepository mongoCourseEnrollmentRepository;

  public CourseEnrollmentRepositoryImpl(MongoCourseEnrollmentRepository mongoCourseEnrollmentRepository)
  {
    this.mongoCourseEnrollmentRepository = mongoCourseEnrollmentRepository;
  }

  @Override
  public void save(CourseEnrollment courseEnrollment)
  {
    mongoCourseEnrollmentRepository.save(map(courseEnrollment));
  }

  @Override
  public EnrollmentState getEnrollmentState(LearnerId learnerId, CourseId courseId)
  {
    return map(getMongoCourseEnrollment(learnerId, courseId)).getEnrollmentState();
  }

  @Override
  public List<CourseEnrollment> listAll(LearnerId learnerId)
  {
    return mongoCourseEnrollmentRepository.findByLearnerId(learnerId.getId()).stream()
        .map(this::map).collect(Collectors.toList());
  }

  @Override
  public List<CourseEnrollment> listAll()
  {
    return mongoCourseEnrollmentRepository.findAll().stream()
        .map(this::map).collect(Collectors.toList());
  }

  @Override
  public List<CourseEnrollment> listAll(CourseId courseId)
  {
    return mongoCourseEnrollmentRepository.findByCourseId(courseId.getId()).stream()
        .map(this::map).collect(Collectors.toList());
  }

  @Override
  public int getEnrollmentCount(CourseId courseId)
  {
    return mongoCourseEnrollmentRepository.countByCourseId(courseId.getId());
  }

  @Override
  public int getEnrollmentCount(LearnerId learnerId)
  {
    return mongoCourseEnrollmentRepository.countByLearnerId(learnerId.getId());
  }

  @Override
  public int getEnrollmentCountByLearnerId(CourseId courseId, String learnerId)
  {
    return mongoCourseEnrollmentRepository.countByCourseIdAndLearnerId(courseId.getId(), learnerId);
  }

  @Override
  public boolean changeEnrollmentState(LearnerId learnerId, CourseId courseId, EnrollmentState enrollmentState)
  {
    MongoCourseEnrollment mongoCourseEnrollment = getMongoCourseEnrollment(learnerId, courseId);
    mongoCourseEnrollment.setEnrollmentState(MongoEnrollmentState.valueOf(enrollmentState.name()));
    mongoCourseEnrollmentRepository.save(mongoCourseEnrollment);
    return true;
  }

  @Override
  public boolean deleteAll(CourseId courseId)
  {
    List<MongoCourseEnrollment> enrollments = mongoCourseEnrollmentRepository.findByCourseId(courseId.getId());
    enrollments.forEach(mongoCourseEnrollmentRepository::delete);
    return true;
  }

  @Override
  public boolean deleteAll(LearnerId learnerId)
  {
    List<MongoCourseEnrollment> enrollments = mongoCourseEnrollmentRepository.findByLearnerId(learnerId.getId());
    enrollments.forEach(mongoCourseEnrollmentRepository::delete);
    return true;
  }

  @Override
  public boolean deleteAll(LearnerId learnerId, EnrollmentState enrollmentState)
  {
    List<MongoCourseEnrollment> enrollments = mongoCourseEnrollmentRepository.findByLearnerIdAndEnrollmentState(learnerId.getId(),
        MongoEnrollmentState.valueOf(enrollmentState.name()));
    enrollments.forEach(mongoCourseEnrollmentRepository::delete);
    return true;
  }

  @Override
  public boolean delete(LearnerId learnerId, CourseId courseId)
  {
    MongoCourseEnrollment mongoCourseEnrollment;
    try
    {
      mongoCourseEnrollment = getMongoCourseEnrollment(learnerId, courseId);
    }
    catch (NoSuchElementException e)
    {
      return false;
    }
    mongoCourseEnrollmentRepository.delete(mongoCourseEnrollment);
    return true;
  }

  private MongoCourseEnrollment getMongoCourseEnrollment(LearnerId learnerId, CourseId courseId)
  {
    Optional<MongoCourseEnrollment> mongoCourseEnrollment = mongoCourseEnrollmentRepository
        .findById(learnerId.getId() + ENROLLMENT_ID_DELIMITER + courseId.getId());

    if (!mongoCourseEnrollment.isPresent())
    {
      throw new NoSuchElementException("Enrollment not found for a learner [" + learnerId.getId() +
          "] on the course [" + courseId.getId() + "]");
    }

    return mongoCourseEnrollment.get();
  }

  private CourseEnrollment map(MongoCourseEnrollment mongoCourseEnrollment)
  {
    CourseEnrollment courseEnrollment = new CourseEnrollment(CourseId.valueOf(mongoCourseEnrollment.getCourseId()),
        LearnerId.valueOf(mongoCourseEnrollment.getLearnerId()));

    courseEnrollment.setEnrolledDate(mongoCourseEnrollment.getEnrolledDate());
    courseEnrollment.changeEnrollmentState(EnrollmentState.valueOf(mongoCourseEnrollment.getEnrollmentState().name()));

    return courseEnrollment;
  }

  private MongoCourseEnrollment map(CourseEnrollment courseEnrollment)
  {
    return new MongoCourseEnrollment(courseEnrollment.getCourseId().getId(),
        courseEnrollment.getLearnerId().getId(), MongoEnrollmentState.valueOf(courseEnrollment.getEnrollmentState().name()),
        courseEnrollment.getEnrolledDate());
  }
}

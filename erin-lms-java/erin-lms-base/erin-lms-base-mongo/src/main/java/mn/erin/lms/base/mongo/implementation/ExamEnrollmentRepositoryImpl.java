package mn.erin.lms.base.mongo.implementation;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import mn.erin.lms.base.aim.user.LearnerId;
import mn.erin.lms.base.domain.model.enrollment.ExamEnrollment;
import mn.erin.lms.base.domain.model.enrollment.ExamEnrollmentId;
import mn.erin.lms.base.domain.model.exam.ExamConstants;
import mn.erin.lms.base.domain.model.exam.ExamId;
import mn.erin.lms.base.domain.repository.ExamEnrollmentRepository;
import mn.erin.lms.base.mongo.document.enrollment.MongoExamEnrollment;
import mn.erin.lms.base.mongo.repository.MongoExamEnrollmentRepository;
import mn.erin.lms.base.mongo.util.IdGenerator;
import org.apache.commons.lang3.Validate;

/**
 * @author Galsan Bayart.
 */
public class ExamEnrollmentRepositoryImpl implements ExamEnrollmentRepository
{
  private final MongoExamEnrollmentRepository mongoExamEnrollmentRepository;

  public ExamEnrollmentRepositoryImpl(MongoExamEnrollmentRepository mongoExamEnrollmentRepository)
  {
    this.mongoExamEnrollmentRepository = mongoExamEnrollmentRepository;
  }

  @Override
  public ExamEnrollment createEnrollment(String examId, String learnerId, String permission)
  {
    MongoExamEnrollment mongoExamEnrollment = new MongoExamEnrollment(IdGenerator.generateId(), examId, learnerId, permission);
    return this.mapToExamEnrollment(mongoExamEnrollmentRepository.save(mongoExamEnrollment));
  }

  @Override
  public ExamEnrollment update(ExamEnrollment examEnrollment)
  {
    return null;
  }

  @Override
  public List<ExamEnrollment> getAll()
  {
    return mongoExamEnrollmentRepository.findAll().stream().map(this::mapToExamEnrollment).collect(Collectors.toList());
  }

  @Override
  public ExamEnrollment getByExamIdAndLearnerId(String examId, String learnerId)
  {

      Validate.notBlank(examId);
      Validate.notBlank(learnerId);
      MongoExamEnrollment mongoExamEnrollment = mongoExamEnrollmentRepository.findByExamIdAndLearnerId(examId, learnerId);

      if (null != mongoExamEnrollment)
      {
        return mapToExamEnrollment(mongoExamEnrollment);
      }
      return null;
  }

  @Override
  public List<ExamEnrollment> getByLearnerId(LearnerId learnerId)
  {
    return mongoExamEnrollmentRepository.findByLearnerId(learnerId.getId()).stream().map(this::mapToExamEnrollment).collect(Collectors.toList());
  }

  @Override
  public boolean deleteAllByExamId(ExamId examId)
  {
    List<MongoExamEnrollment> enrollments = mongoExamEnrollmentRepository.findAllByExamId(examId.getId());
    enrollments.forEach(mongoExamEnrollmentRepository::delete);
    return true;
  }

  @Override
  public boolean deleteByExamIdAndLearnerId(ExamId examId, LearnerId learnerId)
  {
    mongoExamEnrollmentRepository.deleteByExamIdAndLearnerId(examId.getId(), learnerId.getId());
    return true;
  }

  @Override
  public List<ExamEnrollment> getAllReadByUserId(String learnerId)
  {
    return mongoExamEnrollmentRepository.findByLearnerIdAndPermission(learnerId, ExamConstants.READ_PERMISSION).stream().map(this::mapToExamEnrollment)
        .collect(Collectors.toList());
  }

  @Override
  public List<ExamEnrollment> getAllWriteByUserId(String userId)
  {
    return mongoExamEnrollmentRepository.findByLearnerIdAndPermission(userId, ExamConstants.WRITE_PERMISSION).stream().map(this::mapToExamEnrollment)
        .collect(Collectors.toList());
  }

  @Override
  public Set<String> getAllReadLearnerByExamId(String examId)
  {
    return mongoExamEnrollmentRepository.findAllByExamIdAndPermission(examId, ExamConstants.READ_PERMISSION).stream().map(MongoExamEnrollment::getLearnerId).collect(Collectors.toSet());
  }

  private ExamEnrollment mapToExamEnrollment(MongoExamEnrollment mongoExamEnrollment)
  {
    return new ExamEnrollment(
        new ExamEnrollmentId(mongoExamEnrollment.getId()),
        mongoExamEnrollment.getExamId(),
        mongoExamEnrollment.getLearnerId(),
        mongoExamEnrollment.getPermission());
  }
}

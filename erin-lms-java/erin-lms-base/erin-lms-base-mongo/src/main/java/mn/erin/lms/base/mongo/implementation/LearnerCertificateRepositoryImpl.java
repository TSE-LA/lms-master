package mn.erin.lms.base.mongo.implementation;

import java.util.ArrayList;
import java.util.List;

import mn.erin.lms.base.domain.model.certificate.CertificateId;
import mn.erin.lms.base.domain.model.certificate.LearnerCertificate;
import mn.erin.lms.base.domain.model.course.CourseId;
import mn.erin.lms.base.aim.user.LearnerId;
import mn.erin.lms.base.domain.repository.LearnerCertificateRepository;
import mn.erin.lms.base.mongo.document.certificate.MongoLearnerCertificate;
import mn.erin.lms.base.mongo.repository.MongoLearnerCertificateRepository;
import mn.erin.lms.base.mongo.util.IdGenerator;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class LearnerCertificateRepositoryImpl implements LearnerCertificateRepository
{
  private final MongoLearnerCertificateRepository mongoLearnerCertificateRepository;

  public LearnerCertificateRepositoryImpl(MongoLearnerCertificateRepository mongoLearnerCertificateRepository)
  {
    this.mongoLearnerCertificateRepository = mongoLearnerCertificateRepository;
  }

  @Override
  public void save(LearnerCertificate learnerCertificate)
  {
    String id = IdGenerator.generateId();
    MongoLearnerCertificate mongoLearnerCertificate = new MongoLearnerCertificate(id, learnerCertificate.getLearnerId().getId(),
        learnerCertificate.getCourseId().getId(), learnerCertificate.getCertificateId().getId(), learnerCertificate.getReceivedDate());
    mongoLearnerCertificateRepository.save(mongoLearnerCertificate);
  }

  @Override
  public List<LearnerCertificate> listAll(LearnerId learnerId)
  {
    List<MongoLearnerCertificate> mongoLearnerCertificates = mongoLearnerCertificateRepository.findByLearnerId(learnerId.getId());

    List<LearnerCertificate> learnerCertificates = new ArrayList<>();
    for (MongoLearnerCertificate mongoLearnerCertificate : mongoLearnerCertificates)
    {
      LearnerCertificate learnerCertificate = new LearnerCertificate(LearnerId.valueOf(mongoLearnerCertificate.getLearnerId()),
          CourseId.valueOf(mongoLearnerCertificate.getCourseId()), CertificateId.valueOf(mongoLearnerCertificate.getCertificateId()),
          mongoLearnerCertificate.getReceivedDate());
      learnerCertificates.add(learnerCertificate);
    }

    return learnerCertificates;
  }

  @Override
  public List<LearnerCertificate> listAll(CourseId courseId)
  {
    List<MongoLearnerCertificate> mongoLearnerCertificates = mongoLearnerCertificateRepository.findByCourseId(courseId.getId());

    List<LearnerCertificate> learnerCertificates = new ArrayList<>();
    for (MongoLearnerCertificate mongoLearnerCertificate : mongoLearnerCertificates)
    {
      LearnerCertificate learnerCertificate = new LearnerCertificate(LearnerId.valueOf(mongoLearnerCertificate.getLearnerId()),
          CourseId.valueOf(mongoLearnerCertificate.getCourseId()), CertificateId.valueOf(mongoLearnerCertificate.getCertificateId()),
          mongoLearnerCertificate.getReceivedDate());
      learnerCertificates.add(learnerCertificate);
    }

    return learnerCertificates;
  }

  @Override
  public List<LearnerCertificate> listAll(CourseId courseId, LearnerId learnerId)
  {
    List<MongoLearnerCertificate> mongoLearnerCertificates = mongoLearnerCertificateRepository.findByLearnerIdAndCourseId(courseId.getId(), learnerId.getId());

    List<LearnerCertificate> learnerCertificates = new ArrayList<>();
    for (MongoLearnerCertificate mongoLearnerCertificate : mongoLearnerCertificates)
    {
      LearnerCertificate learnerCertificate = new LearnerCertificate(LearnerId.valueOf(mongoLearnerCertificate.getLearnerId()),
          CourseId.valueOf(mongoLearnerCertificate.getCourseId()), CertificateId.valueOf(mongoLearnerCertificate.getCertificateId()),
          mongoLearnerCertificate.getReceivedDate());
      learnerCertificates.add(learnerCertificate);
    }

    return learnerCertificates;
  }

  @Override
  public boolean deleteByCourseId(CourseId courseId)
  {
    List<MongoLearnerCertificate> learnerCertificates = mongoLearnerCertificateRepository.findByCourseId(courseId.getId());
    learnerCertificates.forEach(mongoLearnerCertificateRepository::delete);
    return true;
  }

  @Override
  public boolean exists(CourseId courseId, LearnerId learnerId)
  {
    return mongoLearnerCertificateRepository.findByCourseIdAndLearnerId(courseId.getId(), learnerId.getId()).isPresent();
  }
}

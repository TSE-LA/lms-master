package mn.erin.lms.base.mongo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

import mn.erin.lms.base.mongo.document.certificate.MongoLearnerCertificate;

/**
 * @author Bat-Erdene Tsogoo.
 */
public interface MongoLearnerCertificateRepository extends MongoRepository<MongoLearnerCertificate, String>,
    QueryByExampleExecutor<MongoLearnerCertificate>
{
  List<MongoLearnerCertificate> findByLearnerId(String learnerId);

  List<MongoLearnerCertificate> findByCourseId(String courseId);

  List<MongoLearnerCertificate> findByLearnerIdAndCourseId(String learnerId, String courseId);

  Optional<MongoLearnerCertificate> findByCourseIdAndLearnerId(String courseId, String learnerId);
}

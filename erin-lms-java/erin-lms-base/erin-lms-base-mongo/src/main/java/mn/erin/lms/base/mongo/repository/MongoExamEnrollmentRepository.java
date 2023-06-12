package mn.erin.lms.base.mongo.repository;

import java.util.List;

import mn.erin.lms.base.mongo.document.enrollment.MongoExamEnrollment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

/**
 * @author Galsan Bayart.
 */
public interface MongoExamEnrollmentRepository extends MongoRepository<MongoExamEnrollment, String>, QueryByExampleExecutor<MongoExamEnrollment>
{
  MongoExamEnrollment findByExamIdAndLearnerId(String examId, String learnerId);

  List<MongoExamEnrollment> findAllByExamId(String examId);

  List<MongoExamEnrollment> findAllByExamIdAndPermission(String examId, String permission);

  List<MongoExamEnrollment> findByLearnerId(String learnerId);

  void deleteById(String examId);

  void deleteByExamIdAndLearnerId(String examId, String learnerId);

  List<MongoExamEnrollment> findByLearnerIdAndPermission(String learnerId, String permission);
}

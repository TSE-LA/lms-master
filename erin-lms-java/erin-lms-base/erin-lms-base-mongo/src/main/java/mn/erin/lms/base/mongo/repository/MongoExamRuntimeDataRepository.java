package mn.erin.lms.base.mongo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import mn.erin.lms.base.mongo.document.exam.MongoExamRuntimeData;

/**
 * @author Byambajav
 */
public interface MongoExamRuntimeDataRepository extends MongoRepository<MongoExamRuntimeData, String>
{
  List<MongoExamRuntimeData> findAllByLearnerId(String learnerId);

  List<MongoExamRuntimeData> findAllByExamId(String examId);

  MongoExamRuntimeData findByLearnerIdAndExamId(String learnerId, String examId);

  Optional<MongoExamRuntimeData> findByExamIdAndLearnerId(String examId, String learnerId);
}

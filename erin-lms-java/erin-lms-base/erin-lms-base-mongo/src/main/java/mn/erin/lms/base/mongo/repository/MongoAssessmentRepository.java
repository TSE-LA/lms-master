package mn.erin.lms.base.mongo.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

import mn.erin.lms.base.mongo.document.assessment.MongoAssessment;

/**
 * @author Bat-Erdene Tsogoo.
 */
public interface MongoAssessmentRepository extends MongoRepository<MongoAssessment, String>,
    QueryByExampleExecutor<MongoAssessment>
{
  List<MongoAssessment> findByCreatedDateBetween(LocalDateTime from, LocalDateTime to);

}

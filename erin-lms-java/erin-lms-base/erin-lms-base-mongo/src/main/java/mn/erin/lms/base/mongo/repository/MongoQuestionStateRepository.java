package mn.erin.lms.base.mongo.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

import mn.erin.lms.base.mongo.document.exam.question.MongoQuestionState;

/**
 * @author Galsan Bayart
 */
public interface MongoQuestionStateRepository extends MongoRepository<MongoQuestionState, String>, QueryByExampleExecutor<MongoQuestionState>
{
  List<MongoQuestionState> getAllByTenantId(String tenantId);
}

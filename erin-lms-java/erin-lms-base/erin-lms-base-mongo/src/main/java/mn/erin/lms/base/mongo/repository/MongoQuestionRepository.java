package mn.erin.lms.base.mongo.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

import mn.erin.lms.base.mongo.document.exam.question.MongoQuestion;
import mn.erin.lms.base.mongo.document.exam.question.MongoQuestionStatus;

/**
 * @author Galsan Bayart
 */
public interface MongoQuestionRepository extends MongoRepository<MongoQuestion, String>, QueryByExampleExecutor<MongoQuestion>
{
  List<MongoQuestion> findAllByStatus(MongoQuestionStatus status);

  List<MongoQuestion> findAllByCategoryIdAndStatus(String categoryId, MongoQuestionStatus status);

  List<MongoQuestion> findAllByGroupIdAndStatus(String groupId, MongoQuestionStatus status);

  List<MongoQuestion> findAllByGroupIdAndCategoryIdAndStatus(String groupId, String categoryId, MongoQuestionStatus status);

  List<MongoQuestion> findAllByScoreAndStatus(int score, MongoQuestionStatus status);

  List<MongoQuestion> findAllByCategoryIdAndScoreAndStatus(String categoryId, int score, MongoQuestionStatus status);

  List<MongoQuestion> findAllByGroupIdAndScoreAndStatus(String groupId, int score, MongoQuestionStatus status);

  List<MongoQuestion> findAllByGroupIdAndCategoryIdAndScoreAndStatus(String groupId, String categoryId, int score, MongoQuestionStatus status);

  List<MongoQuestion> findAllByGroupIdAndCategoryIdAndScore(String groupId, String categoryId, int score);

  List<MongoQuestion> findAllByIdIn(Set<String> questionIds);

  List<MongoQuestion> findAllByCategoryId(String categoryId);
}
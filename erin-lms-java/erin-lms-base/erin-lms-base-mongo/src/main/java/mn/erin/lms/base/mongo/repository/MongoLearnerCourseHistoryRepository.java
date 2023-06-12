package mn.erin.lms.base.mongo.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.QueryByExampleExecutor;

import mn.erin.lms.base.mongo.document.course.MongoLearnerCourseHistory;

/**
 * @author Temuulen Naranbold
 */
public interface MongoLearnerCourseHistoryRepository extends MongoRepository<MongoLearnerCourseHistory, String>, QueryByExampleExecutor<MongoLearnerCourseHistory>
{
  Optional<MongoLearnerCourseHistory> findByCourseIdAndUserId(String courseId, String userId);

  boolean existsByCourseIdAndUserId(String courseId, String userId);

  List<MongoLearnerCourseHistory> findAllByUserId(String userId);

  @Query(value = "{ $and: [ { 'userId' : ?0 }, { 'completionDate' : {$gte: ?1, $lte: ?2} } ] }")
  List<MongoLearnerCourseHistory> findAllByUserIdAndCompletionDateBetween(String userId, LocalDateTime from, LocalDateTime to);
}

package mn.erin.lms.unitel.mongo.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.mongodb.repository.MongoRepository;

import mn.erin.lms.unitel.mongo.document.MongoLearnerSuccess;

/**
 * @author Byambajav
 */
public interface MongoLearnerSuccessRepository extends MongoRepository<MongoLearnerSuccess, String>
{
  List<MongoLearnerSuccess> getAllByLearnerIdAndCourseTypeAndYearAndMonthBetween(String learnerId, String courseType, int year, int startMonth, int endMonth);

  List<MongoLearnerSuccess> getAllByLearnerIdInAndYearAndMonthBetween(Set<String> learnerIds, int year, int startMonth, int endMonth);

  List<MongoLearnerSuccess> getAllByLearnerId(String learnerId);

  Optional<MongoLearnerSuccess> findByLearnerIdAndYearAndMonth(String learnerId, int year, int month);

  int countByYearAndMonthAndCourseType(int year, int month, String courseType);
}

package mn.erin.lms.jarvis.mongo.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.mongodb.repository.MongoRepository;

import mn.erin.lms.jarvis.mongo.document.report.MongoLearnerSuccess;

/**
 * @author Galsan Bayart
 */
public interface MongoLearnerSuccessRepository extends MongoRepository<MongoLearnerSuccess, String>
{
  List<MongoLearnerSuccess> getAllByLearnerIdAndYearAndMonthBetween(String learnerId, int year, int startMonth, int endMonth);

  List<MongoLearnerSuccess> getAllByLearnerIdInAndYearAndMonthBetween(Set<String> learnerIds, int year, int startMonth, int endMonth);

  List<MongoLearnerSuccess> getAllByLearnerId(String learnerId);

  Optional<MongoLearnerSuccess> findByLearnerIdAndYearAndMonth(String learnerId, int year, int month);

  int countByYearAndMonth(int year, int month);
}

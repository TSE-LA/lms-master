package mn.erin.lms.unitel.mongo.repository;

import java.time.LocalDateTime;
import java.util.List;

import mn.erin.lms.unitel.mongo.document.MongoAssessmentReport;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

/**
 * @author Bat-Erdene Tsogoo.
 */
public interface MongoAssessmentReportRepository extends MongoRepository<MongoAssessmentReport, String>,
    QueryByExampleExecutor<MongoAssessmentReport>
{
  List<MongoAssessmentReport> findByAssessmentIdAndDateBetween(String assessmentId, LocalDateTime startDate, LocalDateTime endDate);
}

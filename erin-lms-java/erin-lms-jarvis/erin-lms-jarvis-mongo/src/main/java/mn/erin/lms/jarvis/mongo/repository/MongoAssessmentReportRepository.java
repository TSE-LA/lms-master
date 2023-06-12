package mn.erin.lms.jarvis.mongo.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

import mn.erin.lms.jarvis.mongo.document.report.MongoAssessmentReport;

/**
 * @author Bat-Erdene Tsogoo.
 */
public interface MongoAssessmentReportRepository extends MongoRepository<MongoAssessmentReport, String>,
    QueryByExampleExecutor<MongoAssessmentReport>
{
  List<MongoAssessmentReport> findByAssessmentIdAndDateBetween(String assessmentId, LocalDateTime startDate, LocalDateTime endDate);
}

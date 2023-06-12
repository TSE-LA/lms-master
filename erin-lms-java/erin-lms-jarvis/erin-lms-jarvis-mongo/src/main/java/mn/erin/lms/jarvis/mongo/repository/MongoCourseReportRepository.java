package mn.erin.lms.jarvis.mongo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import mn.erin.lms.jarvis.mongo.document.report.MongoCourseReport;

/**
 * @author Bat-Erdene Tsogoo.
 */
public interface MongoCourseReportRepository extends MongoRepository<MongoCourseReport, String>
{

}

package mn.erin.lms.unitel.mongo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import mn.erin.lms.unitel.mongo.document.MongoCourseReport;

/**
 * @author Bat-Erdene Tsogoo.
 */
public interface MongoCourseReportRepository extends MongoRepository<MongoCourseReport, String>
{

}

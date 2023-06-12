package mn.erin.lms.base.mongo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import mn.erin.lms.base.mongo.document.assessment.MongoCourseAssessment;

/**
 * @author Bat-Erdene Tsogoo.
 */
public interface MongoCourseAssessmentRepository extends MongoRepository<MongoCourseAssessment, String>
{
}

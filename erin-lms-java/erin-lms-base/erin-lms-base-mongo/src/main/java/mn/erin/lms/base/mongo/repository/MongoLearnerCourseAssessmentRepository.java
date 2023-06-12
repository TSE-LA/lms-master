package mn.erin.lms.base.mongo.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

import mn.erin.lms.base.mongo.document.assessment.MongoLearnerCourseAssessment;

/**
 * @author Erdenetulga
 */
public interface MongoLearnerCourseAssessmentRepository
    extends MongoRepository<MongoLearnerCourseAssessment, String>, QueryByExampleExecutor<MongoLearnerCourseAssessment>

{
  Optional<MongoLearnerCourseAssessment> findByCourseIdAndAndLearnerId(String courseId, String learnerId);
}

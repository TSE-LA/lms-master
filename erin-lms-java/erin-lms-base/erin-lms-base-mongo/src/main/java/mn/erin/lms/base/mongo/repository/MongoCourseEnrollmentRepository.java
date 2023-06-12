package mn.erin.lms.base.mongo.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

import mn.erin.lms.base.mongo.document.enrollment.MongoCourseEnrollment;
import mn.erin.lms.base.mongo.document.enrollment.MongoEnrollmentState;

/**
 * @author Bat-Erdene Tsogoo.
 */
public interface MongoCourseEnrollmentRepository extends MongoRepository<MongoCourseEnrollment, String>,
    QueryByExampleExecutor<MongoCourseEnrollment>
{
  List<MongoCourseEnrollment> findByCourseId(String courseId);

  List<MongoCourseEnrollment> findByLearnerId(String learnerId);

  List<MongoCourseEnrollment> findByLearnerIdAndEnrollmentState(String learnerId, MongoEnrollmentState enrollmentState);

  int countByCourseId(String courseId);

  int countByLearnerId(String learnerId);

  int countByCourseIdAndLearnerId(String courseId, String learnerId);
}

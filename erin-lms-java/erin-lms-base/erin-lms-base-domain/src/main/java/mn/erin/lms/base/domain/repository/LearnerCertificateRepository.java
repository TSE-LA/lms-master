package mn.erin.lms.base.domain.repository;

import java.util.List;

import mn.erin.lms.base.domain.model.certificate.LearnerCertificate;
import mn.erin.lms.base.domain.model.course.CourseId;
import mn.erin.lms.base.aim.user.LearnerId;

/**
 * @author Bat-Erdene Tsogoo.
 */
public interface LearnerCertificateRepository
{
  void save(LearnerCertificate learnerCertificate);

  List<LearnerCertificate> listAll(LearnerId learnerId);

  List<LearnerCertificate> listAll(CourseId courseId);

  List<LearnerCertificate> listAll(CourseId courseId, LearnerId learnerId);

  /**
   * Deletes a course learner certificate
   *
   * @param courseId  The ID of the course
   * @return true if deleted, otherwise false
   */

  boolean deleteByCourseId(CourseId courseId);

  boolean exists(CourseId courseId, LearnerId learnerId);
}

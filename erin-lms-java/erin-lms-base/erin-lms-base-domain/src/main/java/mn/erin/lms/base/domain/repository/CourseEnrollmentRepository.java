package mn.erin.lms.base.domain.repository;

import java.util.List;

import mn.erin.lms.base.domain.model.course.CourseId;
import mn.erin.lms.base.domain.model.enrollment.CourseEnrollment;
import mn.erin.lms.base.domain.model.enrollment.EnrollmentState;
import mn.erin.lms.base.aim.user.LearnerId;

/**
 * @author Bat-Erdene Tsogoo.
 */
public interface CourseEnrollmentRepository
{
  /**
   * Saves course enrollment
   *
   * @param courseEnrollment course enrollment object
   */
  void save(CourseEnrollment courseEnrollment);

  /**
   * Gets the enrollment state of a learner in a particular course
   *
   * @param learnerId The learner ID
   * @param courseId  The course ID
   * @return The enrollment state
   */
  EnrollmentState getEnrollmentState(LearnerId learnerId, CourseId courseId);

  /**
   * Lists all enrollments of a learner
   *
   * @param learnerId The learner ID
   * @return A list of course enrollments
   */
  List<CourseEnrollment> listAll(LearnerId learnerId);


  /**
   * Lists all enrollments of a learner
   *
   * @return A list of course enrollments
   */
  List<CourseEnrollment> listAll();

  /**
   * Lists all enrollments of a give ncourse
   *
   * @param courseId The course ID
   * @return A list of course enrollments
   */
  List<CourseEnrollment> listAll(CourseId courseId);

  /**
   * Gets enrollment count
   *
   * @param courseId The ID of the course
   * @return The enrollment count
   */
  int getEnrollmentCount(CourseId courseId);
  
   /**
   * Retrieves total enrollment count for a user.
   */
  int getEnrollmentCount(LearnerId learnerId);

  /**
   * Gets enrollment count by department id
   *
   * @param courseId The ID of the course
   * @return The enrollment count
   */
  int getEnrollmentCountByLearnerId(CourseId courseId, String learnerId);

  /**
   * Updates course enrollment state
   *
   * @param learnerId       The learner ID
   * @param courseId        The course ID
   * @param enrollmentState The enrollment state
   * @return true if updated; otherwise, false
   */
  boolean changeEnrollmentState(LearnerId learnerId, CourseId courseId, EnrollmentState enrollmentState);

  /**
   * Deletes all enrollments of a given course
   *
   * @param courseId The ID of the course
   * @return true if deleted, otherwise false
   */
  boolean deleteAll(CourseId courseId);

  /**
   * Deletes all enrollments associated with a learner
   *
   * @param learnerId The ID of the learner
   * @return true if deleted; otherwise false
   */
  boolean deleteAll(LearnerId learnerId);

  /**
   * Deletes all enrollments associated to a learner with a specified state
   *
   * @param learnerId       The ID of the learner
   * @param enrollmentState The enrollment state
   * @return true if deleted; otherwise false
   */
  boolean deleteAll(LearnerId learnerId, EnrollmentState enrollmentState);

  /**
   * Deletes a course enrollment
   *
   * @param learnerId The ID of the learner
   * @param courseId  The ID of the course
   * @return true if deleted, otherwise false
   */
  boolean delete(LearnerId learnerId, CourseId courseId);
}

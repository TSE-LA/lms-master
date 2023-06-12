/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.lms.repository;

import java.util.Date;
import java.util.List;
import java.util.Set;

import mn.erin.lms.legacy.domain.lms.model.course.CourseId;
import mn.erin.lms.legacy.domain.lms.model.enrollment.CourseEnrollment;
import mn.erin.lms.legacy.domain.lms.model.enrollment.CourseEnrollmentState;
import mn.erin.lms.legacy.domain.lms.model.enrollment.LearnerId;

/**
 * @author Oyungerel Chuluunsukh.
 */
public interface CourseEnrollmentRepository
{
  /**
   * CREATES a course enrollment
   *
   * @param courseId  The unique ID of the course
   * @param learnerId user id
   * @param state     course enrollment state
   * @return A created course enrollment
   */
  CourseEnrollment createEnrollment(CourseId courseId, LearnerId learnerId, CourseEnrollmentState state);

  /**
   * GETS a course enrollment
   *
   * @param learnerId learner id
   * @param courseId  course id
   * @return A course enrollment
   */
  CourseEnrollment getEnrollment(LearnerId learnerId, CourseId courseId);

  /**
   * GETS a list of course enrollent
   *
   * @param learnerId learner id
   * @return A course enrollment
   */
  List<CourseEnrollment> getEnrollmentList(LearnerId learnerId);

  /**
   * GETS a list of course enrollent
   *
   * @param courseId The course id
   * @return A course enrollment
   */
  List<CourseEnrollment> getEnrollmentList(CourseId courseId);

  /**
   * GETS a list of course enrollments
   *
   * @param learnerId       learner id
   * @param enrollmentState enrollment state
   * @return A course enrollment
   */
  List<CourseEnrollment> getEnrollmentList(LearnerId learnerId, CourseEnrollmentState enrollmentState);

  /**
   * GETS a list of course enrollments
   *
   * @param courseIds  course ids
   * @return A course enrollment
   */

  List<CourseEnrollment> getEnrollments(Set<CourseId> courseIds);

  /**
   * GETS a list of course enrollments filtered by date
   *
   * @param startDate Starting date
   * @param endDate End date
   * @return Course enrollment list
   */
  List<CourseEnrollment> getEnrollmentList(Date startDate, Date endDate);

  /**
   * Updates a course enrollment by changing its enrollment status
   *
   * @param learnerId             learner id
   * @param courseId              course id
   * @param courseEnrollmentState course enrollment state
   * @return A course enrollment
   * @throws LMSRepositoryException If the course enrollment on the course id and learner id is not found
   */
  CourseEnrollment updateEnrollmentState(LearnerId learnerId, CourseId courseId, CourseEnrollmentState courseEnrollmentState) throws LMSRepositoryException;

  /**
   * Deletes course enrollments
   *
   * @param courseId course id
   * @return A boolean
   */
  boolean deleteEnrollments(CourseId courseId);

  /**
   * Deletes course enrollment
   *
   * @param courseId  course id
   * @param learnerId learner id
   * @return A boolean
   */
  boolean deleteEnrollment(CourseId courseId, LearnerId learnerId);

  /**
   * Deletes course enrollments
   *
   * @param enrollmentList list of course enrollments
   * @throws LMSRepositoryException If enrollments could not be deleted
   */

  void deleteEnrollments(Set<String> enrollmentList) throws LMSRepositoryException;
}

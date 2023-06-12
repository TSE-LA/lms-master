package mn.erin.lms.base.domain.repository;

import java.util.Collection;

import mn.erin.lms.base.domain.model.assessment.CourseAssessment;
import mn.erin.lms.base.domain.model.assessment.QuizId;
import mn.erin.lms.base.domain.model.course.CourseId;

/**
 * @author Bat-Erdene Tsogoo.
 */
public interface CourseAssessmentRepository
{
  /**
   * Creates a new course assessment
   *
   * @param courseId The ID of the course
   * @param quizzes  The list of quizzes
   * @return A new course assessment
   * @throws LmsRepositoryException If failed to create a course assessment
   */
  CourseAssessment create(CourseId courseId, Collection<QuizId> quizzes) throws LmsRepositoryException;

  /**
   * Updates a course assessment
   *
   * @param courseId The ID of the course
   * @param quizzes  The list of quizzes
   * @return An updated course assessment
   * @throws LmsRepositoryException If failed to update a course assessment
   */
  CourseAssessment update(CourseId courseId, Collection<QuizId> quizzes) throws LmsRepositoryException;

  /**
   * Fetches a course assessment by course ID
   *
   * @param courseId The ID of the course
   * @return A course assessment
   * @throws LmsRepositoryException If the course assessment does not exist
   */
  CourseAssessment fetchById(CourseId courseId) throws LmsRepositoryException;

  /**
   * Deletes a course assessment
   *
   * @param courseId The ID of the course whose assessment to delete
   * @return true if deleted; otherwise, false
   */
  boolean delete(CourseId courseId);
}

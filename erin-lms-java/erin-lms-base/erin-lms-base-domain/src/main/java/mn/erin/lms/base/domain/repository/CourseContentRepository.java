package mn.erin.lms.base.domain.repository;

import java.util.List;

import mn.erin.lms.base.domain.model.content.Attachment;
import mn.erin.lms.base.domain.model.content.CourseContent;
import mn.erin.lms.base.domain.model.content.CourseModule;
import mn.erin.lms.base.domain.model.course.CourseId;

/**
 * @author Bat-Erdene Tsogoo.
 */
public interface CourseContentRepository
{
  /**
   * Creates a new course content
   *
   * @param courseId      The ID of the course
   * @param courseModules The list of course modules
   * @param attachments   The list of attachments
   * @return A new course content
   * @throws LmsRepositoryException If failed to create a course content
   */
  CourseContent create(CourseId courseId, List<CourseModule> courseModules, List<Attachment> attachments)
      throws LmsRepositoryException;

  /**
   * Fetches a course content by course ID
   *
   * @param courseId The ID of the course whose content will be fetched
   * @return A course content
   * @throws LmsRepositoryException If the course content does not exist
   */
  CourseContent fetchById(CourseId courseId) throws LmsRepositoryException;

  /**
   * Updates a course content
   *
   * @param courseId      The ID of the course
   * @param courseModules The course modules
   * @param attachments The list of attachments
   * @return An updated course content
   * @throws LmsRepositoryException If failed to update a course content
   */
  CourseContent update(CourseId courseId, List<CourseModule> courseModules, List<Attachment> attachments) throws LmsRepositoryException;

  /**
   * @param courseId The ID of the course
   * @param attachments Updating course attachment
   * @return Updated CourseContent object
   * @throws LmsRepositoryException if CourseContent not found by given course ID
   */
  CourseContent updateAttachment(CourseId courseId, List<Attachment> attachments) throws LmsRepositoryException;

  /**
   * Deletes a course content.
   *
   * @param courseId The ID of the course whose content will be deleted
   * @return true if deleted; otherwise, false
   */
  boolean delete(CourseId courseId);

  /**
   * Checks if the course content exists on a course
   *
   * @param courseId the checking ID of a course
   * @return true if exists; otherwise false
   */
  boolean exists(CourseId courseId);
}

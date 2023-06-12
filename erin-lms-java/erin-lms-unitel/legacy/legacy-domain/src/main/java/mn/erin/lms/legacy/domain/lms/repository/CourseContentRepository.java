/*
 * (C)opyright, 2018, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.lms.repository;

import java.util.List;

import mn.erin.lms.legacy.domain.lms.model.content.Attachment;
import mn.erin.lms.legacy.domain.lms.model.content.CourseContent;
import mn.erin.lms.legacy.domain.lms.model.content.CourseModule;
import mn.erin.lms.legacy.domain.lms.model.course.CourseId;

/**
 * Responsible repository for {@link CourseContent} object.
 *
 * author Tamir Batmagnai.
 */
public interface CourseContentRepository
{
  /**
   * Creates course content with modules and attachments.
   *
   * @param courseId The unique id of course.
   * @param courseModules The modules of course.
   * @param attachmentsList The attachments list otherwise file ids.
   * @param additionalFiles The additional files list otherwise file names.
   * @return created course content.
   * @throws LMSRepositoryException if the parameters are invalid or other issues.
   */
  CourseContent create(CourseId courseId, List<CourseModule> courseModules, List<Attachment> attachmentsList, List<Attachment> additionalFiles) throws LMSRepositoryException;


  /**
   * Gets a course content with course id.
   *
   * @param courseId The unique ID of the course.
   * @return The course content object depending on course id.
   * @throws LMSRepositoryException if the course id invalid.
   */
  CourseContent get(CourseId courseId) throws LMSRepositoryException;

  /**
   * Updates a course content depending on course id.
   *
   * @param courseId The unique ID of the course.
   * @param courseModules The modules of course.
   * @param attachments The attachments list otherwise file ids.
   * @param additionalFiles The additional files list otherwise file names.
   * @return updated course content.
   * @throws LMSRepositoryException if course content does not exist with given course id.
   */
  CourseContent update(CourseId courseId, List<CourseModule> courseModules, List<Attachment> attachments, List<Attachment> additionalFiles) throws LMSRepositoryException;

  /**
   * Deletes a course content depending on course id.
   *
   * @param courseId The unique ID of the course.
   * @return true if it is deleted otherwise return false.
   */
  boolean deleteById(CourseId courseId);
  /**
   * Deletes a course content depending on course id.
   *
   * @param courseId The unique ID of the course.
   * @return true if it is deleted otherwise return false.
   */
  boolean deleteAllAttachmentsByCourseId(CourseId courseId, List<Attachment> attachments);
  /**
   * Deletes a course content depending on course id.
   *
   * @param courseId The unique ID of the course.
   * @return true if it is deleted otherwise return false.
   */
  boolean deleteAttachmentByAttachmentId(CourseId courseId, List<Attachment> attachments);
  /**
   * Deletes a course content depending on course id.
   *
   * @param courseId The unique ID of the course.
   * @return true if it is deleted otherwise return false.
   */
  boolean deleteAllAdditionalFilesByCourseId(CourseId courseId, List<Attachment> attachments);
  /**
   * Deletes a course content depending on course id.
   *
   * @param courseId The unique ID of the course.
   * @return true if it is deleted otherwise return false.
   */
  boolean deleteAdditionalFileByAttachmentId(CourseId courseId, List<Attachment> attachments);
}

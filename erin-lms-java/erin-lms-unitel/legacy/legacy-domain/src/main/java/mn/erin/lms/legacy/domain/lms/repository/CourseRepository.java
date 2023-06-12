/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.lms.repository;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mn.erin.lms.legacy.domain.lms.model.content.CourseContentId;
import mn.erin.lms.legacy.domain.lms.model.course.Course;
import mn.erin.lms.legacy.domain.lms.model.course.CourseCategoryId;
import mn.erin.lms.legacy.domain.lms.model.course.CourseDetail;
import mn.erin.lms.legacy.domain.lms.model.course.CourseId;
import mn.erin.lms.legacy.domain.lms.model.course.PublishStatus;
import mn.erin.lms.legacy.domain.lms.model.course.UserGroup;

public interface CourseRepository
{
  /**
   * Creates a new course
   *
   * @param courseDetail     The detail of the course
   * @param courseCategoryId The unique ID of the course category
   * @param userGroup        The user and groups of the course category
   * @return A new course
   * @throws LMSRepositoryException if the course has invalid properties
   */
  Course createCourse(CourseDetail courseDetail, CourseCategoryId courseCategoryId, UserGroup userGroup) throws LMSRepositoryException;

  /**
   * Gets a course by ID
   *
   * @param courseId The unique ID of the course
   * @return A course
   * @throws LMSRepositoryException if the course does not exist.
   */
  Course getCourse(CourseId courseId) throws LMSRepositoryException;

  /**
   * Gets a course by content ID
   *
   * @param contentId The unique ID of the content of the course
   * @return A course
   * @throws LMSRepositoryException if the course dies not exist.
   */
  Course getCourse(CourseContentId contentId) throws LMSRepositoryException;

  /**
   * Removes a course by ID
   *
   * @param courseId The unique ID of the course
   * @return true if it is removed; otherwise, false.
   */
  boolean removeCourse(CourseId courseId);

  /**
   * Lists courses by category.
   *
   * @param courseCategoryId The unique ID of the course category
   * @return A list of courses. Returns empty list if no courses were found.
   */
  List<Course> getCourseList(CourseCategoryId courseCategoryId);

  /**
   * Lists courses by category and publish status
   *
   * @param courseCategoryId The unique ID of the course category
   * @param status           The publish status of the course
   * @return A list of courses. Returns empty list if no courses were found.
   */
  List<Course> getCourseList(CourseCategoryId courseCategoryId, PublishStatus status);

  List<Course> getCourseList(CourseCategoryId courseCategoryId, PublishStatus status, Date startDate, Date endDate);

  List<Course> getCourseList(CourseCategoryId courseCategoryId, PublishStatus status, Date startDate, Date endDate, String state);

  /**
   * Lists courses by category and properties
   *
   * @param courseCategoryId The unique ID of the course category
   * @param properties       The properties of the course
   * @return A list of courses with specified parameter values. Returns empty list if no courses were found.
   */
  List<Course> getCourseList(CourseCategoryId courseCategoryId, Map<String, Object> properties);

  /**
   * Lists courses by category, publish status and properties.
   *
   * @param courseCategoryId The unique ID of the course category
   * @param status           The publish status of the course
   * @param properties       The properties of the course
   * @return A list of courses with specified parameter values. Returns empty list if no courses were found.
   */
  List<Course> getCourseList(CourseCategoryId courseCategoryId, PublishStatus status, Map<String, Object> properties);

  List<Course> getCourseList(PublishStatus status);

  List<Course> getCourseList(PublishStatus status, Date startDate, Date endDate);

  List<Course> getCourseList(PublishStatus status, Date startDate, Date endDate, String state);

  List<Course> getCourseList(Date startDate, Date endDate);

  List<Course> getCourseList(String group);

  List<Course> getCourseList(Set<String> enrolledGroups);

  List<Course> getCourseList(Set<String> enrolledGroups, Date startDate, Date endDate);

  List<Course> getCourseList(Set<String> enrolledGroups, Date startDate, Date endDate, String state);

  /**
   * Updates the detail of a course
   *
   * @param courseId     The unique ID of the course
   * @param courseDetail The detail of the course to update
   * @return An updated course
   * @throws LMSRepositoryException If the course does not exist or failed to update the course
   */
  Course update(CourseId courseId, CourseDetail courseDetail) throws LMSRepositoryException;

  /**
   * Updates the detail of a course
   *
   * @param courseId  The unique ID of the course
   * @param userGroup The user group of the course to update
   * @return An updated course
   * @throws LMSRepositoryException If the course does not exist or failed to update the course
   */
  Course update(CourseId courseId, UserGroup userGroup) throws LMSRepositoryException;

  /**
   * Updates a course by setting a course content ID
   *
   * @param courseId        The unique ID of the course
   * @param courseContentId The unique ID of the course content
   * @return An updated course
   * @throws LMSRepositoryException If the course does not exist or failed to assign the content to the course.
   */
  Course update(CourseId courseId, CourseContentId courseContentId) throws LMSRepositoryException;

  /**
   * Updates a course by changing its publish status
   *
   * @param courseId      The unique ID of the course
   * @param publishStatus Publish status
   * @return An updated course
   * @throws LMSRepositoryException If the course does not exist or failed to assign the content to the course.
   */
  Course update(CourseId courseId, PublishStatus publishStatus) throws LMSRepositoryException;

  /**
   * Updates the detail of a course
   *
   * @param courseId     The unique ID of the course
   * @param categoryId   The unique ID of the course category.
   * @param courseDetail The detail of the course to update
   * @return An updated course
   * @throws LMSRepositoryException If the course does not exist or failed to update the course
   */
  Course update(CourseId courseId, CourseCategoryId categoryId, CourseDetail courseDetail) throws LMSRepositoryException;
}

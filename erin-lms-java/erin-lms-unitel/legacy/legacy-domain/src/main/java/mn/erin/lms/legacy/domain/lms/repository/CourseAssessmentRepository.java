/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.lms.repository;

import java.util.List;

import mn.erin.lms.legacy.domain.lms.model.assessment.Assessment;
import mn.erin.lms.legacy.domain.lms.model.assessment.TestId;
import mn.erin.lms.legacy.domain.lms.model.course.CourseId;

/**
 * @author Oyungerel Chuluunsukh.
 */
public interface CourseAssessmentRepository
{
  /**
   * CREATES a new course assessment.
   *
   * @param courseId       The id of the course
   * @param courseTestList The assessment item list
   * @return A new course assessment
   * @throws LMSRepositoryException If failed to create a new course assessment
   */
  Assessment create(CourseId courseId, List<TestId> courseTestList) throws LMSRepositoryException;

  /**
   * UPDATES a course category.
   *
   * @param courseId       The id of the course
   * @param courseTestList The assessment item list
   * @return A course assessment
   * @throws LMSRepositoryException If failed to update course assessment
   */
  Assessment update(CourseId courseId, List<TestId> courseTestList) throws LMSRepositoryException;

  /**
   * GETS an assessment.
   *
   * @param courseId The id of the course
   * @return A course assessment
   * @throws LMSRepositoryException If failed to get course assessment
   */
  Assessment get(CourseId courseId) throws LMSRepositoryException;

  /**
   * DELETES an assessment.
   *
   * @param courseId The id of the course
   * @return A boolean on delete
   * @throws LMSRepositoryException If failed to delete course assessment
   */
  boolean delete(CourseId courseId) throws LMSRepositoryException;
}

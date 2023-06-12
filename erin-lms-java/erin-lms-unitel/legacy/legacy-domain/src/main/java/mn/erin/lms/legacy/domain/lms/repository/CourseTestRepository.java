/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.lms.repository;

import java.util.Date;
import java.util.List;

import mn.erin.lms.legacy.domain.lms.model.assessment.Question;
import mn.erin.lms.legacy.domain.lms.model.assessment.Test;
import mn.erin.lms.legacy.domain.lms.model.assessment.TestId;

/**
 * @author Oyungerel Chuluunsukh.
 */
public interface CourseTestRepository
{
  /**
   * CREATES a new course test.
   *
   * @param questions The course test questions
   * @param name      The course test name
   * @param graded    The course test graded state
   * @param timeLimit The course test timeLimit
   * @param dueDate   The course test due Date
   * @return A new course test
   * @throws LMSRepositoryException If failed to create a new course test
   */
  Test create(List<Question> questions, String name, boolean graded, Long timeLimit, Date dueDate) throws LMSRepositoryException;

  /**
   * CREATES a new course test with max attempts and threshold score included
   *
   * @param questions      The course test questions
   * @param name           The course test name
   * @param graded         The course test graded state
   * @param timeLimit      The course test timeLimit
   * @param dueDate        The course test due Date
   * @param maxAttempts    The number of attempts required for the test
   * @param thresholdScore The threshold score of the test
   * @return A new course test
   * @throws LMSRepositoryException If failed to create a new course test
   */
  Test create(List<Question> questions, String name, boolean graded, Long timeLimit, Date dueDate, Integer maxAttempts, Double thresholdScore)
      throws LMSRepositoryException;

  /**
   * UPDATES a new course test.
   *
   * @param id        The course test id
   * @param questions The course test questions
   * @return A course test
   * @throws LMSRepositoryException If failed to update a new course test
   */
  Test update(TestId id, List<Question> questions) throws LMSRepositoryException;

  /**
   * UPDATES a course test including its max attempts and threshold score
   *
   * @param id             The course test id
   * @param questions      The course test questions
   * @param maxAttempts    The number of attempts required for the test
   * @param thresholdScore The threshold score of the test
   * @return An updated course test
   * @throws LMSRepositoryException If failed to update a new course test
   */
  Test update(TestId id, List<Question> questions, Integer maxAttempts, Double thresholdScore) throws LMSRepositoryException;

  /**
   * GETS a new course test.
   *
   * @param id The id of the course test
   * @return A course test
   * @throws LMSRepositoryException If failed to get a new course test
   */
  Test get(TestId id) throws LMSRepositoryException;

  /**
   * DELETES a course test.
   *
   * @param id The id of the course test
   * @return A boolean
   * @throws LMSRepositoryException If failed to get a new course test
   */
  boolean delete(TestId id) throws LMSRepositoryException;
}

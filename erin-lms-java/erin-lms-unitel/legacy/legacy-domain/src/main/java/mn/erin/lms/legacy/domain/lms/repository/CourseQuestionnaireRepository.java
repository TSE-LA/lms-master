/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.lms.repository;

import mn.erin.lms.legacy.domain.lms.model.assessment.Test;
import mn.erin.lms.legacy.domain.lms.model.course.CourseId;

/**
 * @author Oyungerel Chuluunsukh.
 */
public interface CourseQuestionnaireRepository
{
  /**
   * GETS a questionnaire test.
   *
   * @param courseId The id of the course
   * @return A course questionnaire
   */
  Test get(CourseId courseId);
}

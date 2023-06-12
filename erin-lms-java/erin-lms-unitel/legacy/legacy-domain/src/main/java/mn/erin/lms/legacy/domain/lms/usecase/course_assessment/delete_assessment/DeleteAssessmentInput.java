/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.lms.usecase.course_assessment.delete_assessment;

/**
 * @author Oyungerel Chuluunsukh.
 */
public class DeleteAssessmentInput
{
  private String courseId;

  public DeleteAssessmentInput(String courseId)
  {
    this.courseId = courseId;
  }

  public String getCourseId()
  {

    return this.courseId;
  }
}

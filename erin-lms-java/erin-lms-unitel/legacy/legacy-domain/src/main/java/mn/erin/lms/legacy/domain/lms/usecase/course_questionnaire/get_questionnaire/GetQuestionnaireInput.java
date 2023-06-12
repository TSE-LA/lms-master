/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.lms.usecase.course_questionnaire.get_questionnaire;

import org.apache.commons.lang3.Validate;

/**
 * @author Oyungerel Chuluunsukh.
 */
public class GetQuestionnaireInput
{
  private String courseId;

  public GetQuestionnaireInput(String courseId)
  {
    this.courseId = Validate.notBlank(courseId, "Course id cannot be null");
  }

  public String getCourseId()
  {
    return courseId;
  }

  public void setCourseId(String courseId)
  {
    this.courseId = courseId;
  }
}

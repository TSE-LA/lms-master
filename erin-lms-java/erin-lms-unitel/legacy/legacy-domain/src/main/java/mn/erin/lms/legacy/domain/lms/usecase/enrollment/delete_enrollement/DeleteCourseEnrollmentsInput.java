/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.lms.usecase.enrollment.delete_enrollement;

import org.apache.commons.lang3.Validate;

/**
 * @author Oyungerel Chuluunsukh.
 */
public class DeleteCourseEnrollmentsInput
{
  private String courseId;

  public DeleteCourseEnrollmentsInput(String courseId)
  {
    this.courseId = Validate.notBlank(courseId, "Course cannot be blank!");
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

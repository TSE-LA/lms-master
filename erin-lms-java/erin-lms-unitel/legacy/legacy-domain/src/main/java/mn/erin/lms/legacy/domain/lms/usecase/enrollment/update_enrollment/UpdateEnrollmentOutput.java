/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.lms.usecase.enrollment.update_enrollment;

import mn.erin.lms.legacy.domain.lms.model.enrollment.CourseEnrollment;

/**
 * @author Oyungerel Chuluunsukh.
 */
public class UpdateEnrollmentOutput
{
  private CourseEnrollment courseEnrollment;

  public UpdateEnrollmentOutput(CourseEnrollment courseEnrollment)
  {
    this.courseEnrollment = courseEnrollment;
  }

  public CourseEnrollment getCourseEnrollment()
  {
    return courseEnrollment;
  }

  public void setCourseEnrollment(CourseEnrollment courseEnrollment)
  {
    this.courseEnrollment = courseEnrollment;
  }
}

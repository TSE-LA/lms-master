/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.lms.usecase.enrollment.create_enrollment;

import java.util.ArrayList;
import java.util.List;

import mn.erin.lms.legacy.domain.lms.model.enrollment.CourseEnrollment;

/**
 * @author Oyungerel Chuluunsukh.
 */
public class CreateCourseEnrollmentsOutput
{
  private List<CourseEnrollment> courseEnrollments = new ArrayList<>();

  public void addEnrollment(CourseEnrollment enrollment)
  {
    courseEnrollments.add(enrollment);
  }

  public List<CourseEnrollment> getEnrollments()
  {
    return this.courseEnrollments;
  }
}

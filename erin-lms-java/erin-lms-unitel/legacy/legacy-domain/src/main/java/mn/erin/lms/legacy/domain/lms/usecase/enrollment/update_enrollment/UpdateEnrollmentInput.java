/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.lms.usecase.enrollment.update_enrollment;

import org.apache.commons.lang3.Validate;

/**
 * @author Oyungerel Chuluunsukh.
 */
public class UpdateEnrollmentInput
{
  private String courseId;
  private String learnerId;

  public UpdateEnrollmentInput(String courseId, String learnerId)
  {
    this.courseId = Validate.notBlank(courseId, "Course id cannot be blank!");
    this.learnerId = Validate.notBlank(learnerId, "Learner id cannot be blank!");
  }

  public String getCourseId()
  {
    return courseId;
  }

  public void setCourseId(String courseId)
  {
    this.courseId = courseId;
  }

  public String getLearnerId()
  {
    return learnerId;
  }

  public void setLearnerId(String learnerId)
  {
    this.learnerId = learnerId;
  }
}

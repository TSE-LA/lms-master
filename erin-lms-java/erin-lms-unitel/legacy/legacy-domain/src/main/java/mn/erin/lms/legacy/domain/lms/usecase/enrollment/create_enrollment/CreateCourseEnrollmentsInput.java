/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.lms.usecase.enrollment.create_enrollment;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.Validate;

/**
 * @author Oyungerel Chuluunsukh.
 */
public class CreateCourseEnrollmentsInput
{
  private String courseId;
  private Set<String> learnerIds = new HashSet<>();

  public CreateCourseEnrollmentsInput(String courseId)
  {
    this.courseId = Validate.notBlank(courseId, "Course id cannot be blank!");
  }

  public String getCourseId()
  {
    return courseId;
  }

  public Set<String> getLearnerIds()
  {
    return learnerIds;
  }

  public void addLearnerId(String id)
  {
    this.learnerIds.add(id);
  }
}

/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.lms.usecase.course_assessment.update_assessment;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.Validate;

/**
 * @author Oyungerel Chuluunsukh.
 */
public class UpdateAssessmentInput
{
  private String courseId;
  private List<String> courseTestIds = new ArrayList<>();

  public UpdateAssessmentInput(String courseId)
  {
    this.courseId = Validate.notBlank(courseId, "Course id cannot be blank!");
  }

  public String getCourseId()
  {
    return courseId;
  }

  public void setCourseId(String courseId)
  {
    this.courseId = courseId;
  }

  public List<String> getCourseTestIds()
  {
    return courseTestIds;
  }

  public void addCourseTestIds(String courseTestId)
  {
    this.courseTestIds.add(courseTestId);
  }
}

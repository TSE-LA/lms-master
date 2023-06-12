/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.lms.usecase.course_assessment.get_assessment;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Oyungerel Chuluunsukh.
 */
public class GetAssessmentOutput
{
  private String assessmentId;
  private String courseId;
  private List<String> courseTestIds = new ArrayList<>();

  public GetAssessmentOutput(String assessmentId, String courseId)
  {
    this.assessmentId = assessmentId;
    this.courseId = courseId;
  }

  public String getAssessmentId()
  {
    return assessmentId;
  }

  public void setAssessmentId(String assessmentId)
  {
    this.assessmentId = assessmentId;
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

  public void addCourseTestId(String courseTestId)
  {
    this.courseTestIds.add(courseTestId);
  }
}

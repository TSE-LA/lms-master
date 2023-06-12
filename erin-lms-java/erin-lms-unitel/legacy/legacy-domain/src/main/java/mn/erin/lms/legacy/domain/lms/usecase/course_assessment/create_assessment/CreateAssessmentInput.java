/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.lms.usecase.course_assessment.create_assessment;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.Validate;

/**
 * @author Oyungerel Chuluunsukh.
 */
public class CreateAssessmentInput
{
  private String courseId;
  private List<String> testIds = new ArrayList<>();
  public CreateAssessmentInput(String courseId){
    this.courseId = Validate.notBlank(courseId, "Course id cannot be null!");
  }

  public void addTestIds(String testId){
    testIds.add(testId);
  }

  public String getCourseId()
  {
    return courseId;
  }

  public void setCourseId(String courseId)
  {
    this.courseId = courseId;
  }
  public List<String> getTestIds(){
    return this.testIds;
  }
}

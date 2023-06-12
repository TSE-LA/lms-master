/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.lms.usecase.course_test.get_course_test;


/**
 * @author Oyungerel Chuluunsukh.
 */
public class GetCourseTestInput
{
  private String testId;

  public GetCourseTestInput(String testId)
  {
    this.testId = testId;
  }

  public String getTestId()
  {
    return testId;
  }

  public void setTestId(String testId)
  {
    this.testId = testId;
  }
}

/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.lms.usecase.course_test.delete_course_test;

/**
 * @author Oyungerel Chuluunsukh.
 */
public class DeleteCourseTestInput
{
  private String courseTestId;

  public DeleteCourseTestInput(String courseTestId)
  {
    this.courseTestId = courseTestId;
  }

  public String getCourseTestId()
  {
    return courseTestId;
  }

  public void setCourseTestId(String courseTestId)
  {
    this.courseTestId = courseTestId;
  }
}

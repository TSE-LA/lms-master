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
public class DeleteCourseTestOutput
{
  private final boolean deleted;

  public DeleteCourseTestOutput(boolean deleted)
  {
    this.deleted = deleted;
  }

  public boolean isDeleted()
  {
    return deleted;
  }
}

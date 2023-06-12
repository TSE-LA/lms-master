/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.lms.usecase.course.delete_course;

/**
 * @author Oyungerel Chuluunsukh.
 */
public class DeleteCourseOutput
{
  private final boolean deleted;

  public DeleteCourseOutput(boolean deleted)
  {
    this.deleted = deleted;
  }

  public boolean isDeleted()
  {
    return deleted;
  }
}

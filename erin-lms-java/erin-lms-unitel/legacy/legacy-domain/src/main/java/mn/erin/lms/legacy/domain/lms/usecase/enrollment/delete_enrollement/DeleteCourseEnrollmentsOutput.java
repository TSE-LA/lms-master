/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.lms.usecase.enrollment.delete_enrollement;

/**
 * @author Oyungerel Chuluunsukh.
 */
public class DeleteCourseEnrollmentsOutput
{
  private boolean deleted;

  public DeleteCourseEnrollmentsOutput(boolean deleted)
  {
    this.deleted = deleted;
  }

  public boolean isDeleted()
  {
    return deleted;
  }

  public void setDeleted(boolean deleted)
  {
    this.deleted = deleted;
  }
}

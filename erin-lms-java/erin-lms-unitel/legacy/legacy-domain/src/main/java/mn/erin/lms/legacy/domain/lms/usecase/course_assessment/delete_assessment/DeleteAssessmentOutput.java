/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.lms.usecase.course_assessment.delete_assessment;

/**
 * @author Oyungerel Chuluunsukh.
 */
public class DeleteAssessmentOutput
{
  private boolean deleted;

  public DeleteAssessmentOutput(boolean deleted)
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

/*
 * (C)opyright, 2018, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.lms.usecase.course.unpublish_course;

/**
 * author Tamir Batmagnai.
 */
public class HideCourseOutput
{
  private boolean isHidden;
  private String contentId;

  public HideCourseOutput(boolean isHidden, String contentId)
  {
    this.isHidden = isHidden;
    this.contentId = contentId;
  }

  public boolean isHidden()
  {
    return isHidden;
  }

  public void setHidden(boolean hidden)
  {
    this.isHidden = hidden;
  }

  public String getContentId()
  {
    return contentId;
  }

  public void setContentId(String contentId)
  {
    this.contentId = contentId;
  }
}

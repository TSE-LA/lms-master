/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.lms.usecase.update_course_enrollment;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Oyungerel Chuluunsukh.
 */
public class UpdateCourseEnrollmentOutput
{
  private Set<String> learnerIds = new HashSet<>();
  private String contentId;
  public UpdateCourseEnrollmentOutput()
  {
  }

  public UpdateCourseEnrollmentOutput(String contentId)
  {
    this.contentId = contentId;
  }


  public Set<String> getLearnerIds()
  {
    return learnerIds;
  }

  public void addLearnerIds(String learnerIds)
  {
    this.learnerIds.add(learnerIds);
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

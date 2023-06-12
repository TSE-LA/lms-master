/*
 * (C)opyright, 2018, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.infrastructure.lms.rest.course_content.rest_entities;

import java.util.List;

/**
 * author Tamir Batmagnai.
 */
public class RestFileSection
{
  private String attachFileId;
  private List<RestSection> restSections;

  public RestFileSection()
  {
  }

  public RestFileSection(String attachFileId, List<RestSection> restSections)
  {
    this.attachFileId = attachFileId;
    this.restSections = restSections;
  }

  public String getAttachFileId()
  {
    return attachFileId;
  }

  public List<RestSection> getRestSections()
  {
    return restSections;
  }

  public void setAttachFileId(String attachFileId)
  {
    this.attachFileId = attachFileId;
  }

  public void setRestSections(List<RestSection> restSections)
  {
    this.restSections = restSections;
  }
}

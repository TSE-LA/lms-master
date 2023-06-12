/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.infrastructure.scorm.rest;

import java.io.Serializable;
import java.util.List;

import org.springframework.lang.Nullable;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class RestContentAggregation implements Serializable
{
  private static final long serialVersionUID = 7801893664978104468L;

  private String scormContentName;
  private List<OrganizationEntry> organization;

  @Nullable
  private String wrapperType;

  public String getScormContentName()
  {
    return scormContentName;
  }

  public String getWrapperType()
  {
    return wrapperType;
  }

  public List<OrganizationEntry> getOrganization()
  {
    return organization;
  }
}

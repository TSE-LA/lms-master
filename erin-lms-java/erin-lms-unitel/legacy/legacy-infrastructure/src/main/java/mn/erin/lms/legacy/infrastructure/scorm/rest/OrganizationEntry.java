/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.infrastructure.scorm.rest;

import java.io.Serializable;
import java.util.List;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class OrganizationEntry implements Serializable
{
  private static final long serialVersionUID = -3609513917369646869L;

  private String title;
  private List<ResourceEntry> resources;

  public String getTitle()
  {
    return title;
  }

  public List<ResourceEntry> getResources()
  {
    return resources;
  }
}

/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.scorm.usecase.scorm;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.lang3.Validate;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class OrganizationInfo
{
  private final String title;
  private Set<ResourceInfo> resources = new LinkedHashSet<>();

  public OrganizationInfo(String title)
  {
    this.title = Validate.notBlank(title, "The title of the organizational info cannot be null or blank!");
  }

  public void addResourceInfo(ResourceInfo resourceInfo)
  {
    Validate.notNull(resourceInfo, "Resource info cannot be null!");
    this.resources.add(resourceInfo);
  }

  public String getTitle()
  {
    return title;
  }

  public Set<ResourceInfo> getResources()
  {
    return Collections.unmodifiableSet(resources);
  }
}

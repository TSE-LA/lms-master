/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.domain.scorm.usecase.scorm;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang3.Validate;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class CreateSCORMContentInput
{
  private final String scormContentName;
  private String wrapperType;

  private Set<OrganizationInfo> organizations = new LinkedHashSet<>();

  public CreateSCORMContentInput(String scormContentName)
  {
    this.scormContentName = Validate.notBlank(scormContentName, "SCORM content name cannot be null or blank!");
  }

  public void addOrganizationInfo(OrganizationInfo organizationInfo)
  {
    this.organizations.add(Objects.requireNonNull(organizationInfo, "Organization info cannot be null!"));
  }

  public void setWrapperType(String wrapperType)
  {
    this.wrapperType = wrapperType;
  }

  public String getScormContentName()
  {
    return scormContentName;
  }

  public String getWrapperType()
  {
    return wrapperType;
  }

  public Set<OrganizationInfo> getOrganizations()
  {
    return Collections.unmodifiableSet(organizations);
  }
}

package mn.erin.lms.base.domain.service.impl;

import mn.erin.lms.base.domain.service.OrganizationIdProvider;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class JarvisOrganizationIdProvider implements OrganizationIdProvider
{
  private static final String ID = "jarvis";

  @Override
  public String getOrganizationId()
  {
    return ID;
  }
}

package mn.erin.lms.unitel.domain.service;

import mn.erin.lms.base.domain.service.OrganizationIdProvider;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class UnitelOrganizationIdProvider implements OrganizationIdProvider
{
  private static final String ID = "unitel";

  @Override
  public String getOrganizationId()
  {
    return ID;
  }
}

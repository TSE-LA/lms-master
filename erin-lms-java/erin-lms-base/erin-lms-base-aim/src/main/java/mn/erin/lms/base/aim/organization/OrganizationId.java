package mn.erin.lms.base.aim.organization;

import mn.erin.domain.base.model.EntityId;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class OrganizationId extends EntityId
{
  private OrganizationId(String id)
  {
    super(id);
  }

  public static OrganizationId valueOf(String id)
  {
    return new OrganizationId(id);
  }
}

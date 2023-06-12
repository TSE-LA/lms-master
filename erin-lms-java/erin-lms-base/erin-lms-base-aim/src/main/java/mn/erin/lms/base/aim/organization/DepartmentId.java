package mn.erin.lms.base.aim.organization;

import mn.erin.domain.base.model.EntityId;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class DepartmentId extends EntityId
{
  private DepartmentId(String id)
  {
    super(id);
  }

  public static DepartmentId valueOf(String id)
  {
    return new DepartmentId(id);
  }
}

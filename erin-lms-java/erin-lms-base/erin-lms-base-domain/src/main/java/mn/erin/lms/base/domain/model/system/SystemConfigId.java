package mn.erin.lms.base.domain.model.system;

import mn.erin.domain.base.model.EntityId;

/**
 * @author Temuulen Naranbold
 */
public class SystemConfigId extends EntityId
{
  public SystemConfigId(String id)
  {
    super(id);
  }
  public static SystemConfigId valueOf(String id)
  {
    return new SystemConfigId(id);
  }
}

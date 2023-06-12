package mn.erin.lms.base.aim.permission;

import java.io.Serializable;
import java.util.Map;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class PermissionDto
{
  private final String id;
  private final Map<String, Serializable> properties;

  public PermissionDto(String id, Map<String, Serializable> properties)
  {
    this.id = id;
    this.properties = properties;
  }

  public String getId()
  {
    return id;
  }

  public Map<String, Serializable> getProperties()
  {
    return properties;
  }
}

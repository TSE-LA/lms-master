package mn.erin.domain.aim.model.permission;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.StringTokenizer;

import mn.erin.domain.base.model.ValueObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

/**
 * Presents a permission string which consists of application id, module id and action id.
 * A permission says that an action in the module of the application needs a permission to be executed
 *
 * @author EBazarragchaa
 */
public class Permission implements ValueObject<Permission>
{
  private static final String SEPARATOR = ".";

  private final String applicationId;
  private final String moduleId;
  private final String actionId;

  private Map<String, Serializable> properties;

  protected Permission(String applicationId, String moduleId, String actionId)
  {
    this.applicationId = Validate.notBlank(applicationId, "Application id is required!");
    this.moduleId = Validate.notBlank(moduleId, "Module id is required!");
    this.actionId = Validate.notBlank(actionId, "Action id is required!");
  }

  public String getApplicationId()
  {
    return applicationId;
  }

  public String getModuleId()
  {
    return moduleId;
  }

  public String getActionId()
  {
    return actionId;
  }

  public final String getPermissionString()
  {
    return applicationId + SEPARATOR + moduleId + SEPARATOR + actionId;
  }

  @Override
  public String toString()
  {
    return "Permission{" +
      "applicationId='" + applicationId + '\'' +
      ", moduleId='" + moduleId + '\'' +
      ", actionId='" + actionId + '\'' +
      '}';
  }

  @Override
  public boolean equals(Object o)
  {
    if (this == o)
    {
      return true;
    }
    if (o == null || getClass() != o.getClass())
    {
      return false;
    }
    Permission that = (Permission) o;
    return applicationId.equals(that.applicationId) &&
      moduleId.equals(that.moduleId) &&
      actionId.equals(that.actionId);
  }

  public static Permission valueOf(String applicationId, String moduleId, String actionId)
  {
    Validate.notBlank(applicationId, "Application id is required!");
    Validate.notBlank(moduleId, "Module id is required!");
    Validate.notBlank(actionId, "Action id is required!");
    return valueOf(applicationId + SEPARATOR + moduleId + SEPARATOR + actionId);
  }

  public static Permission valueOf(String permissionString)
  {
    if (StringUtils.isBlank(permissionString))
    {
      throw new IllegalArgumentException("Provided permission string cannot be blank");
    }
    StringTokenizer tokenizer = new StringTokenizer(permissionString, ".");
    if (tokenizer.countTokens() < 3)
    {
      throw new IllegalArgumentException("Provided permission string does not have valid length");
    }
    String applicationId = tokenizer.nextToken();
    String moduleId = tokenizer.nextToken();
    StringBuilder actionId = new StringBuilder(tokenizer.nextToken());
    while (tokenizer.hasMoreTokens())
    {
      actionId.append('.').append(tokenizer.nextToken());
    }
    return new Permission(applicationId, moduleId, actionId.toString());
  }

  @Override
  public int hashCode()
  {
    return Objects.hash(applicationId, moduleId, actionId);
  }

  @Override
  public boolean sameValueAs(Permission other)
  {
    return this.equals(other);
  }

  public Map<String, Serializable> getProperties()
  {
    return Collections.unmodifiableMap(properties);
  }

  public void setProperties(Map<String, Serializable> properties)
  {
    this.properties = properties;
  }
}

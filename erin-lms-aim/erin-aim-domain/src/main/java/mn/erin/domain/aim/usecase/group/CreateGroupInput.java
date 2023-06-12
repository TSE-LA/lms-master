package mn.erin.domain.aim.usecase.group;

/**
 * @author Zorig
 */
public class CreateGroupInput
{
  private final String parentId;
  private final String tenantId;
  private final String name;
  private final String description;

  public CreateGroupInput(String parentId, String tenantId, String name, String description)
  {
    this.parentId = parentId;
    this.tenantId = tenantId;
    this.name = name;
    this.description = description;
  }

  public String getParentId()
  {
    return parentId;
  }

  public String getTenantId()
  {
    return tenantId;
  }

  public String getName()
  {
    return name;
  }

  public String getDescription()
  {
    return description;
  }
}

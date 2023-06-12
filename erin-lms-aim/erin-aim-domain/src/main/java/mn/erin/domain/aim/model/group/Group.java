package mn.erin.domain.aim.model.group;

import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.base.model.Entity;
import mn.erin.domain.base.model.Node;

/**
 * @author Zorig
 */
public class Group extends Node<GroupId> implements Entity<Group>
{
  private final GroupId id;
  private final TenantId tenantId;
  private final String name;
  private String description;

  public Group(GroupId id, GroupId parentId, TenantId tenantId, String name)
  {
    super(id, parentId);
    this.id = id;
    this.tenantId = tenantId;
    this.name= name;
  }

  public GroupId getId()
  {
    return id;
  }

  public TenantId getTenantId()
  {
    return tenantId;
  }

  public String getDescription()
  {
    return description;
  }

  public String getName()
  {
    return name;
  }

  @Override
  public boolean sameIdentityAs(Group other)
  {
    return other != null && other.id.equals(this.id);
  }

  public void setDescription(String description)
  {
    this.description = description;
  }
}

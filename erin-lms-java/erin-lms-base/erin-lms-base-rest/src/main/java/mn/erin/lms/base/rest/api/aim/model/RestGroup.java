package mn.erin.lms.base.rest.api.aim.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import mn.erin.domain.aim.model.group.Group;
import mn.erin.domain.aim.model.group.GroupId;

/**
 * @author Zorig
 */
public class RestGroup
{
  private String id;
  private String parentId;
  private String tenantId;
  private String name;
  private String description;
  private int nthSibling;
  private List<String> children;


  public RestGroup()
  {
  }

  public static RestGroup of(Group group)
  {
    RestGroup restGroup = new RestGroup();

    String parentId = null;
    if (group.getParent() != null)
    {
      parentId = group.getParent().getId();
    }

    restGroup.setId(group.getId().getId());
    restGroup.setParentId(parentId);
    restGroup.setTenantId(group.getTenantId().getId());
    restGroup.setName(group.getName());
    restGroup.setDescription(group.getDescription());
    restGroup.setNthSibling(group.getNthSibling());

    Iterator<GroupId> childrenGroupIdsIterator = group.getChildren().iterator();
    List<String> childrenStringIds = new ArrayList<>();
    while (childrenGroupIdsIterator.hasNext())
    {
      childrenStringIds.add(childrenGroupIdsIterator.next().getId());
    }

    restGroup.setChildren(childrenStringIds);

    return restGroup;
  }

  public int getNthSibling()
  {
    return nthSibling;
  }

  public void setNthSibling(int nthSibling)
  {
    this.nthSibling = nthSibling;
  }

  public List<String> getChildren()
  {
    return children;
  }

  public void setChildren(List<String> children)
  {
    this.children = children;
  }

  public String getParentId()
  {
    return parentId;
  }

  public void setParentId(String parentId)
  {
    this.parentId = parentId;
  }

  public String getId()
  {
    return id;
  }

  public void setId(String id)
  {
    this.id = id;
  }

  public String getTenantId()
  {
    return tenantId;
  }

  public void setTenantId(String tenantId)
  {
    this.tenantId = tenantId;
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public String getDescription()
  {
    return description;
  }

  public void setDescription(String description)
  {
    this.description = description;
  }
}

package mn.erin.lms.base.domain.model.exam;

import java.util.ArrayList;
import java.util.List;

import mn.erin.lms.base.aim.organization.OrganizationId;

/**
 * @author Temuulen Naranbold
 */
public class ExamGroupTree
{
  private final String id;
  private final String parentId;
  private final String name;
  private final OrganizationId organizationId;
  List<ExamGroupTree> children = new ArrayList<>();

  public ExamGroupTree(String id, String parentId, String name, OrganizationId organizationId)
  {
    this.id = id;
    this.parentId = parentId;
    this.name = name;
    this.organizationId = organizationId;
  }

  public String getId()
  {
    return id;
  }

  public String getParentId()
  {
    return parentId;
  }

  public String getName()
  {
    return name;
  }

  public OrganizationId getOrganizationId()
  {
    return organizationId;
  }

  public List<ExamGroupTree> getChildren()
  {
    return children;
  }

  public void setChildren(List<ExamGroupTree> children)
  {
    this.children = children;
  }

  public boolean isRoot()
  {
    return parentId != null;
  }

  public void addChild(ExamGroupTree child)
  {
    this.children.add(child);
  }
}

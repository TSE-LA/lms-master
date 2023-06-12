package mn.erin.lms.base.domain.model.course;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.Validate;

/**
 * @author Temuulen Naranbold
 */
public class SelectedDepartmentTree
{
  private final String id;
  private String parent;
  private final String name;
  private List<SelectedDepartmentTree> children = new ArrayList<>();
  private boolean currentGroupSelected;
  private boolean someChildrenSelected;
  private boolean allChildrenSelected;

  public SelectedDepartmentTree(String id, String parent, String name)
  {
    this.id = Validate.notBlank(id, "Department ID cannot be null or blank!");
    this.parent = parent;
    this.name = Validate.notBlank(name, "Department name cannot be null or blank");
  }

  public String getId()
  {
    return id;
  }

  public String getParent()
  {
    return parent;
  }

  public String getName()
  {
    return name;
  }

  public List<SelectedDepartmentTree> getChildren()
  {
    return children;
  }

  public boolean isCurrentGroupSelected()
  {
    return currentGroupSelected;
  }

  public void setCurrentGroupSelected(boolean currentGroupSelected)
  {
    this.currentGroupSelected = currentGroupSelected;
  }

  public boolean isSomeChildrenSelected()
  {
    return someChildrenSelected;
  }

  public void setSomeChildrenSelected(boolean someChildrenSelected)
  {
    this.someChildrenSelected = someChildrenSelected;
  }

  public boolean isAllChildrenSelected()
  {
    return allChildrenSelected;
  }

  public void setAllChildrenSelected(boolean allChildrenSelected)
  {
    this.allChildrenSelected = allChildrenSelected;
  }

  public boolean isRoot()
  {
    return parent == null;
  }

  public void addChild(SelectedDepartmentTree child)
  {
    if (child != null && !children.contains(child))
    {
      children.add(child);
    }
  }
}

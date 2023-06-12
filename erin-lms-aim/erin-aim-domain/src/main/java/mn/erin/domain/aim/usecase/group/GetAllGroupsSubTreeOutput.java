package mn.erin.domain.aim.usecase.group;

import java.util.List;

import mn.erin.domain.aim.model.group.GroupTree;

/**
 * @author Zorig
 */
public class GetAllGroupsSubTreeOutput
{
  private final List<GroupTree> groupTreeList;

  public GetAllGroupsSubTreeOutput(List<GroupTree> groupTreeList)
  {
    this.groupTreeList = groupTreeList;
  }

  public List<GroupTree> getGroupTreeList()
  {
    return groupTreeList;
  }
}

package mn.erin.lms.base.domain.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import mn.erin.domain.aim.model.group.Group;
import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.lms.base.aim.organization.DepartmentPath;

/**
 * @author Galsan Bayart.
 */

public final class DepartmentPathUtil
{
  private DepartmentPathUtil()
  {
  }

  public static Map<String, DepartmentPath> getPath(String root, Set<Group> groups)
  {
    HashMap<String, DepartmentPath> groupIdPathMap = new HashMap<>();

    HashMap<String, Group> groupIdGroupMap = new HashMap<>();
    for (Group group : groups)
    {
      groupIdGroupMap.put(group.getId().getId(), group);
    }
    groupIdPathMap.put(root, new DepartmentPath(groupIdGroupMap.get(root).getName(), 0));
    DepartmentPathUtil.findChild(root, groupIdGroupMap.get(root).getName(), groupIdGroupMap, groupIdPathMap, 1);
    return groupIdPathMap;
  }

  private static void findChild(String root, String groupPath, HashMap<String, Group> groupIdGroupMap, HashMap<String, DepartmentPath> groupIdPathMap, int deep)
  {
    for (String group : groupIdGroupMap.get(root).getChildren().stream().map(GroupId::getId).collect(Collectors.toSet()))
    {
      groupIdPathMap.put(group,
          new DepartmentPath(groupIdGroupMap.get(group).getName() + "/" + groupPath, deep));
      findChild(group, groupIdGroupMap.get(group).getName() + "/" + groupPath, groupIdGroupMap,
          groupIdPathMap, deep + 1);
    }
  }
}


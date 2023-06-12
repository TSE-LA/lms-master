package mn.erin.lms.base.domain.usecase;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import mn.erin.domain.aim.model.group.Group;
import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.lms.base.aim.organization.DepartmentPath;
import mn.erin.lms.base.domain.util.DepartmentPathUtil;

/**
 * @author Galsan Bayart.
 */
public class DepartmentPathUtilTest
{
  private final Set<Group> groups = new HashSet<>();

  private final Group root = new Group(GroupId.valueOf("root"), null, null, "Root Name");
  private final Group subGroup1 = new Group(GroupId.valueOf("sub1"), GroupId.valueOf("root"), null, "Sub group 1");
  private final Group subGroup2 = new Group(GroupId.valueOf("sub2"), GroupId.valueOf("root"), null, "Sub group 2");

  private final Group subGroup11 = new Group(GroupId.valueOf("sub11"), GroupId.valueOf("sub1"), null, "Sub group 11");
  private final Group subGroup12 = new Group(GroupId.valueOf("sub12"), GroupId.valueOf("sub1"), null, "Sub group 12");

  private final Group subGroup21 = new Group(GroupId.valueOf("sub21"), GroupId.valueOf("sub2"), null, "Sub group 21");
  private final Group subGroup22 = new Group(GroupId.valueOf("sub22"), GroupId.valueOf("sub2"), null, "Sub group 22");

  @Before
  public void setUp()
  {
    root.addChild(subGroup1.getId());
    root.addChild(subGroup2.getId());
    subGroup1.addChild(subGroup11.getId());
    subGroup1.addChild(subGroup12.getId());
    subGroup2.addChild(subGroup21.getId());
    subGroup2.addChild(subGroup22.getId());

    groups.add(root);
    groups.add(subGroup1);
    groups.add(subGroup2);
    groups.add(subGroup11);
    groups.add(subGroup12);
    groups.add(subGroup21);
    groups.add(subGroup22);
  }

  @Test
  public void groupIdMapToPath_success()
  {
    Assert.assertTrue(isEqual(getExpected(), DepartmentPathUtil.getPath("root", groups)));
  }

  private HashMap<String, DepartmentPath> getExpected()
  {
    HashMap<String, DepartmentPath> expected = new HashMap<>();
    String groupPath = root.getName();
    expected.put(root.getId().getId(), new DepartmentPath(groupPath, 0));
    expected.put(subGroup1.getId().getId(), new DepartmentPath(subGroup1.getName() + "/" + groupPath, 1));
    expected.put(subGroup2.getId().getId(), new DepartmentPath(subGroup2.getName() + "/" + groupPath, 1));
    expected.put(subGroup11.getId().getId(), new DepartmentPath(subGroup11.getName() + "/" + subGroup1.getName() + "/" + groupPath, 2));
    expected.put(subGroup12.getId().getId(), new DepartmentPath(subGroup12.getName() + "/" + subGroup1.getName() + "/" + groupPath, 2));
    expected.put(subGroup21.getId().getId(), new DepartmentPath(subGroup21.getName() + "/" + subGroup2.getName() + "/" + groupPath, 2));
    expected.put(subGroup22.getId().getId(), new DepartmentPath(subGroup22.getName() + "/" + subGroup2.getName() + "/" + groupPath, 2));

    return expected;
  }

  private boolean isEqual(Map<String, DepartmentPath> expected, Map<String, DepartmentPath> useCaseResult)
  {
    if (expected.size() != useCaseResult.size())
    {
      return false;
    }
    for (Map.Entry<String, DepartmentPath> groupIdPath : expected.entrySet())
    {
      if (!useCaseResult.containsKey(groupIdPath.getKey()))
      {
        return false;
      }
      DepartmentPath departmentPath = useCaseResult.get(groupIdPath.getKey());
      if (!groupIdPath.getValue().getPath().equals(departmentPath.getPath()) || groupIdPath.getValue().getLevel() != departmentPath.getLevel())
      {
        return false;
      }
    }
    return true;
  }
}
package mn.erin.aim.repository.memory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;

import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.group.Group;
import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.aim.repository.GroupRepository;
import mn.erin.domain.base.model.EntityId;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.stereotype.Repository;

/**
 * author Naranbaatar Avir.
 */
@Repository
public class MemoryGroupRepository implements GroupRepository
{
  private final List<Group> groups = new ArrayList<>();

  public MemoryGroupRepository(TenantId tenantId)
  {
    try
    {
      Group group = new Group(GroupId.valueOf("123"), null, tenantId, "First group");
      group.setDescription("123");
      group.setNthSibling(0);

      groups.add(group);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  @Override
  public Group createGroup(String name, String parentId, String description, TenantId tenantId) throws AimRepositoryException
  {
    GroupId parentGroupId = null;
    if (!StringUtils.isBlank(parentId))
    {
      parentGroupId = GroupId.valueOf(parentId);

      //check if parent exists
      if (findById(new GroupId(parentId)) == null)
      {
        throw new AimRepositoryException("Parent Id does not exist!");
      }
    }
    String randomId = UUID.randomUUID().toString();

    int nthSibling = getNthSibling(parentId);

    //set child
    if (parentGroupId != null)
    {
      for (Group group : groups)
      {
        if (group.getId().equals(parentGroupId))
        {
          group.addChild(new GroupId(randomId));
        }
      }
    }

    Group group = new Group(GroupId.valueOf(randomId), parentGroupId, tenantId, name);
    group.setDescription(description);
    group.setNthSibling(nthSibling);
    groups.add(group);
    return group;
  }

  @Override
  public boolean doesGroupExist(GroupId id)
  {
    Validate.notNull(id, "Id being check for existence should not be null.");

    for (Group group : groups)
    {
      if (group.getId().equals(id))
      {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean doesGroupExistByName(String name)
  {
    Validate.notNull(name, "Name being check for existence should not be null.");

    for (Group group : groups)
    {
      if (name.equals(group.getName()))
      {
        return true;
      }
    }
    return false;
  }

  @Override
  public int getNthSibling(String parentId)
  {
    int siblingCount = 0;
    if (parentId != null)
    {
      for (Group group : groups)
      {
        if (group.getId().getId().equals(parentId))
        {
          return group.getChildren().size() + 1;
        }
      }
    }

    return siblingCount;
  }

  @Override
  public List<Group> getAllRootGroups(TenantId tenantId)
  {
    List<Group> groupsToReturn = new ArrayList<>();
    for (Group group : groups)
    {
      if (group.getTenantId().getId().equals(tenantId.getId()) && group.getParent() == null)
      {
        groupsToReturn.add(group);
      }
    }
    return groupsToReturn;
  }

  @Override
  public List<Group> getAllGroups(Set<String> groupIds)
  {
    List<Group> groupsToReturn = new ArrayList<>();
    for (Group group : groups)
    {
      if (groupIds.contains(group.getId().getId()))
      {
        groupsToReturn.add(group);
      }
    }
    return groupsToReturn;
  }

  @Override
  public Map<String, String> getAllGroupNamesAndId()
  {
    return new HashMap<>();
  }

  @Override
  public boolean deleteGroup(String groupId)
  {
    String parentId = null;

    Group group = findById(new GroupId(groupId));
    if (group.getParent() != null)
    {
      parentId = group.getParent().getId();
    }

    if (parentId != null)
    {
      deleteSiblingShift(groupId, parentId);
      removeChild(parentId, groupId);
    }

    Queue<String> groupsToTraverse = new PriorityQueue<>();
    //root
    groupsToTraverse.add(groupId);

    Iterator<GroupId> children;

    String currentPeak = groupsToTraverse.peek();

    while (!groupsToTraverse.isEmpty())
    {
      children = findById(new GroupId(currentPeak)).getChildren().iterator();

      while (children.hasNext())
      {
        GroupId nextChild = children.next();
        groupsToTraverse.add(nextChild.getId());
      }

      //delete and check if deletion is successful
      Group currentGroupToDelete = findById(new GroupId(currentPeak));
      groups.remove(currentGroupToDelete);

      groupsToTraverse.remove(currentPeak);
      currentPeak = groupsToTraverse.peek();
    }

    //do a check if delete was successful and set return accordingly
    return true;
  }

  @Override
  public Set<GroupId> getAllSubGroups(String number)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Group moveGroupParent(String groupId, String parentId)
  {
    return null;
  }

  @Override
  public Group moveGroupSibling(String groupId, int siblingNumber)
  {
    return null;
  }

  @Override
  public Group renameGroup(String groupId, String newName)
  {
    Group oldGroup = findById(new GroupId(groupId));
    Group changedGroup = new Group(oldGroup.getId(), oldGroup.getParent(), oldGroup.getTenantId(), newName);
    changedGroup.setDescription(oldGroup.getDescription());
    changedGroup.setNthSibling(oldGroup.getNthSibling());
    return changedGroup;
  }

  @Override
  public Set<String> getParentGroupIds(String groupId)
  {
    Group group = findById(new GroupId(groupId));
    Set<String> parentIds = new HashSet<>();
    getParentGroupIds(group, parentIds);
    return parentIds;
  }

  @Override
  public Set<String> getSubGroups(String groupId)
  {
    Set<String> subGroups = new HashSet<>();
    for (Group group : groups)
    {
      if (group.getParent().equals(groupId))
      {
        subGroups.add(group.getId().getId());
      }
    }
    return subGroups;
  }

  private void getParentGroupIds(Group child, Set<String> parentIds)
  {
    Group parent = findById(child.getParent());
    if (parent == null)
    {
      return;
    }
    parentIds.add(parent.getId().getId());
    getParentGroupIds(parent, parentIds);
  }

  @Override
  public Group findByNumberAndTenantId(String number, TenantId tenantId)
  {
    for (Group group : groups)
    {
      if (group.getDescription().equals(number) && group.getTenantId().sameValueAs(tenantId))
      {
        return group;
      }
    }

    return null;
  }

  @Override
  public Group findByName(String name)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Group findById(EntityId entityId)
  {
    for (Group group : groups)
    {
      if (group.getId().sameValueAs(entityId))
      {
        return group;
      }
    }

    return null;
  }

  @Override
  public Collection<Group> findAll()
  {
    return Collections.unmodifiableList(groups);
  }

  private boolean removeChild(String parentId, String childId)
  {
    Group parentGroup = findById(new GroupId(parentId));
    parentGroup.removeChild(new GroupId(childId));

    return true;
  }

  private boolean deleteSiblingShift(String groupId, String parentId)
  {
    int index = findById(new GroupId(groupId)).getNthSibling() - 1;

    List<GroupId> siblings = findById(new GroupId(parentId)).getChildren();

    List<GroupId> siblingsToShiftList = new ArrayList<>();

    for (int i = index; i <= siblings.size() - 1; i++)
    {
      siblingsToShiftList.add(siblings.get(i));
    }

    Iterator<GroupId> siblingsToShiftIterator = siblingsToShiftList.iterator();

    while (siblingsToShiftIterator.hasNext())
    {
      Group groupToShift = findById(siblingsToShiftIterator.next());
      groupToShift.setNthSibling(groupToShift.getNthSibling() - 1);
    }

    //add checker for successful change, set return accordingly
    return true;
  }

  @Override
  public Set<Group> getAllByIds(Set<String> groupIds)
  {
    return null;
  }
}

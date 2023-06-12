package mn.erin.domain.aim.usecase.group;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Queue;

import mn.erin.domain.aim.model.group.Group;
import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.domain.aim.model.group.GroupTree;
import mn.erin.domain.aim.model.permission.AimModulePermission;
import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.repository.GroupRepository;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.aim.usecase.AuthorizedUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import org.apache.commons.lang3.Validate;

/**
 * @author Zorig
 */
public class GetGroupSubTree extends AuthorizedUseCase<GetGroupSubTreeInput, GetGroupSubTreeOutput>
{
  private static final Permission permission = new AimModulePermission("GetGroupSubTree");

  private final GroupRepository groupRepository;

  public GetGroupSubTree(
    AuthenticationService authenticationService,
    AuthorizationService authorizationService,
    GroupRepository groupRepository)
  {
    super(authenticationService, authorizationService);
    this.groupRepository = Objects.requireNonNull(groupRepository, "GroupRepository cannot be null!");
  }

  @Override
  public Permission getPermission()
  {
    return permission;
  }

  @Override
  protected GetGroupSubTreeOutput executeImpl(GetGroupSubTreeInput input) throws UseCaseException
  {
    String groupId = Validate.notNull(input.getGroupId());
    if (!groupRepository.doesGroupExist(GroupId.valueOf(groupId)))
    {
      throw new UseCaseException("Group does not exist!");
    }

    GroupTree startingNode = getGroupSubTree(groupId);
    return new GetGroupSubTreeOutput(startingNode);
  }

  private GroupTree getGroupSubTree(String groupId)
  {
    //queue of id strings
    Queue<String> groupsToTraverse = new PriorityQueue<>();
    //hashmap used to keep track/link parent to children
    Map<String, GroupTree> map = new HashMap<>();

    groupsToTraverse.add(groupId);
    String currentPeak;
    while (!groupsToTraverse.isEmpty())
    {
      currentPeak = groupsToTraverse.peek();
      Group mappedGroup = groupRepository.findById(GroupId.valueOf(currentPeak));

      String parentId = null;
      if (mappedGroup.getParent() != null)
      {
        parentId = mappedGroup.getParent().getId();
      }

      GroupTree currentGroup = new GroupTree(mappedGroup.getId().getId(), parentId, mappedGroup.getTenantId().getId(), mappedGroup.getName());
      currentGroup.setDescription(mappedGroup.getDescription());
      currentGroup.setNthSibling(mappedGroup.getNthSibling());

      //load queue for other iterations
      for (GroupId id : mappedGroup.getChildren())
      {
        String nextChild = id.getId();
        groupsToTraverse.add(nextChild);
      }

      // set/connect child(s) with parent
      if (currentGroup.getParent() != null && !currentGroup.getId().equals(groupId))//starting group has no parent, so no need to set
      {
        GroupTree parentObject = map.get(currentGroup.getParent());
        parentObject.addChild(currentGroup);
      }

      map.put(currentPeak, currentGroup);
      //remove peek
      groupsToTraverse.remove(currentPeak);
    }

    return map.get(groupId);
  }
}



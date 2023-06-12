package mn.erin.lms.base.rest.api.aim;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;

import io.swagger.annotations.Api;
import org.apache.commons.lang3.Validate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import mn.erin.domain.aim.model.group.Group;
import mn.erin.domain.aim.model.group.GroupTree;
import mn.erin.domain.aim.repository.AimRepositoryRegistry;
import mn.erin.domain.aim.repository.GroupRepository;
import mn.erin.domain.aim.repository.MembershipRepository;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.aim.service.TenantIdProvider;
import mn.erin.domain.aim.usecase.group.CreateGroup;
import mn.erin.domain.aim.usecase.group.CreateGroupInput;
import mn.erin.domain.aim.usecase.group.CreateGroupOutput;
import mn.erin.domain.aim.usecase.group.DeleteGroup;
import mn.erin.domain.aim.usecase.group.DeleteGroupInput;
import mn.erin.domain.aim.usecase.group.GetAllGroups;
import mn.erin.domain.aim.usecase.group.GetGroupSubTree;
import mn.erin.domain.aim.usecase.group.GetGroupSubTreeInput;
import mn.erin.domain.aim.usecase.group.GetGroupSubTreeOutput;
import mn.erin.domain.aim.usecase.group.RenameGroup;
import mn.erin.domain.aim.usecase.group.RenameGroupInput;
import mn.erin.domain.aim.usecase.group.RenameGroupOutput;
import mn.erin.domain.aim.usecase.group.UpdateGroupParent;
import mn.erin.domain.aim.usecase.group.UpdateGroupParentInput;
import mn.erin.domain.aim.usecase.membership.DeleteMembership;
import mn.erin.domain.aim.usecase.membership.GetGroupMemberships;
import mn.erin.domain.aim.usecase.membership.GetMembershipOutput;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.infrastucture.rest.common.response.RestEntity;
import mn.erin.infrastucture.rest.common.response.RestResponse;
import mn.erin.lms.base.aim.AccessIdentityManagement;
import mn.erin.lms.base.aim.role.LmsRole;
import mn.erin.lms.base.aim.user.Learner;
import mn.erin.lms.base.aim.user.LmsUser;
import mn.erin.lms.base.aim.user.Manager;
import mn.erin.lms.base.aim.user.Supervisor;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.enrollment.AssignCoursesToGroup;
import mn.erin.lms.base.domain.usecase.enrollment.dto.AssignCoursesToGroupInput;
import mn.erin.lms.base.rest.api.aim.model.RestGroup;
import mn.erin.lms.base.rest.api.aim.model.RestUpdateGroupParent;

/**
 * @author Zorig
 */

@Api("Group")
@RequestMapping(value = "/aim")
@RestController
public class GroupRestApi
{
  private final LmsRepositoryRegistry lmsRepositoryRegistry;
  private final LmsServiceRegistry lmsServiceRegistry;
  private final AuthenticationService authenticationService;
  private final AuthorizationService authorizationService;
  private final TenantIdProvider tenantIdProvider;
  private final AimRepositoryRegistry aimRepositoryRegistry;

  @Inject
  private AccessIdentityManagement accessIdentityManagement;

  @Inject
  private MembershipRepository membershipRepository;

  @Inject
  private GroupRepository groupRepository;

  public GroupRestApi(LmsRepositoryRegistry lmsRepositoryRegistry, LmsServiceRegistry lmsServiceRegistry,
    AuthenticationService authenticationService, AuthorizationService authorizationService,
    TenantIdProvider tenantIdProvider, AimRepositoryRegistry aimRepositoryRegistry)
  {
    this.lmsRepositoryRegistry = lmsRepositoryRegistry;
    this.lmsServiceRegistry = lmsServiceRegistry;
    this.authenticationService = authenticationService;
    this.authorizationService = authorizationService;
    this.tenantIdProvider = tenantIdProvider;
    this.aimRepositoryRegistry = aimRepositoryRegistry;
  }

  @RequestMapping(value = "/group", method = RequestMethod.POST)
  public ResponseEntity<RestEntity> createGroup(@RequestBody RestGroup restGroup)
  {
    try
    {
      CreateGroupInput input = new CreateGroupInput(restGroup.getParentId(), tenantIdProvider.getCurrentUserTenantId(), restGroup.getName(), restGroup.getDescription());
      CreateGroup createGroup = new CreateGroup(authenticationService, authorizationService, aimRepositoryRegistry.getGroupRepository());
      CreateGroupOutput output = createGroup.execute(input);

      AssignCoursesToGroup assignCoursesToGroup = new AssignCoursesToGroup(lmsRepositoryRegistry, lmsServiceRegistry, groupRepository);
      assignCoursesToGroup.execute(new AssignCoursesToGroupInput(output.getId(), restGroup.getParentId()));

      return ResponseEntity.status(HttpStatus.CREATED).body(RestEntity.of(output));
    }
    catch (UseCaseException e)
    {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(RestEntity.of(e.getMessage()));
    }
  }

  @RequestMapping(value = "/group/update-parent", method = RequestMethod.PUT)
  public ResponseEntity<RestEntity> updateGroupParent(@RequestBody RestUpdateGroupParent restUpdateGroupParent)
  {
    try
    {
      Validate.notNull(restUpdateGroupParent);
      Validate.notBlank(restUpdateGroupParent.getId());
      Validate.notBlank(restUpdateGroupParent.getParentId());
      UpdateGroupParentInput input = new UpdateGroupParentInput(restUpdateGroupParent.getId(), restUpdateGroupParent.getParentId());
      UpdateGroupParent updateGroupParent = new UpdateGroupParent(authenticationService, authorizationService, groupRepository);
      return ResponseEntity.status(HttpStatus.OK).body(RestEntity.of(updateGroupParent.execute(input)));
    }
    catch (UseCaseException | IllegalArgumentException | NullPointerException e)
    {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(RestEntity.of(e.getMessage()));
    }
  }

  @RequestMapping(value = "/group/{groupId}", method = RequestMethod.DELETE)
  public ResponseEntity<RestEntity> deleteGroup(@PathVariable String groupId)
  {
    String role = accessIdentityManagement.getRole(accessIdentityManagement.getCurrentUsername());

    if ("LMS_SUPERVISOR".equals(role) || "LMS_MANAGER".equals(role))
    {
      return RestResponse.unauthorized("Supervisor is not allowed to delete a group");
    }

    try
    {
      GetGroupSubTreeInput getGroupSubTreeInput = new GetGroupSubTreeInput(groupId);
      GetGroupSubTree getGroupSubTree = newGetGroupSubTree();
      GroupTree groupTree = getGroupSubTree.execute(getGroupSubTreeInput).getGroupTree();

      DeleteGroupInput input = new DeleteGroupInput(groupId);
      DeleteGroup deleteGroup = new DeleteGroup(authenticationService, authorizationService, aimRepositoryRegistry.getGroupRepository());
      boolean isDeleted = deleteGroup.execute(input).isDeleted();

      Set<String> deletedMemberships = new HashSet<>();

      if (isDeleted)
      {
        deleteMemberships(groupTree, deletedMemberships);
      }
      else
      {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(RestEntity.of("Failed to delete the group"));
      }

      return ResponseEntity.ok(RestEntity.of(deletedMemberships));
    }
    catch (UseCaseException e)
    {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(RestEntity.of(e.getMessage()));
    }
  }

  @RequestMapping(value = "/group/{groupId}", method = RequestMethod.GET)
  public ResponseEntity<RestEntity> getGroup(@PathVariable String groupId)
  {
    try
    {
      GetGroupSubTreeInput input = new GetGroupSubTreeInput(groupId);
      GetGroupSubTreeOutput output = newGetGroupSubTree().execute(input);

      return ResponseEntity.ok(RestEntity.of(output.getGroupTree()));
    }
    catch (UseCaseException e)
    {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(RestEntity.of(e.getMessage()));
    }
  }

  @RequestMapping(value = "/group", method = RequestMethod.GET)
  public ResponseEntity<RestEntity> getAllGroups() throws UseCaseException
  {
    GetAllGroups getAllGroups = new GetAllGroups(authenticationService, authorizationService, aimRepositoryRegistry.getGroupRepository(), tenantIdProvider);
    List<Group> output = getAllGroups.execute(null);
    return ResponseEntity.ok(RestEntity.of(output));
  }

  @RequestMapping(value = "/group/{groupId}/rename", method = RequestMethod.PATCH)
  public ResponseEntity<RestEntity> renameGroup(@PathVariable String groupId, @RequestBody RestGroup restGroup)
  {
    String name = restGroup.getName();

    try
    {
      RenameGroupInput input = new RenameGroupInput(groupId, name);
      RenameGroup renameGroup = new RenameGroup(authenticationService, authorizationService, aimRepositoryRegistry.getGroupRepository());
      RenameGroupOutput output = renameGroup.execute(input);

      RestGroup restGroupToReturn = RestGroup.of(output.getRenamedGroup());
      return ResponseEntity.ok(RestEntity.of(restGroupToReturn));
    }
    catch (UseCaseException e)
    {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(RestEntity.of(e.getMessage()));
    }
  }

  @RequestMapping(value = "/group/{groupId}/memberships", method = RequestMethod.GET)
  public ResponseEntity<RestEntity> getMemberships(@PathVariable String groupId)
  {
    Validate.notBlank(groupId);
    LmsUser currentUser = lmsServiceRegistry.getLmsUserService().getCurrentUser();
    GetGroupMemberships getGroupMemberships = new GetGroupMemberships(membershipRepository, tenantIdProvider);
    try
    {
      List<GetMembershipOutput> memberships = getGroupMemberships.execute(groupId);
      memberships.removeIf(membership -> membership.getUserId().equals(currentUser.getId().getId()));

      if (currentUser instanceof Manager)
      {
        memberships.removeIf(membership -> membership.getRoleId().equals(LmsRole.LMS_ADMIN.name()));
        memberships.removeIf(membership -> membership.getRoleId().equals(LmsRole.LMS_MANAGER.name()));
      }

      if (currentUser instanceof Supervisor)
      {
        memberships.removeIf(membership -> membership.getRoleId().equals(LmsRole.LMS_ADMIN.name()));
        memberships.removeIf(membership -> membership.getRoleId().equals(LmsRole.LMS_MANAGER.name()));
        memberships.removeIf(membership -> membership.getRoleId().equals(LmsRole.LMS_SUPERVISOR.name()));
      }

      if (currentUser instanceof Learner)
      {
        return ResponseEntity.ok(RestEntity.of(Collections.emptyList()));
      }

      return ResponseEntity.ok(RestEntity.of(memberships));
    }
    catch (UseCaseException e)
    {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(RestEntity.of(e.getMessage()));
    }
  }


  private GetGroupSubTree newGetGroupSubTree()
  {
    return new GetGroupSubTree(authenticationService, authorizationService, aimRepositoryRegistry.getGroupRepository());
  }

  private void deleteMemberships(GroupTree groupTree, Set<String> deletedMemberships) throws UseCaseException
  {
    if (groupTree == null)
    {
      return;
    }

    GetGroupMemberships getGroupMemberships = new GetGroupMemberships(membershipRepository, tenantIdProvider);
    List<GetMembershipOutput> memberships = getGroupMemberships.execute(groupTree.getId());
    DeleteMembership deleteMembership = new DeleteMembership(authenticationService, authorizationService, aimRepositoryRegistry.getMembershipRepository());
    for (GetMembershipOutput membership : memberships)
    {
      boolean isDeleted = deleteMembership.execute(membership.getMembershipId());
      if (isDeleted)
      {
        deletedMemberships.add(membership.getMembershipId());
      }
    }

    deleteMembershipsRecursively(groupTree.getChildren(), deletedMemberships);
  }

  private void deleteMembershipsRecursively(List<GroupTree> groupTrees, Set<String> deletedMemberships) throws UseCaseException
  {
    if (groupTrees.isEmpty())
    {
      return;
    }

    GetGroupMemberships getGroupMemberships = new GetGroupMemberships(membershipRepository, tenantIdProvider);
    for (GroupTree groupTree : groupTrees)
    {
      List<GetMembershipOutput> memberships = getGroupMemberships.execute(groupTree.getId());
      DeleteMembership deleteMembership = new DeleteMembership(authenticationService, authorizationService, aimRepositoryRegistry.getMembershipRepository());
      for (GetMembershipOutput membership : memberships)
      {
        boolean isDeleted = deleteMembership.execute(membership.getMembershipId());
        if (isDeleted)
        {
          deletedMemberships.add(membership.getMembershipId());
        }
      }

      deleteMembershipsRecursively(groupTree.getChildren(), deletedMemberships);
    }
  }
}

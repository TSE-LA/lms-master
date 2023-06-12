package mn.erin.lms.base.domain.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.group.Group;
import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.domain.aim.model.membership.Membership;
import mn.erin.domain.aim.model.role.RoleId;
import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.aim.model.user.User;
import mn.erin.domain.aim.model.user.UserAggregate;
import mn.erin.domain.aim.model.user.UserContact;
import mn.erin.domain.aim.model.user.UserId;
import mn.erin.domain.aim.model.user.UserIdentity;
import mn.erin.domain.aim.model.user.UserIdentitySource;
import mn.erin.domain.aim.model.user.UserInfo;
import mn.erin.domain.aim.model.user.UserProfile;
import mn.erin.domain.aim.model.user.UserStatus;
import mn.erin.domain.aim.repository.AimRepositoryRegistry;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.TenantIdProvider;
import mn.erin.domain.base.model.EntityId;
import mn.erin.domain.base.model.person.ContactInfo;
import mn.erin.lms.base.aim.AccessIdentityManagement;
import mn.erin.lms.base.aim.role.LmsRole;

/**
 * Responsible for bridging LMS with AIM.
 * UserID to username mapping takes place here as well.
 *
 * @author Bat-Erdene Tsogoo.
 */
public class AccessIdentityManagementImpl implements AccessIdentityManagement
{
  private static final Logger LOG = LoggerFactory.getLogger(AccessIdentityManagementImpl.class);

  private final AuthenticationService authenticationService;
  private final AimRepositoryRegistry aimRepositoryRegistry;
  private final TenantIdProvider tenantIdProvider;

  public AccessIdentityManagementImpl(AuthenticationService authenticationService, AimRepositoryRegistry aimRepositoryRegistry,
      TenantIdProvider tenantIdProvider)
  {
    this.authenticationService = authenticationService;
    this.aimRepositoryRegistry = aimRepositoryRegistry;
    this.tenantIdProvider = Objects.requireNonNull(tenantIdProvider, "TenantIdProvider cannot be null");
  }

  @Override
  public String getCurrentUserId()
  {
    UserId currentUserId = getUserId(getCurrentUsername());
    return currentUserId != null ? currentUserId.getId() : null;
  }

  @Override
  public String getCurrentUsername()
  {
    return authenticationService.getCurrentUsername();
  }

  @Override
  public String getRole(String username)
  {
    if (username == null)
    {
      return null;
    }
    try
    {
      Membership membership = aimRepositoryRegistry.getMembershipRepository().findByUsername(username);
      if (membership != null)
      {
        return membership.getRoleId().getId();
      }
    }
    catch (AimRepositoryException e)
    {
      LOG.error("Error getting user role: [{}]", username, e);
    }
    return null;
  }

  @Override
  public String getUserDepartmentId(String username)
  {
    try
    {
      Membership membership = aimRepositoryRegistry.getMembershipRepository().findByUsername(username);
      if (membership != null)
      {
        return membership.getGroupId().getId();
      }
    }
    catch (AimRepositoryException e)
    {
      LOG.error("Error getting user department: [{}]", username, e);
    }
    return null;
  }

  @Override
  public String getCurrentUserDepartmentId()
  {
    return getUserDepartmentId(getCurrentUsername());
  }

  @Override
  public String getDepartmentId(String departmentName)
  {
    try
    {
      Group group = aimRepositoryRegistry.getGroupRepository().findByName(departmentName);
      if (group != null)
      {
        return aimRepositoryRegistry.getGroupRepository().findByName(departmentName).getId().getId();
      }
      else
      {
        return null;
      }
    }
    catch (AimRepositoryException | NullPointerException e)
    {
      LOG.error("Error getting department ID [{}]", departmentName, e);
    }
    return null;
  }

  @Override
  public Set<String> getSubDepartments(String departmentId)
  {
    Group group = aimRepositoryRegistry.getGroupRepository().findById(GroupId.valueOf(departmentId));
    Set<String> departments = new HashSet<>();
    departments.add(group.getId().getId());
    recursivelyAddGroup(group.getChildren(), departments);
    return departments;
  }

  @Override
  public Group getDepartment(String departmentId)
  {
    return aimRepositoryRegistry.getGroupRepository().findById(GroupId.valueOf(departmentId));
  }

  @Override
  public List<Group> getDepartments(Set<String> departments)
  {
    try
    {
      return aimRepositoryRegistry.getGroupRepository().getAllGroups(departments);
    }
    catch (AimRepositoryException e)
    {
      return Collections.emptyList();
    }
  }

  @Override
  public Set<String> getAncestorDepartments(String departmentId)
  {
    Group group = aimRepositoryRegistry.getGroupRepository().findById(GroupId.valueOf(departmentId));
    Set<String> departments = new HashSet<>();
    departments.add(group.getId().getId());
    recursivelyAddParentGroup(group, departments);
    return departments;
  }

  /**
   * Does some thing in old style.
   *
   * @deprecated use getAncestorDepartments instead.
   */
  @Deprecated /*use getAncestorDepartments instead*/
  @Override
  public Set<String> getParentDepartments(String departmentId)
  {
    return aimRepositoryRegistry.getGroupRepository().getParentGroupIds(departmentId);
  }

  @Override
  public String getDepartmentName(String departmentId)
  {
    try
    {
      Group group = aimRepositoryRegistry.getGroupRepository().findById(GroupId.valueOf(departmentId));
      return group.getName();
    }
    catch (NullPointerException e)
    {
      return null;
    }
  }

  @Override
  public Map<String, String> getDepartmentNamesAndId()
  {
    return aimRepositoryRegistry.getGroupRepository().getAllGroupNamesAndId();
  }

  @Override
  public Set<String> getInstructors(String departmentId)
  {
    try
    {
      List<Membership> memberships = aimRepositoryRegistry.getMembershipRepository().listAllByGroupId(tenantId(), GroupId.valueOf(departmentId));
      Set<String> instructors = new HashSet<>();
      for (Membership membership : memberships)
      {
        RoleId roleId = membership.getRoleId();
        if (LmsRole.LMS_ADMIN.name().equals(roleId.getId()))
        {
          instructors.add(membership.getUsername());
        }
      }
      return instructors;
    }
    catch (AimRepositoryException e)
    {
      LOG.error("Error getting department [{}] instructors", departmentId, e);
    }
    return Collections.emptySet();
  }

  @Override
  public Set<String> getLearners(String departmentId)
  {
    try
    {
      List<Membership> memberships = aimRepositoryRegistry.getMembershipRepository().listAllByGroupId(tenantId(), GroupId.valueOf(departmentId));
      Set<String> existingLearners = getExistingLearners();
      Set<String> learners = new HashSet<>();
      for (Membership membership : memberships)
      {
        RoleId roleId = membership.getRoleId();
        if (existingLearners.contains(membership.getUsername()) && !LmsRole.LMS_ADMIN.name().equals(roleId.getId()))
        {
          learners.add(membership.getUsername());
        }
      }
      return learners;
    }
    catch (AimRepositoryException e)
    {
      LOG.error("Error getting department [{}] learners", departmentId, e);
    }
    return Collections.emptySet();
  }

  @Override
  public Set<String> getAllLearners(String departmentId)
  {
    Group group = aimRepositoryRegistry.getGroupRepository().findById(GroupId.valueOf(departmentId));
    Set<String> departments = new HashSet<>();
    departments.add(group.getId().getId());
    recursivelyAddGroup(group.getChildren(), departments);
    List<Membership> memberships = new ArrayList<>();
    try
    {
      for (String department : departments)
      {
        memberships.addAll(aimRepositoryRegistry.getMembershipRepository().listAllByGroupId(tenantId(), GroupId.valueOf(department)));
      }
      Set<String> learners = new HashSet<>();
      Set<String> existingLearners = getExistingLearners();
      for (Membership membership : memberships)
      {
        RoleId roleId = membership.getRoleId();
        if (existingLearners.contains(membership.getUsername()) && !LmsRole.LMS_ADMIN.name().equals(roleId.getId()))
        {
          learners.add(membership.getUsername());
        }
      }
      return learners;
    }
    catch (AimRepositoryException e)
    {
      LOG.error("Error getting department [{}] learners", departmentId, e);
    }
    return Collections.emptySet();
  }

  @Override
  public Set<String> getLearnersByRole(String departmentId, String role)
  {
    try
    {
      Set<String> groups = getSubDepartments(departmentId);
      return getLearners(role, groups);
    }
    catch (AimRepositoryException e)
    {
      LOG.error("Error getting learners for role [{}] for department [{}]", departmentId, role, e);
    }
    return Collections.emptySet();
  }

  @Override
  public List<UserAggregate> getUserAggregatesByUsername(Set<String> usernames)
  {
    Set<String> userIds = usernames.stream().map(this::getUserId).filter(Objects::nonNull).map(EntityId::getId).collect(Collectors.toSet());
    return userIds.stream().map(userId -> aimRepositoryRegistry.getUserAggregateService().getUserAggregate(userId))
        .collect(Collectors.toList());
  }

  @Override
  public Set<String> getParentGroupLearnersByRole(String departmentId, String role)
  {
    try
    {
      Set<String> groups = getAncestorDepartments(departmentId);
      return getLearners(role, groups);
    }
    catch (AimRepositoryException e)
    {
      LOG.error("Error getting learners for role [{}] for department [{}]", departmentId, role, e);
    }
    return Collections.emptySet();
  }

  @Override
  public ContactInfo getContactInfo(String username)
  {
    UserId userId = getUserId(username);
    UserProfile profile = null;
    if (userId != null)
    {
      profile = aimRepositoryRegistry.getUserProfileRepository().findByUserId(userId);
    }
    if (profile == null)
    {
      return null;
    }
    UserContact userContact = profile.getUserContact();
    return new ContactInfo(userContact.getPhoneNumber(), userContact.getEmail());
  }

  @Override
  public UserInfo getUserInfo(String username)
  {
    UserId userId = getUserId(username);
    UserProfile profile = null;
    if (userId != null)
    {
      profile = aimRepositoryRegistry.getUserProfileRepository().findByUserId(userId);
    }
    if (profile != null && profile.getUserInfo() != null)
    {
      return profile.getUserInfo();
    }
    return null;
  }

  @Override
  public boolean isArchived(String username)
  {
    UserId userid = getUserId(username);
    if (userid != null)
    {
      User user = aimRepositoryRegistry.getUserRepository().findById(userid);
      return user != null && user.getStatus() == UserStatus.ARCHIVED;
    }
    return true;
  }

  private UserId getUserId(String username)
  {
    UserIdentity identity = aimRepositoryRegistry.getUserIdentityRepository().getUserIdentityByUsername(username);
    if (identity == null)
    {
      return null;
    }
    return identity.getUserId();
  }

  private void recursivelyAddGroup(List<GroupId> nodes, Set<String> groups)
  {
    if (nodes.isEmpty())
    {
      return;
    }

    for (GroupId node : nodes)
    {
      Group group = aimRepositoryRegistry.getGroupRepository().findById(GroupId.valueOf(node.getId()));
      groups.add(group.getId().getId());
      recursivelyAddGroup(group.getChildren(), groups);
    }
  }

  private void recursivelyAddParentGroup(Group group, Set<String> groups)
  {
    if (group.getParent() == null)
    {
      return;
    }
    Group parentGroup = aimRepositoryRegistry.getGroupRepository().findById(group.getParent());
    groups.add(parentGroup.getId().getId());
    recursivelyAddParentGroup(parentGroup, groups);
  }

  private TenantId tenantId()
  {
    String tenantId = tenantIdProvider.getCurrentUserTenantId();
    // todo: fix me after tenant update.
    return TenantId.valueOf(tenantId != null ? tenantId : "dummyTenantId");
  }

  private Set<String> getExistingLearners()
  {
    Set<String> existingLearners = aimRepositoryRegistry.getUserIdentityRepository().getAllBySource(UserIdentitySource.OWN).stream()
        .map(UserIdentity::getUsername).collect(Collectors.toSet());
    if (existingLearners.isEmpty())
    {
      existingLearners = aimRepositoryRegistry.getUserIdentityRepository().getAllBySource(UserIdentitySource.LDAP).stream()
          .map(UserIdentity::getUsername).collect(Collectors.toSet());
    }
    return existingLearners;
  }

  @NotNull
  private Set<String> getLearners(String role, Set<String> groups) throws AimRepositoryException
  {
    Set<String> learners = new HashSet<>();
    for (String group : groups)
    {
      List<Membership> memberships = aimRepositoryRegistry.getMembershipRepository().listAllByGroupId(tenantId(), GroupId.valueOf(group));
      for (Membership membership : memberships)
      {
        RoleId roleId = membership.getRoleId();
        if (role.equals(roleId.getId()))
        {
          learners.add(membership.getUsername());
        }
      }
    }
    return learners;
  }
}

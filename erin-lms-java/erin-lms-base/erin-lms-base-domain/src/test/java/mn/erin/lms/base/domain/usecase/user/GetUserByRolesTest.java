package mn.erin.lms.base.domain.usecase.user;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.aim.model.group.Group;
import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.domain.aim.model.membership.Membership;
import mn.erin.domain.aim.model.membership.MembershipId;
import mn.erin.domain.aim.model.role.RoleId;
import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.aim.model.user.User;
import mn.erin.domain.aim.model.user.UserAggregate;
import mn.erin.domain.aim.model.user.UserContact;
import mn.erin.domain.aim.model.user.UserId;
import mn.erin.domain.aim.model.user.UserIdentity;
import mn.erin.domain.aim.model.user.UserProfile;
import mn.erin.domain.aim.model.user.UserStatus;
import mn.erin.domain.aim.repository.AimRepositoryRegistry;
import mn.erin.domain.aim.repository.GroupRepository;
import mn.erin.domain.aim.repository.MembershipRepository;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.aim.service.UserAggregateService;
import mn.erin.domain.base.model.person.PersonId;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.AccessIdentityManagement;
import mn.erin.lms.base.aim.LmsDepartmentService;
import mn.erin.lms.base.aim.LmsUserService;
import mn.erin.lms.base.aim.role.LmsRole;
import mn.erin.lms.base.aim.user.LmsAdmin;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.user.dto.GetUserByRolesInput;
import mn.erin.lms.base.domain.usecase.user.dto.GetUserByRolesOutput;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Oyungerel Chuluunsukh
 **/
public class GetUserByRolesTest
{
  public static final String CURRENT_DEPARTMENT = "currentDepartment";
  private LmsDepartmentService lmsDepartmentService;
  private MembershipRepository membershipRepository;
  private AuthorizationService authorizationService;
  private UserAggregateService userAggregateService;
  private GetUserByRoles getUserByRoles;

  private Set<String> subgroupIds;
  private Set<Group> subgroups;
  private Map<String, Membership> memberships;

  @Before
  public void setUp() throws Exception
  {
    AimRepositoryRegistry aimRepositoryRegistry = mock(AimRepositoryRegistry.class);
    LmsServiceRegistry lmsServiceRegistry = mock(LmsServiceRegistry.class);
    AuthenticationService authenticationService = mock(AuthenticationService.class);
    AccessIdentityManagement accessIdentityManagement = mock(AccessIdentityManagement.class);
    GroupRepository groupRepository = mock(GroupRepository.class);
    LmsUserService lmsUserService = mock(LmsUserService.class);

    lmsDepartmentService = mock(LmsDepartmentService.class);
    membershipRepository = mock(MembershipRepository.class);
    authorizationService = mock(AuthorizationService.class);
    userAggregateService = mock(UserAggregateService.class);

    when(lmsServiceRegistry.getAuthorizationService()).thenReturn(authorizationService);
    when(aimRepositoryRegistry.getUserAggregateService()).thenReturn(userAggregateService);
    when(lmsServiceRegistry.getAuthenticationService()).thenReturn(authenticationService);
    when(lmsServiceRegistry.getAccessIdentityManagement()).thenReturn(accessIdentityManagement);
    when(lmsServiceRegistry.getDepartmentService()).thenReturn(lmsDepartmentService);
    when(aimRepositoryRegistry.getGroupRepository()).thenReturn(groupRepository);
    when(aimRepositoryRegistry.getMembershipRepository()).thenReturn(membershipRepository);
    when(lmsUserService.getCurrentUser()).thenReturn(new LmsAdmin(PersonId.valueOf("admin")));
    //this one is for authorized usecase
    when(authenticationService.getCurrentUsername()).thenReturn("admin");
    when(userAggregateService.getAllUserAggregates(false)).thenReturn(this.setUpDummyData());
    //this one is for get user by roles usecase
    when(accessIdentityManagement.getCurrentUsername()).thenReturn("admin");
    when(accessIdentityManagement.getCurrentUserDepartmentId()).thenReturn(CURRENT_DEPARTMENT);
    when(groupRepository.getAllByIds(Mockito.anySet())).thenReturn(subgroups);
    when(authorizationService.hasPermission(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
    getUserByRoles = new GetUserByRoles(null, lmsServiceRegistry, aimRepositoryRegistry);
  }

  @Test(expected = UseCaseException.class)
  public void when_useCase_not_authorized() throws UseCaseException
  {
    when(authorizationService.hasPermission(Mockito.anyString(), Mockito.anyString())).thenReturn(false);
    List<GetUserByRolesOutput> result = getUserByRoles.executeImpl(new GetUserByRolesInput(null, true));
    Assert.assertTrue(result.isEmpty());
  }

  @Test()
  public void when_memberships_areEmpty() throws UseCaseException
  {
    when(authorizationService.hasPermission(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
    when(membershipRepository.getAllForUsers(Mockito.anyList())).thenReturn(Collections.emptyMap());

    List<GetUserByRolesOutput> result = getUserByRoles.executeImpl(new GetUserByRolesInput(null, true));
    Assert.assertTrue(result.isEmpty());
  }

  @Test()
  public void whenRoles_areEmpty() throws UseCaseException
  {
    when(authorizationService.hasPermission(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
    when(lmsDepartmentService.getSubDepartments(Mockito.anyString())).thenReturn(Collections.emptySet());

    when(membershipRepository.getAllForUsers(Mockito.anyList())).thenReturn(Collections.emptyMap());

    List<GetUserByRolesOutput> result = getUserByRoles.executeImpl(new GetUserByRolesInput(new ArrayList<>(), true));
    Assert.assertTrue(result.isEmpty());
  }

  @Test()
  public void whenSubgroupsExist_butRolesAreNonMatching() throws UseCaseException
  {
    when(authorizationService.hasPermission(Mockito.anyString(), Mockito.anyString())).thenReturn(true);

    when(lmsDepartmentService.getSubDepartments(Mockito.anyString())).thenReturn(subgroupIds);
    when(membershipRepository.getAllForUsers(Mockito.anyList())).thenReturn(memberships);

    List<GetUserByRolesOutput> result = getUserByRoles.executeImpl(new GetUserByRolesInput(new ArrayList<>(), true));
    Assert.assertTrue(result.isEmpty());
  }

  @Test()
  public void whenSubgroupsExist_andHasRoles() throws UseCaseException
  {
    when(authorizationService.hasPermission(Mockito.anyString(), Mockito.anyString())).thenReturn(true);

    when(lmsDepartmentService.getSubDepartments(Mockito.anyString())).thenReturn(subgroupIds);

    when(membershipRepository.getAllForUsers(Mockito.anyList())).thenReturn(memberships);
    List<String> userRoles = new ArrayList<>();
    userRoles.add(LmsRole.LMS_SUPERVISOR.name());
    userRoles.add(LmsRole.LMS_USER.name());
    List<GetUserByRolesOutput> result = getUserByRoles.executeImpl(new GetUserByRolesInput(userRoles, true));
    Assert.assertEquals(5, result.size());
  }

  @Test()
  public void when_includeMe_false() throws UseCaseException
  {
    when(authorizationService.hasPermission(Mockito.anyString(), Mockito.anyString())).thenReturn(true);

    when(lmsDepartmentService.getSubDepartments(Mockito.anyString())).thenReturn(subgroupIds);

    when(membershipRepository.getAllForUsers(Mockito.anyList())).thenReturn(memberships);
    List<String> roles = new ArrayList<>();
    roles.add(LmsRole.LMS_ADMIN.name());
    roles.add(LmsRole.LMS_USER.name());
    List<GetUserByRolesOutput> result = getUserByRoles.executeImpl(new GetUserByRolesInput(roles, false));
    Assert.assertEquals(5, result.size());
  }

  @Test()
  public void when_includeMe_false_hasInactiveUser() throws Exception
  {
    Collection<UserAggregate> aggregates = setUpDummyData();
    UserAggregate userAggregate = aggregates.iterator().next();
    aggregates.remove(userAggregate);
    userAggregate.getRootEntity().setStatus(UserStatus.ARCHIVED);
    aggregates.add(userAggregate);

    when(userAggregateService.getAllUserAggregates(false)).thenReturn(aggregates);

    when(authorizationService.hasPermission(Mockito.anyString(), Mockito.anyString())).thenReturn(true);

    when(lmsDepartmentService.getSubDepartments(Mockito.anyString())).thenReturn(subgroupIds);

    when(membershipRepository.getAllForUsers(Mockito.anyList())).thenReturn(memberships);

    List<String> roles = new ArrayList<>();
    roles.add(LmsRole.LMS_ADMIN.name());
    roles.add(LmsRole.LMS_USER.name());
    List<GetUserByRolesOutput> result = getUserByRoles.executeImpl(new GetUserByRolesInput(roles, false));
    Assert.assertEquals(4, result.size());
  }

  @Test()
  public void when_includeMe_true() throws UseCaseException
  {
    when(authorizationService.hasPermission(Mockito.anyString(), Mockito.anyString())).thenReturn(true);

    when(lmsDepartmentService.getSubDepartments(Mockito.anyString())).thenReturn(subgroupIds);

    when(membershipRepository.getAllForUsers(Mockito.anyList())).thenReturn(memberships);
    List<String> roles = new ArrayList<>();
    roles.add(LmsRole.LMS_ADMIN.name());
    roles.add(LmsRole.LMS_USER.name());
    List<GetUserByRolesOutput> result = getUserByRoles.executeImpl(new GetUserByRolesInput(roles, true));
    Assert.assertEquals(6, result.size());
  }

  @Test()
  public void whenRole_isUser() throws UseCaseException
  {
    when(authorizationService.hasPermission(Mockito.anyString(), Mockito.anyString())).thenReturn(true);

    when(lmsDepartmentService.getSubDepartments(Mockito.anyString())).thenReturn(subgroupIds);

    when(membershipRepository.getAllForUsers(Mockito.anyList())).thenReturn(memberships);
    List<String> roles = new ArrayList<>();
    roles.add(LmsRole.LMS_USER.name());
    List<GetUserByRolesOutput> result = getUserByRoles.executeImpl(new GetUserByRolesInput(roles, true));
    Assert.assertEquals(5, result.size());
  }

  @Test()
  public void whenRole_isAdmin() throws UseCaseException
  {
    when(authorizationService.hasPermission(Mockito.anyString(), Mockito.anyString())).thenReturn(true);

    when(lmsDepartmentService.getSubDepartments(Mockito.anyString())).thenReturn(subgroupIds);

    when(membershipRepository.getAllForUsers(Mockito.anyList())).thenReturn(memberships);
    List<String> roles = new ArrayList<>();
    roles.add(LmsRole.LMS_ADMIN.name());
    List<GetUserByRolesOutput> result = getUserByRoles.executeImpl(new GetUserByRolesInput(roles, true));
    Assert.assertEquals(1, result.size());
  }

  @Test()
  public void when_includeMe_true_hasInactiveUser() throws Exception
  {
    Collection<UserAggregate> aggregates = setUpDummyData();
    UserAggregate userAggregate = aggregates.iterator().next();
    aggregates.remove(userAggregate);
    userAggregate.getRootEntity().setStatus(UserStatus.ARCHIVED);
    aggregates.add(userAggregate);

    when(userAggregateService.getAllUserAggregates(false)).thenReturn(aggregates);

    when(authorizationService.hasPermission(Mockito.anyString(), Mockito.anyString())).thenReturn(true);

    when(lmsDepartmentService.getSubDepartments(Mockito.anyString())).thenReturn(subgroupIds);

    when(membershipRepository.getAllForUsers(Mockito.anyList())).thenReturn(memberships);
    List<String> roles = new ArrayList<>();
    roles.add(LmsRole.LMS_ADMIN.name());
    roles.add(LmsRole.LMS_USER.name());
    List<GetUserByRolesOutput> result = getUserByRoles.executeImpl(new GetUserByRolesInput(roles, true));
    Assert.assertEquals(5, result.size());
  }

  private Collection<UserAggregate> setUpDummyData()
  {
    List<UserAggregate> userAggregateList = new ArrayList<>();
    memberships = new HashMap<>();
    subgroupIds = new HashSet<>();
    subgroups = new HashSet<>();
    Group parentGroup = new Group(GroupId.valueOf(CURRENT_DEPARTMENT), null, new TenantId("tenant"), CURRENT_DEPARTMENT);
    for (int index = 0; index < 5; index++)
    {
      UserId userId = new UserId("user" + index);
      userAggregateList.add(new UserAggregate(
          new User(userId, new TenantId("tenant")),
          new UserIdentity(userId, "username" + index, "pass" + index),
          new UserProfile(userId, new UserContact())));

      subgroupIds.add("group" + index);
      parentGroup.addChild(GroupId.valueOf("group" + index));
      subgroups.add(new Group(GroupId.valueOf("group" + index), GroupId.valueOf(CURRENT_DEPARTMENT), new TenantId("tenant"), "groupName" + index));
      memberships.put("username" + index,
          new Membership(MembershipId.valueOf("membershipId" + index),
              "username" + index, new GroupId(
              "group" + index), RoleId.valueOf("LMS_USER")));
    }

    subgroupIds.add(CURRENT_DEPARTMENT);
    subgroups.add(parentGroup);
    memberships.put("admin",
        new Membership(MembershipId.valueOf("membershipId"),
            "admin", new GroupId(
            CURRENT_DEPARTMENT), RoleId.valueOf("LMS_ADMIN")));

    UserId myUserId = new UserId("admin");
    UserAggregate currentUserAggregate = new UserAggregate(
        new User(myUserId, new TenantId("tenant")),
        new UserIdentity(myUserId, "admin", "pass"),
        new UserProfile(myUserId, new UserContact()));
    userAggregateList.add(currentUserAggregate);
    return userAggregateList;
  }
}
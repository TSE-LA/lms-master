//TODO: Fix this test
//package mn.erin.lms.base.domain.usecase;
//
//import java.util.Arrays;
//import java.util.Collection;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.Map;
//import java.util.Set;
//
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.Test;
//import org.mockito.Mockito;
//
//import mn.erin.domain.aim.model.group.Group;
//import mn.erin.domain.aim.model.group.GroupId;
//import mn.erin.domain.aim.model.membership.Membership;
//import mn.erin.domain.aim.model.membership.MembershipId;
//import mn.erin.domain.aim.model.role.RoleId;
//import mn.erin.domain.aim.model.tenant.TenantId;
//import mn.erin.domain.aim.model.user.UserAggregate;
//import mn.erin.domain.aim.model.user.UserId;
//import mn.erin.domain.aim.model.user.UserIdentity;
//import mn.erin.domain.aim.repository.AimRepositoryRegistry;
//import mn.erin.domain.aim.repository.GroupRepository;
//import mn.erin.domain.aim.repository.MembershipRepository;
//import mn.erin.domain.aim.service.AuthenticationService;
//import mn.erin.domain.aim.service.AuthorizationService;
//import mn.erin.domain.aim.service.UserAggregateService;
//import mn.erin.domain.base.model.person.PersonId;
//import mn.erin.domain.base.usecase.UseCaseException;
//import mn.erin.lms.base.aim.AccessIdentityManagement;
//import mn.erin.lms.base.aim.LmsDepartmentService;
//import mn.erin.lms.base.aim.LmsUserService;
//import mn.erin.lms.base.aim.user.LmsAdmin;
//import mn.erin.lms.base.domain.model.search.SearchInput;
//import mn.erin.lms.base.domain.model.search.UserSearchOutput;
//import mn.erin.lms.base.domain.model.search.UsersWithGroup;
//import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
//import mn.erin.lms.base.domain.service.LmsServiceRegistry;
//
///**
// * @author Galsan Bayart.
// */
//public class SearchUserTest
//{
//  private SearchUser searchUser;
//  private SearchInput searchInput;
//
//  @Before
//  public void setUp() throws Exception
//  {
//    LmsRepositoryRegistry lmsRepositoryRegistry = Mockito.mock(LmsRepositoryRegistry.class);
//    LmsServiceRegistry lmsServiceRegistry = Mockito.mock(LmsServiceRegistry.class);
//    AimRepositoryRegistry aimRepositoryRegistry = Mockito.mock(AimRepositoryRegistry.class);
//    GroupRepository groupRepository = Mockito.mock(GroupRepository.class);
//    LmsDepartmentService departmentService = Mockito.mock(LmsDepartmentService.class);
//
//    Mockito.when(aimRepositoryRegistry.getGroupRepository()).thenReturn(groupRepository);
//    Mockito.when(lmsServiceRegistry.getDepartmentService()).thenReturn(departmentService);
//    Mockito.when(departmentService.getCurrentDepartmentId()).thenReturn("root");
//
//    searchUser = new SearchUser(lmsRepositoryRegistry, lmsServiceRegistry, aimRepositoryRegistry);
//    searchInput = new SearchInput("", Collections.emptyList());
//
//    Set<String> departments = new HashSet<>();
//    departments.add("root");
//    departments.add("sub1");
//    departments.add("sub2");
//    departments.add("sub11");
//    departments.add("sub12");
//
//    Group root = new Group(GroupId.valueOf("root"), null, TenantId.valueOf("tenantId"), "root");
//    Group sub1 = new Group(GroupId.valueOf("sub1"), GroupId.valueOf("root"), TenantId.valueOf("tenantId"), "sub1");
//    Group sub2 = new Group(GroupId.valueOf("sub2"), GroupId.valueOf("root"), TenantId.valueOf("tenantId"), "sub2");
//    Group sub11 = new Group(GroupId.valueOf("sub11"), GroupId.valueOf("sub1"), TenantId.valueOf("tenantId"), "sub11");
//    Group sub12 = new Group(GroupId.valueOf("sub12"), GroupId.valueOf("sub1"), TenantId.valueOf("tenantId"), "sub12");
//    Set<Group> groups = new HashSet<>(Arrays.asList(root, sub1, sub2, sub11, sub12));
//
//    root.addChild(GroupId.valueOf("sub1"));
//    root.addChild(GroupId.valueOf("sub2"));
//    sub1.addChild(GroupId.valueOf("sub11"));
//    sub1.addChild(GroupId.valueOf("sub12"));
//
//    Mockito.when(departmentService.getSubDepartments(Mockito.anyString())).thenReturn(departments);
//    Mockito.when(groupRepository.getAllByIds(Mockito.anySet())).thenReturn(groups);
//
//    LmsUserService lmsUserService = Mockito.mock(LmsUserService.class);
//    Mockito.when(lmsServiceRegistry.getLmsUserService()).thenReturn(lmsUserService);
//    Mockito.when(lmsUserService.getCurrentUser()).thenReturn(new LmsAdmin(PersonId.valueOf("currentUser")));
//
//    UserAggregateService userAggregateService = Mockito.mock(UserAggregateService.class);
//    Mockito.when(aimRepositoryRegistry.getUserAggregateService()).thenReturn(userAggregateService);
//
//    AuthenticationService authenticationService = Mockito.mock(AuthenticationService.class);
//    Mockito.when(lmsServiceRegistry.getAuthenticationService()).thenReturn(authenticationService);
//
//    AuthorizationService authorizationService = Mockito.mock(AuthorizationService.class);
//    Mockito.when(lmsServiceRegistry.getAuthorizationService()).thenReturn(authorizationService);
//
//    AccessIdentityManagement accessIdentityManagement = Mockito.mock(AccessIdentityManagement.class);
//    Mockito.when(lmsServiceRegistry.getAccessIdentityManagement()).thenReturn(accessIdentityManagement);
//    Mockito.when(accessIdentityManagement.getCurrentUsername()).thenReturn("currentUserName");
//    Mockito.when(authenticationService.getCurrentUsername()).thenReturn("currentUserName");
//    Mockito.when(authorizationService.hasPermission(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
//
//    Mockito.when(userAggregateService.getAllUserAggregates(false)).thenReturn(getUserAggregates());
//
//    MembershipRepository membershipRepository = Mockito.mock(MembershipRepository.class);
//    Mockito.when(aimRepositoryRegistry.getMembershipRepository()).thenReturn(membershipRepository);
//
//    Mockito.when(membershipRepository.getAllForUsers(Mockito.anyList())).thenReturn(getMembershipMap());
//  }
//
//  @Test
//  public void whenGettingUsersFromSearch_success() throws UseCaseException
//  {
//    int userWithGroupNumber = 4;
//    int userWithoutGroup = 2;
//    UserSearchOutput userSearchOutput = searchUser.execute(searchInput);
//    Assert.assertEquals(userWithGroupNumber, userSearchOutput.getUsersWithGroup().size());
//    Assert.assertEquals(userWithoutGroup, userSearchOutput.getUsersWithoutGroup().size());
//  }
//
//  @Test
//  public void whenSelectedUserExist_usersWithoutGroupNumberMustBeSubtracted() throws UseCaseException
//  {
//    int userWithGroupNumber = 4;
//    int userWithoutGroup = 1;
//    searchInput.setSelectedUsers(Collections.singletonList("NoGroupUser1"));
//    UserSearchOutput userSearchOutput = searchUser.execute(searchInput);
//    Assert.assertEquals(userWithGroupNumber, userSearchOutput.getUsersWithGroup().size());
//    Assert.assertEquals(userWithoutGroup, userSearchOutput.getUsersWithoutGroup().size());
//  }
//
//  @Test
//  public void whenSelectedStringExists_returnedUsersNamesMustBeContainsThatString() throws UseCaseException
//  {
//    String searchString = "1";
//    searchInput.setSearchKey(searchString);
//    UserSearchOutput userSearchOutput = searchUser.execute(searchInput);
//    for (String userNames : userSearchOutput.getUsersWithoutGroup()){
//      Assert.assertTrue(userNames.contains(searchString));
//    }
//    for (UsersWithGroup user: userSearchOutput.getUsersWithGroup()){
//      Assert.assertTrue(user.getUserName().contains(searchString));
//    }
//  }
//
//  private Collection<UserAggregate> getUserAggregates()
//  {
//    UserAggregate rootUser = new UserAggregate(null, new UserIdentity(UserId.valueOf("userId"), "rootUser", "password"), null);
//    UserAggregate sub1User = new UserAggregate(null, new UserIdentity(UserId.valueOf("userId"), "sub1User", "password"), null);
//    UserAggregate sub2User = new UserAggregate(null, new UserIdentity(UserId.valueOf("userId"), "sub2User", "password"), null);
//    UserAggregate sub11User = new UserAggregate(null, new UserIdentity(UserId.valueOf("userId"), "sub11User", "password"), null);
//    UserAggregate sub12User = new UserAggregate(null, new UserIdentity(UserId.valueOf("userId"), "sub12User", "password"), null);
//    UserAggregate noGroupUser1 = new UserAggregate(null, new UserIdentity(UserId.valueOf("userId"), "NoGroupUser1", "password"), null);
//    UserAggregate noGroupUser2 = new UserAggregate(null, new UserIdentity(UserId.valueOf("userId"), "NoGroupUser2", "password"), null);
//    UserAggregate anotherGroupUser = new UserAggregate(null, new UserIdentity(UserId.valueOf("userId"), "AnotherGroupUser", "password"), null);
//
//    return Arrays.asList(rootUser, sub1User, sub2User, sub11User, sub12User, noGroupUser1, noGroupUser2, anotherGroupUser);
//  }
//
//  private Map<String, Membership> getMembershipMap()
//  {
//    Membership membershipRoot = new Membership(MembershipId.valueOf("rootMember"), "rootUser", GroupId.valueOf("root"), RoleId.valueOf("LMS_USER"));
//    Membership membershipSub1 = new Membership(MembershipId.valueOf("sub1Member"), "sub1User", GroupId.valueOf("sub1"), RoleId.valueOf("LMS_USER"));
//    Membership membershipSub2 = new Membership(MembershipId.valueOf("sub2Member"), "sub2User", GroupId.valueOf("sub2"), RoleId.valueOf("LMS_USER"));
//    Membership membershipSub11 = new Membership(MembershipId.valueOf("sub11Member"), "sub11User", GroupId.valueOf("sub11"), RoleId.valueOf("LMS_USER"));
//    Membership membershipSub12 = new Membership(MembershipId.valueOf("sub12Member"), "sub12user", GroupId.valueOf("sub12"), RoleId.valueOf("LMS_USER"));
//    Membership anotherGroupMembership = new Membership(MembershipId.valueOf("anotherGroupMembership"), "AnotherGroupUser", GroupId.valueOf("anotherGroupId"),
//        RoleId.valueOf("LMS_USER"));
//
//    Map<String, Membership> membershipMap = new HashMap<>();
//    membershipMap.put("rootUser", membershipRoot);
//    membershipMap.put("sub1User", membershipSub1);
//    membershipMap.put("sub2User", membershipSub2);
//    membershipMap.put("sub11User", membershipSub11);
//    membershipMap.put("sub12User", membershipSub12);
//    membershipMap.put("NoGroupUser1", null);
//    membershipMap.put("NoGroupUser2", null);
//    membershipMap.put("AnotherGroupUser", anotherGroupMembership);
//
//    return membershipMap;
//  }
//}
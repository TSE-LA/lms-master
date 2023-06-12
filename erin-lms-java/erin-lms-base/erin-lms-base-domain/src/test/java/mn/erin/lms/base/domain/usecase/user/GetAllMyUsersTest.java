package mn.erin.lms.base.domain.usecase.user;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

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
import mn.erin.domain.aim.repository.MembershipRepository;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.aim.service.UserAggregateService;
import mn.erin.domain.aim.usecase.user.GetAllUsers;
import mn.erin.domain.base.model.person.PersonId;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.lms.base.aim.AccessIdentityManagement;
import mn.erin.lms.base.aim.LmsUserService;
import mn.erin.lms.base.aim.user.LmsAdmin;
import mn.erin.lms.base.aim.user.LmsManager;
import mn.erin.lms.base.aim.user.LmsUser;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.user.dto.GetUserByRolesOutput;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

/**
 * @author Temuulen Naranbold
 */
public class GetAllMyUsersTest
{
  private static final String ADMIN = "admin";
  private static final String CURRENT_DEPARTMENT = "currentDepartment";

  private AimRepositoryRegistry aimRepositoryRegistry;
  private LmsRepositoryRegistry lmsRepositoryRegistry;
  private LmsServiceRegistry lmsServiceRegistry;
  private AuthenticationService authenticationService;
  private AuthorizationService authorizationService;
  private UserAggregateService userAggregateService;
  private AccessIdentityManagement accessIdentityManagement;
  private MembershipRepository membershipRepository;
  private LmsUserService lmsUserService;
  private GetAllUsers getAllUsers;

  private GetAllMyUsers getAllMyUsers;

  @Before
  public void setUp() throws Exception
  {
    aimRepositoryRegistry = mock(AimRepositoryRegistry.class);
    lmsRepositoryRegistry = mock(LmsRepositoryRegistry.class);
    lmsServiceRegistry = mock(LmsServiceRegistry.class);
    authenticationService = mock(AuthenticationService.class);
    authorizationService = mock(AuthorizationService.class);
    userAggregateService = mock(UserAggregateService.class);
    accessIdentityManagement = mock(AccessIdentityManagement.class);
    membershipRepository = mock(MembershipRepository.class);
    lmsUserService = mock(LmsUserService.class);

    when(lmsServiceRegistry.getAuthenticationService()).thenReturn(authenticationService);
    when(lmsServiceRegistry.getAuthorizationService()).thenReturn(authorizationService);
    when(aimRepositoryRegistry.getUserAggregateService()).thenReturn(userAggregateService);
    when(lmsServiceRegistry.getAccessIdentityManagement()).thenReturn(accessIdentityManagement);
    when(aimRepositoryRegistry.getMembershipRepository()).thenReturn(membershipRepository);
    when(lmsServiceRegistry.getLmsUserService()).thenReturn(lmsUserService);
    when(authenticationService.getCurrentUsername()).thenReturn(ADMIN);
    when(authorizationService.hasPermission(anyString(), anyString())).thenReturn(true);

    when(accessIdentityManagement.getCurrentUsername()).thenReturn(ADMIN);
    LmsUser user = new LmsAdmin(new PersonId(ADMIN));
    when(lmsUserService.getCurrentUser()).thenReturn(user);
    when(membershipRepository.getAllForUsers(any())).thenReturn(generateMembership());

    getAllMyUsers = new GetAllMyUsers(lmsRepositoryRegistry, lmsServiceRegistry, aimRepositoryRegistry);
  }

  @Test(expected = NullPointerException.class)
  public void whenAuthenticationService_isNull() throws UseCaseException
  {
    when(lmsServiceRegistry.getAuthenticationService()).thenReturn(null);
    getAllMyUsers.executeImpl(null);
  }

  @Test(expected = NullPointerException.class)
  public void whenAuthorizationService_isNull() throws UseCaseException
  {
    when(lmsServiceRegistry.getAuthorizationService()).thenReturn(null);
    getAllMyUsers.executeImpl(null);
  }

  @Test(expected = NullPointerException.class)
  public void whenAccessIdentityManagement_isNull() throws UseCaseException
  {
    when(lmsServiceRegistry.getAccessIdentityManagement()).thenReturn(null);
    getAllMyUsers.executeImpl(null);
  }

  @Test
  public void whenIncludeMe_isFalse() throws Exception
  {
    getAllUsers = new GetAllUsers(authenticationService, authorizationService, userAggregateService);

    when(userAggregateService.getAllUserAggregates(false)).thenReturn(this.setUpDummyData());
    Assert.assertEquals(5, getAllMyUsers.executeImpl(false).size());
  }

  @Test
  public void whenIncludeMe_isTrue() throws Exception
  {
    getAllUsers = new GetAllUsers(authenticationService, authorizationService, userAggregateService);

    List<GetUserByRolesOutput> actual = setUpDummyData().stream().map(GetUserByRolesOutput::new).collect(Collectors.toList());

    when(userAggregateService.getAllUserAggregates(false)).thenReturn(this.setUpDummyData());
    Assert.assertEquals(actual.size(), getAllMyUsers.executeImpl(true).size());
  }

  @Test
  public void whenHasInactiveUser() throws Exception
  {
    getAllUsers = new GetAllUsers(authenticationService, authorizationService, userAggregateService);

    Collection<UserAggregate> aggregates = setUpDummyData();
    UserAggregate userAggregate = aggregates.iterator().next();
    aggregates.remove(userAggregate);
    userAggregate.getRootEntity().setStatus(UserStatus.ARCHIVED);
    aggregates.add(userAggregate);

    when(userAggregateService.getAllUserAggregates(false)).thenReturn(aggregates);
    Assert.assertEquals(5, getAllMyUsers.executeImpl(true).size());
  }

  @Test(expected = UseCaseException.class)
  public void whenGetAllUsersNull() throws Exception
  {
    getAllUsers = new GetAllUsers(authenticationService, authorizationService, userAggregateService);
    when(userAggregateService.getAllUserAggregates(false)).thenReturn(null);
    getAllMyUsers.executeImpl(true);
  }

  @Test(expected = NullPointerException.class)
  public void whenMembershipsNull() throws Exception
  {
    getAllUsers = new GetAllUsers(authenticationService, authorizationService, userAggregateService);
    when(membershipRepository.getAllForUsers(any())).thenReturn(null);
    when(userAggregateService.getAllUserAggregates(false)).thenReturn(this.setUpDummyData());
    getAllMyUsers.executeImpl(true);
  }

  @Test
  public void whenCurrent_userRole_isManager() throws Exception
  {
    roleTest("LMS_MANAGER");
  }

  @Test
  public void whenCurrent_userRole_isSupervisor() throws Exception
  {
    roleTest("LMS_SUPERVISOR");
  }

  @Test
  public void whenCurrent_userRole_isUser() throws Exception
  {
    roleTest("LMS_USER");
  }

  private void roleTest(String role) throws Exception
  {
    getAllUsers = new GetAllUsers(authenticationService, authorizationService, userAggregateService);
    LmsUser user = new LmsManager(PersonId.valueOf("manager"));
    when(lmsUserService.getCurrentUser()).thenReturn(user);
    Map<String, Membership> membershipMap = generateMembership();
    membershipMap.remove(ADMIN);
    Membership membership = new Membership(
        MembershipId.valueOf("membershipId"), ADMIN, new GroupId(
        CURRENT_DEPARTMENT), RoleId.valueOf(role));
    membershipMap.put(ADMIN, membership);
    when(membershipRepository.getAllForUsers(any())).thenReturn(membershipMap);
    when(userAggregateService.getAllUserAggregates(false)).thenReturn(this.setUpDummyData());
    List<Membership> actual = getAllMyUsers.executeImpl(true).stream().map(GetUserByRolesOutput::getMembership).collect(Collectors.toList());
    Assert.assertTrue(actual.contains(membership));
  }

  private Collection<UserAggregate> setUpDummyData()
  {
    List<UserAggregate> userAggregateList = new ArrayList<>();
    Group parentGroup = new Group(GroupId.valueOf(CURRENT_DEPARTMENT), null, new TenantId("tenant"), CURRENT_DEPARTMENT);
    for (int index = 0; index < 5; index++)
    {
      UserId userId = new UserId("user" + index);
      userAggregateList.add(new UserAggregate(
          new User(userId, new TenantId("tenant")),
          new UserIdentity(userId, "username" + index, "pass" + index),
          new UserProfile(userId, new UserContact())));

      parentGroup.addChild(GroupId.valueOf("group" + index));
    }
    UserId myUserId = new UserId(ADMIN);
    UserAggregate currentUserAggregate = new UserAggregate(
        new User(myUserId, new TenantId("tenant")),
        new UserIdentity(myUserId, ADMIN, "pass"),
        new UserProfile(myUserId, new UserContact()));
    userAggregateList.add(currentUserAggregate);
    return userAggregateList;
  }

  private Map<String, Membership> generateMembership()
  {
    Map<String, Membership> memberships = new HashMap<>();
    for (int index = 0; index < 5; index++)
    {
      memberships.put("username" + index,
          new Membership(MembershipId.valueOf("membershipId" + index),
              "username" + index, new GroupId(
              "group" + index), RoleId.valueOf("LMS_USER")));
    }
    memberships.put("admin",
        new Membership(MembershipId.valueOf("membershipId"),
            "admin", new GroupId(
            CURRENT_DEPARTMENT), RoleId.valueOf("LMS_ADMIN")));
    return memberships;
  }
}

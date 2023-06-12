package mn.erin.aim.repository.memory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.domain.aim.model.membership.Membership;
import mn.erin.domain.aim.model.membership.MembershipId;
import mn.erin.domain.aim.model.role.RoleId;
import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.aim.repository.MembershipRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

/**
 * author Naranbaatar Avir.
 */
@Repository
public class MemoryMembershipRepository implements MembershipRepository
{

  public static final String BRANCH_SPECIALIST = "-branchSpecialist-";
  public static final String USER_MANAGER = "-userManager-";
  public static final String EBANK_ROLE = "-ebank-";

  public static final String GROUP_ID = "123";

  private final List<Membership> memberships = new ArrayList<>();
  private static final Logger LOGGER = LoggerFactory.getLogger(MemoryMembershipRepository.class);

  public MemoryMembershipRepository()
  {
    try
    {
      Membership membership = create("tamir", GroupId.valueOf(GROUP_ID), RoleId.valueOf(BRANCH_SPECIALIST), new TenantId("Xac"));

      Membership membership1 = create("oyungerel", GroupId.valueOf(GROUP_ID), RoleId.valueOf(BRANCH_SPECIALIST), new TenantId("Xac"));
      Membership membership2 = create("bayartsetseg", GroupId.valueOf(GROUP_ID), RoleId.valueOf(BRANCH_SPECIALIST), new TenantId("Xac"));
      Membership membership3 = create("altansoyombo", GroupId.valueOf(GROUP_ID), RoleId.valueOf(BRANCH_SPECIALIST), new TenantId("Xac"));
      Membership membership4 = create("lhagvaa", GroupId.valueOf(GROUP_ID), RoleId.valueOf(BRANCH_SPECIALIST), new TenantId("Xac"));
      Membership membership5 = create("zambaga", GroupId.valueOf(GROUP_ID), RoleId.valueOf(BRANCH_SPECIALIST), new TenantId("Xac"));
      Membership membership6 = create("otgoo", GroupId.valueOf(GROUP_ID), RoleId.valueOf(BRANCH_SPECIALIST), new TenantId("Xac"));
      Membership membership7 = create("admin", GroupId.valueOf(GROUP_ID), RoleId.valueOf(BRANCH_SPECIALIST), new TenantId("Xac"));
      Membership membership9 = create("munkh", GroupId.valueOf(GROUP_ID), RoleId.valueOf(BRANCH_SPECIALIST), new TenantId("Xac"));
      Membership membership8 = create("ebank", GroupId.valueOf(GROUP_ID), RoleId.valueOf(EBANK_ROLE), new TenantId("Xac"));
      Membership membership10 = create("testuser", GroupId.valueOf(GROUP_ID), RoleId.valueOf(USER_MANAGER), new TenantId("Xac"));



      memberships.add(membership);
      memberships.add(membership1);
      memberships.add(membership2);
      memberships.add(membership3);
      memberships.add(membership4);
      memberships.add(membership5);
      memberships.add(membership6);
      memberships.add(membership7);
      memberships.add(membership8);
      memberships.add(membership9);
      memberships.add(membership10);
    }
    catch (Exception e)
    {
      LOGGER.error(e.getMessage(), e);
    }
  }

  @Override
  public Membership create(String username, GroupId groupId, RoleId roleId, TenantId tenantId) throws AimRepositoryException
  {
    String membershipId = String.valueOf(System.currentTimeMillis());
    Membership membership = new Membership(MembershipId.valueOf(membershipId), username, groupId, roleId);
    membership.setTenantId(tenantId);
    memberships.add(membership);

    return membership;
  }

  @Override
  public Membership findByUsername(String username)
  {
    return memberships.stream()
        .filter(membership -> username.equals(membership.getUsername()))
        .findFirst()
        .orElse(null);
  }

  @Override
  public List<Membership> listAllByUsername(TenantId tenantId, String username)
  {
    List<Membership> membershipList = new ArrayList<>();
    for (Membership membership : memberships)
    {
      if (membership.getUsername().equals(username))
      {
        membershipList.add(membership);
      }
    }
    return membershipList;
  }

  @Override
  public List<Membership> listAllByGroupId(TenantId tenantId, GroupId groupId)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean delete(MembershipId membershipId)
  {
    return memberships.removeIf(membership -> membership.sameIdentityAs(membership));
  }

  @Override
  public Collection<Membership> listAllByRole(RoleId roleId) throws AimRepositoryException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Map<String, Membership> getAllForUsers(Collection<String> usernames)
  {
    Map<String, Membership> membershipMap = new HashMap<>();
    for (String username : usernames)
    {
      Membership membershipForUsername = null;
      for (Membership membership : memberships)
      {
        if (username.equals(membership.getUsername()))
        {
          membershipForUsername = membership;
          break;
        }
      }
      membershipMap.put(username, membershipForUsername);
    }
    return Collections.unmodifiableMap(membershipMap);
  }
}

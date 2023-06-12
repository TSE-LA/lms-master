package mn.erin.domain.aim.model.membership;

import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.domain.aim.model.role.RoleId;
import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.base.model.Entity;
import org.apache.commons.lang3.Validate;

/**
 * Membership entity represents a user being assigned to a group with a particular role.
 * This means a user can be assigned to different groups with different roles
 *
 * @author Bat-Erdene Tsogoo.
 */
public class Membership implements Entity<Membership>
{
  private final MembershipId membershipId;
  private final String username;
  private final GroupId groupId;
  private final RoleId roleId;
  private TenantId tenantId;

  public Membership(MembershipId membershipId, String username, GroupId groupId, RoleId roleId)
  {
    this.membershipId = Validate.notNull(membershipId, "Membership ID cannot be null!");
    this.username = Validate.notNull(username, "UserID cannot be null!");
    this.groupId = Validate.notNull(groupId, "GroupID cannot be null!");
    this.roleId = Validate.notNull(roleId, "RoleID cannot be null!");
  }

  public TenantId getTenantId()
  {
    return tenantId;
  }

  public void setTenantId(TenantId tenantId)
  {
    this.tenantId = tenantId;
  }

  public MembershipId getMembershipId()
  {
    return membershipId;
  }

  public String getUsername()
  {
    return username;
  }

  public GroupId getGroupId()
  {
    return groupId;
  }

  public RoleId getRoleId()
  {
    return roleId;
  }

  @Override
  public boolean sameIdentityAs(Membership other)
  {
    return this.membershipId.equals(other.membershipId);
  }
}

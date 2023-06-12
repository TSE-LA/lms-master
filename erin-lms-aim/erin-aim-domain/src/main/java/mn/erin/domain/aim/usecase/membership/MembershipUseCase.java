package mn.erin.domain.aim.usecase.membership;

import java.util.Objects;

import mn.erin.domain.aim.model.membership.Membership;
import mn.erin.domain.aim.repository.MembershipRepository;
import mn.erin.domain.aim.service.TenantIdProvider;
import mn.erin.domain.base.usecase.UseCase;

/**
 * @author Bat-Erdene Tsogoo.
 */
public abstract class MembershipUseCase<I, O> implements UseCase<I, O>
{
  protected final MembershipRepository membershipRepository;
  protected final TenantIdProvider tenantIdProvider;

  public MembershipUseCase(MembershipRepository membershipRepository, TenantIdProvider tenantIdProvider)
  {
    this.membershipRepository = Objects.requireNonNull(membershipRepository, "MembershipRepository cannot be null!");
    this.tenantIdProvider = tenantIdProvider;
  }

  protected GetMembershipOutput convert(Membership membership)
  {
    GetMembershipOutput output = new GetMembershipOutput();
    output.setMembershipId(membership.getMembershipId().getId());
    output.setGroupId(membership.getGroupId().getId());
    output.setRoleId(membership.getRoleId().getId());
    output.setUserId(membership.getUsername());

    return output;
  }
}

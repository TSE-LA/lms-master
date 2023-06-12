package mn.erin.domain.aim.usecase.membership;

import java.util.ArrayList;
import java.util.List;

import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.domain.aim.model.membership.Membership;
import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.aim.repository.MembershipRepository;
import mn.erin.domain.aim.service.TenantIdProvider;
import mn.erin.domain.base.usecase.UseCaseException;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class GetGroupMemberships extends MembershipUseCase<String, List<GetMembershipOutput>>
{
  public GetGroupMemberships(MembershipRepository membershipRepository, TenantIdProvider tenantIdProvider)
  {
    super(membershipRepository, tenantIdProvider);
  }

  @Override
  public List<GetMembershipOutput> execute(String groupId) throws UseCaseException
  {
    if (StringUtils.isBlank(groupId))
    {
      throw new UseCaseException("Group ID cannot be blank");
    }

    try
    {
      String tenantId = tenantIdProvider.getCurrentUserTenantId();
      List<Membership> memberships = membershipRepository.listAllByGroupId(TenantId.valueOf(tenantId), GroupId.valueOf(groupId));
      List<GetMembershipOutput> result = new ArrayList<>();
      for (Membership membership : memberships)
      {
        result.add(convert(membership));
      }
      return result;
    }
    catch (AimRepositoryException e)
    {
      throw new UseCaseException("Error getting group membership", e);
    }
  }
}

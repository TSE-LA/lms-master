package mn.erin.domain.aim.usecase.membership;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.domain.aim.model.membership.Membership;
import mn.erin.domain.aim.model.membership.MembershipId;
import mn.erin.domain.aim.model.permission.AimModulePermission;
import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.model.role.RoleId;
import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.aim.repository.MembershipRepository;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.aim.service.TenantIdProvider;
import mn.erin.domain.aim.usecase.AuthorizedUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Zorig
 */
public class ChangeUserGroup extends AuthorizedUseCase<ChangeUserGroupInput, ChangeUserGroupOutput>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(ChangeUserGroup.class);

  private static final Permission permission = new AimModulePermission("MoveUserGroup");

  private final MembershipRepository membershipRepository;
  private final TenantIdProvider tenantIdProvider;

  public ChangeUserGroup(AuthenticationService authenticationService, AuthorizationService authorizationService,
      MembershipRepository membershipRepository, TenantIdProvider tenantIdProvider)
  {
    super(authenticationService, authorizationService);
    this.membershipRepository = Objects.requireNonNull(membershipRepository, "MembershipRepository cannot be null!");
    this.tenantIdProvider = Objects.requireNonNull(tenantIdProvider, "TenantIdProvider cannot be null!");
  }

  @Override
  public Permission getPermission()
  {
    return permission;
  }

  @Override
  protected ChangeUserGroupOutput executeImpl(ChangeUserGroupInput input) throws UseCaseException
  {
    validateNotBlank(input.getUsername(), "Username must not be blank");
    validateNotBlank(input.getNewGroupId(), "New group ID must not be blank");

    TenantId tenantId = TenantId.valueOf(tenantIdProvider.getCurrentUserTenantId());
    GroupId newGroupId = new GroupId(input.getNewGroupId());

    //FIXME:logic doesn't make sense after allowing multiple memberships per user, implement a different way when needed
    try
    {
      List<Membership> membershipList = membershipRepository.listAllByUsername(tenantId, input.getUsername());
      Iterator<Membership> membershipIterator = membershipList.iterator();

      Membership currentMembership = membershipIterator.next();
      MembershipId membershipIdToUpdate = MembershipId.valueOf(currentMembership.getMembershipId().getId());
      RoleId roleId = currentMembership.getRoleId();

      membershipRepository.delete(membershipIdToUpdate);
      membershipRepository.create(input.getUsername(), newGroupId, roleId, tenantId);
    }
    catch (Exception e)
    {
      throw new UseCaseException("Problem Updating User Group");
    }

    return new ChangeUserGroupOutput(true);
  }
}

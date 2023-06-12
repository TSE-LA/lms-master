package mn.erin.domain.aim.usecase.membership;

import java.util.ArrayList;
import java.util.List;

import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.domain.aim.model.membership.Membership;
import mn.erin.domain.aim.model.permission.AimModulePermission;
import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.model.role.RoleId;
import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.aim.repository.AimRepositoryRegistry;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.aim.service.TenantIdProvider;
import mn.erin.domain.aim.usecase.AuthorizedUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class CreateMemberships extends AuthorizedUseCase<CreateMembershipsInput, List<GetMembershipOutput>>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(CreateMemberships.class);

  private static final Permission permission = new AimModulePermission("CreateMemberships");

  private final AimRepositoryRegistry repositoryRegistry;
  private TenantIdProvider tenantIdProvider;

  public CreateMemberships(AuthenticationService authenticationService,
    AuthorizationService authorizationService, AimRepositoryRegistry repositoryRegistry, TenantIdProvider tenantIdProvider)
  {
    super(authenticationService, authorizationService);
    this.repositoryRegistry = repositoryRegistry;
    this.tenantIdProvider = tenantIdProvider;
  }

  @Override
  public Permission getPermission()
  {
    return permission;
  }

  @Override
  protected List<GetMembershipOutput> executeImpl(CreateMembershipsInput input) throws UseCaseException
  {
    if (StringUtils.isAnyBlank(input.getGroupId(), input.getRoleId()))
    {
      throw new UseCaseException("Group or role id cannot be blank");
    }
    else if (!repositoryRegistry.getGroupRepository().doesGroupExist(GroupId.valueOf(input.getGroupId())))
    {
      throw new UseCaseException("Group does not exist by ID [" + input.getGroupId() + "]");
    }
    else if (!repositoryRegistry.getRoleRepository().doesExistById(RoleId.valueOf(input.getRoleId())))
    {
      throw new UseCaseException("Role does not exist by ID [" + input.getRoleId() + "]");
    }

    List<GetMembershipOutput> result = new ArrayList<>();
    String tenantId = tenantIdProvider.getCurrentUserTenantId();
    for (String username : input.getUsers())
    {
      try
      {
        if (repositoryRegistry.getUserIdentityRepository().getUserIdentityByUsername(username) == null)
        {
          LOGGER.warn("User does not exist by username [{}]", username);
          continue;
        }
        Membership membership = repositoryRegistry.getMembershipRepository()
            .create(username, GroupId.valueOf(input.getGroupId()), RoleId.valueOf(input.getRoleId()), new TenantId(tenantId));
        result.add(convert(membership));
      }
      catch (AimRepositoryException e)
      {
        LOGGER.error("Error creating user [{}] membership for group [{}] with role [{}]", username, input.getGroupId(), input.getRoleId(), e);
      }
    }

    if (result.isEmpty())
    {
      throw new UseCaseException("No membership has been created in the group with the ID: [" + input.getGroupId() + "]");
    }

    return result;
  }

  private GetMembershipOutput convert(Membership membership)
  {
    GetMembershipOutput output = new GetMembershipOutput();

    output.setMembershipId(membership.getMembershipId().getId());
    output.setGroupId(membership.getGroupId().getId());
    output.setRoleId(membership.getRoleId().getId());
    output.setUserId(membership.getUsername());
    output.setTenantId(membership.getTenantId().getId());

    return output;
  }
}

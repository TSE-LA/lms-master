package mn.erin.domain.aim.usecase.group;

import java.util.Objects;

import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.group.Group;
import mn.erin.domain.aim.model.permission.AimModulePermission;
import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.aim.repository.GroupRepository;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.aim.usecase.AuthorizedUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Zorig
 */
public class CreateGroup extends AuthorizedUseCase<CreateGroupInput, CreateGroupOutput>
{
  private static final Permission permission = new AimModulePermission("CreateGroup");

  private final GroupRepository groupRepository;

  public CreateGroup(AuthenticationService authenticationService, AuthorizationService authorizationService, GroupRepository groupRepository)
  {
    super(authenticationService, authorizationService);
    this.groupRepository = Objects.requireNonNull(groupRepository, "GroupRepository cannot be null!");
  }

  @Override
  public Permission getPermission()
  {
    return permission;
  }

  @Override
  protected CreateGroupOutput executeImpl(CreateGroupInput input) throws UseCaseException
  {
    // TODO create dedicated usecase for CreateRootGroup because parentId can be null
    if ((input.getParentId() != null && StringUtils.isBlank(input.getParentId()))
      || StringUtils.isBlank(input.getTenantId())
      || StringUtils.isBlank(input.getName()))
    {
      throw new UseCaseException("Field(s) can not be blank");
    }

    try
    {
      if (groupRepository.doesGroupExistByName(input.getName()))
      {
        throw new UseCaseException("Group with name [" + input.getName() + "] already exists");
      }
      int nthSibling = groupRepository.getNthSibling(input.getParentId());
      TenantId tenantId = new TenantId(input.getTenantId());
      Group newGroup = groupRepository.createGroup(input.getName(), input.getParentId(), input.getDescription(), tenantId);
      return new CreateGroupOutput(newGroup.getId().getId(), nthSibling);
    }
    catch (IllegalArgumentException | AimRepositoryException e)
    {
      throw new UseCaseException("Group cannot be created", e);
    }
  }
}
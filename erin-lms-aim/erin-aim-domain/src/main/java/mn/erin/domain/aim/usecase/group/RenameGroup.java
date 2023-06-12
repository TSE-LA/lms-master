package mn.erin.domain.aim.usecase.group;

import java.util.Objects;

import mn.erin.domain.aim.model.group.Group;
import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.domain.aim.model.permission.AimModulePermission;
import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.repository.GroupRepository;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.aim.usecase.AuthorizedUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Zorig
 */
public class RenameGroup extends AuthorizedUseCase<RenameGroupInput, RenameGroupOutput>
{
  private static final Permission permission = new AimModulePermission("RenameGroup");

  private final GroupRepository groupRepository;

  public RenameGroup(AuthenticationService authenticationService, AuthorizationService authorizationService,
    GroupRepository groupRepository)
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
  protected RenameGroupOutput executeImpl(RenameGroupInput input) throws UseCaseException
  {
    if (StringUtils.isBlank(input.getName())
      || StringUtils.isBlank(input.getGroupId()))
    {
      throw new UseCaseException("Name should not be blank!");
    }

    if (!groupRepository.doesGroupExist(GroupId.valueOf(input.getGroupId())))
    {
      throw new UseCaseException("Group does not exist!");
    }

    Group renamedGroup = groupRepository.renameGroup(input.getGroupId(), input.getName());
    return new RenameGroupOutput(renamedGroup);
  }
}

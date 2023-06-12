package mn.erin.domain.aim.usecase.group;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.aim.model.group.Group;
import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.domain.aim.model.permission.AimModulePermission;
import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.repository.GroupRepository;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.aim.usecase.AuthorizedUseCase;
import mn.erin.domain.base.model.EntityId;
import mn.erin.domain.base.usecase.UseCaseException;

/**
 * @author Temuulen Naranbold
 */
public class UpdateGroupParent extends AuthorizedUseCase<UpdateGroupParentInput, Group>
{
  private static final Permission permission = new AimModulePermission("UpdateGroup");
  private final GroupRepository groupRepository;

  public UpdateGroupParent(AuthenticationService authenticationService, AuthorizationService authorizationService, GroupRepository groupRepository)
  {
    super(authenticationService, authorizationService);
    this.groupRepository = groupRepository;
  }

  @Override
  public Permission getPermission()
  {
    return permission;
  }

  @Override
  protected Group executeImpl(UpdateGroupParentInput input) throws UseCaseException
  {
    try
    {
      Validate.notNull(input);
      Validate.notBlank(input.getId());
      Validate.notBlank(input.getParentId());
      Group group = groupRepository.findById(GroupId.valueOf(input.getId()));
      if (group.getChildren().stream().map(EntityId::getId).noneMatch(id -> id.equals(input.getParentId())))
      {
        return groupRepository.moveGroupParent(input.getId(), input.getParentId());
      }
      throw new UseCaseException("Could not update group parent by its child");
    }
    catch (NullPointerException | IllegalArgumentException e)
    {
      throw new UseCaseException(e.getMessage());
    }
  }
}

package mn.erin.domain.aim.usecase.group;

import java.util.Objects;

import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.domain.aim.model.permission.AimModulePermission;
import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.repository.GroupRepository;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.aim.usecase.AuthorizedUseCase;
import mn.erin.domain.base.usecase.UseCaseException;

/**
 * @author Zorig
 */
public class DeleteGroup extends AuthorizedUseCase<DeleteGroupInput, DeleteGroupOutput>
{
  private static final Permission permission = new AimModulePermission("DeleteGroup");

  private final GroupRepository groupRepository;

  public DeleteGroup(AuthenticationService authenticationService, AuthorizationService authorizationService,
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
  protected DeleteGroupOutput executeImpl(DeleteGroupInput input) throws UseCaseException
  {
    try
    {
      if (!groupRepository.doesGroupExist(GroupId.valueOf(input.getId())))
      {
        throw new UseCaseException("Can't delete a group that does not exist");
      }
      boolean isDeleted = groupRepository.deleteGroup(input.getId());
      return new DeleteGroupOutput(isDeleted);
    }
    catch (AimRepositoryException e)
    {
      throw new UseCaseException("Group was not deleted", e);
    }
  }
}

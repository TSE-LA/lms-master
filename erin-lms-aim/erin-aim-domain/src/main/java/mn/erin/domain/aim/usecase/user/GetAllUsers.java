package mn.erin.domain.aim.usecase.user;

import mn.erin.domain.aim.model.permission.AimModulePermission;
import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.aim.service.UserAggregateService;
import mn.erin.domain.aim.usecase.AuthorizedUseCase;
import mn.erin.domain.base.usecase.UseCaseException;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class GetAllUsers extends AuthorizedUseCase<Boolean, GetAllUsersOutput>
{
  private static final Permission permission = new AimModulePermission("GetAllUsers");

  private final UserAggregateService userAggregateService;

  public GetAllUsers(AuthenticationService authenticationService, AuthorizationService authorizationService, UserAggregateService userAggregateService)
  {
    super(authenticationService, authorizationService);
    this.userAggregateService = userAggregateService;
  }

  @Override
  public Permission getPermission()
  {
    return permission;
  }

  @Override
  protected GetAllUsersOutput executeImpl(Boolean checkData) throws UseCaseException
  {
    try
    {
      checkData = checkData != null && checkData;
      return new GetAllUsersOutput(userAggregateService.getAllUserAggregates(checkData));
    }
    catch (Exception e)
    {
      throw new UseCaseException(e.getMessage());
    }
  }
}

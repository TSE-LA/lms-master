package mn.erin.domain.aim.usecase.user;

import mn.erin.domain.aim.model.permission.AimModulePermission;
import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.model.user.UserAggregate;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.aim.service.UserAggregateService;
import mn.erin.domain.aim.usecase.AuthorizedUseCase;
import mn.erin.domain.base.usecase.UseCaseException;

/**
 * @author Munkh
 */
public class GetUser extends AuthorizedUseCase<String, GetUserOutput>
{
  private static final Permission permission = new AimModulePermission("GetUser");
  private final UserAggregateService service;

  public GetUser(
    AuthenticationService authenticationService,
    AuthorizationService authorizationService,
    UserAggregateService service)
  {
    super(authenticationService, authorizationService);
    this.service = service;
  }

  @Override
  public Permission getPermission()
  {
    return permission;
  }

  @Override
  protected GetUserOutput executeImpl(String userId) throws UseCaseException
  {
    UserAggregate aggregate = service.getUserAggregate(userId);
    return new GetUserOutput.Builder(aggregate.getUserId())
        .withTenant(aggregate.getTenantId())
        .withUsername(aggregate.getUsername())
        .withFirstName(aggregate.getFirstName())
        .withLastName(aggregate.getLastName())
        .withDisplayName(aggregate.getDisplayName())
        .withGender(aggregate.getGender())
        .withBirthday(aggregate.getBirthday())
        .withJobTitle(aggregate.getJobTitle())
        .withEmail(aggregate.getEmail())
        .withPhoneNumber(aggregate.getPhoneNumber())
        .withStatus(aggregate.getRootEntity().getStatus())
        .withDateLastModified(aggregate.getRootEntity().getLastModified())
        .withImageId(aggregate.getImageId())
        .withMessage(aggregate.getMessage())
        .hasDeletable(aggregate.isDeletable())
        .withProperties(aggregate.getProperties())
        .build();
  }
}

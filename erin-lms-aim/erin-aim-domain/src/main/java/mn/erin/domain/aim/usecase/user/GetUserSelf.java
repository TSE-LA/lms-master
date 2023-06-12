package mn.erin.domain.aim.usecase.user;

import org.apache.commons.lang3.StringUtils;

import mn.erin.domain.aim.model.permission.AimModulePermission;
import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.model.user.UserAggregate;
import mn.erin.domain.aim.model.user.UserIdentity;
import mn.erin.domain.aim.repository.AimRepositoryRegistry;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.aim.service.UserAggregateService;
import mn.erin.domain.aim.usecase.AuthorizedUseCase;
import mn.erin.domain.base.usecase.UseCaseException;

/**
 * @author F I R E
 */
public class GetUserSelf extends AuthorizedUseCase<String, GetUserOutput>
{
  private static final Permission permission = new AimModulePermission("GetUserSelf");
  private final AimRepositoryRegistry registry;
  private final UserAggregateService service;

  public GetUserSelf(AuthenticationService authenticationService,
      AuthorizationService authorizationService, AimRepositoryRegistry aimRepositoryRegistry, UserAggregateService userAggregateService)
  {
    super(authenticationService, authorizationService);
    this.registry = aimRepositoryRegistry;
    this.service = userAggregateService;
  }

  @Override
  public Permission getPermission()
  {
    return permission;
  }

  @Override
  protected GetUserOutput executeImpl(String input) throws UseCaseException
  {
    String username = StringUtils.isBlank(input) ? authenticationService.getCurrentUsername() : input;

    UserIdentity identity = registry.getUserIdentityRepository().getUserIdentityByUsername(username);
    if (identity == null)
    {
      throw new UseCaseException("User identity not found for username: [" + username + "]");
    }
    UserAggregate aggregate = service.getUserAggregate(identity.getUserId().getId());
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
        .withImageUrl(aggregate.getProfile().getUserInfo().getImageURL())
        .withProperties(aggregate.getProperties())
        .build();
  }
}

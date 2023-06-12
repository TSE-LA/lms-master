package mn.erin.domain.aim.usecase;

import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.base.usecase.UseCaseException;

/**
 * @author EBazarragchaa
 */
public class TestAuthorizedUseCase extends AuthorizedUseCase<String, String>
{
  public static final Permission permission = Permission.valueOf("admin", "aim", "testAuthorizedUseCase");

  public TestAuthorizedUseCase(AuthenticationService authenticationService, AuthorizationService authorizationService)
  {
    super(authenticationService, authorizationService);
  }

  @Override
  public Permission getPermission()
  {
    return permission;
  }

  @Override
  public String executeImpl(String input) throws UseCaseException
  {
    return null;
  }
}

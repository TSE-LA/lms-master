package mn.erin.lms.base.aim.permission;

import mn.erin.domain.aim.service.AuthorizationService;

public class AlwaysPermittingAuthorizationService implements AuthorizationService
{
  @Override
  public Boolean hasPermission(String userId, String permissionString)
  {
    return true;
  }
}

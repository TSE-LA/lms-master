package mn.erin.aim.rest.shiro.config;

import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.service.AuthorizationService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

/**
 * @author EBazarragchaa
 */
public class ShiroAuthorizationService implements AuthorizationService
{
  @Override
  public Boolean hasPermission(String username, String permissionString)
  {
    Subject currentUser = SecurityUtils.getSubject();

    if (currentUser.isAuthenticated()
        // FIXME : find root cause and fix it, issue ->
        && currentUser.isPermitted(ShiroUtils.toShiroPermission(Permission.valueOf(permissionString)))
    )
    {
      return Boolean.TRUE;
    }

    return Boolean.FALSE;
  }
}

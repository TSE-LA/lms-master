package mn.erin.lms.aim.shiro.filter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.lms.base.aim.AccessIdentityManagement;
import mn.erin.lms.base.aim.role.LmsRole;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class LmsAdminOnlyFilter extends CustomShiroFilter
{
  public LmsAdminOnlyFilter(AccessIdentityManagement accessIdentityManagement,
      AuthenticationService authenticationService)
  {
    super(accessIdentityManagement, authenticationService);
  }

  @Override
  protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue)
  {
    if (!authenticationService.isCurrentUserAuthenticated() ||
        !LmsRole.LMS_ADMIN.name().equals(accessIdentityManagement.getRole(accessIdentityManagement.getCurrentUsername())))
    {
      return false;
    }

    return super.isAccessAllowed(request, response, mappedValue);
  }
}

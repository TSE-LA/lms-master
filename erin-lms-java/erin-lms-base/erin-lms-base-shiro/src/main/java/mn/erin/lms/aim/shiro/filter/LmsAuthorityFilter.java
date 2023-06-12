package mn.erin.lms.aim.shiro.filter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.lms.base.aim.AccessIdentityManagement;
import mn.erin.lms.base.aim.role.LmsRole;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class LmsAuthorityFilter extends CustomShiroFilter
{
  public LmsAuthorityFilter(AccessIdentityManagement accessIdentityManagement,
      AuthenticationService authenticationService)
  {
    super(accessIdentityManagement, authenticationService);
  }

  @Override
  protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue)
  {
    if (!authenticationService.isCurrentUserAuthenticated())
    {
      return false;
    }

    LmsRole role = getCurrentUserRole();

    if (role == LmsRole.LMS_USER)
    {
      return false;
    }

    return super.isAccessAllowed(request, response, mappedValue);
  }
}

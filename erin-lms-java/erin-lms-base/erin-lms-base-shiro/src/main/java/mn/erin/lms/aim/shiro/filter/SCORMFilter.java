package mn.erin.lms.aim.shiro.filter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.lms.base.aim.AccessIdentityManagement;
import mn.erin.lms.base.aim.role.LmsRole;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class SCORMFilter extends CustomShiroFilter
{
  public SCORMFilter(AccessIdentityManagement accessIdentityManagement, AuthenticationService authenticationService)
  {
    super(accessIdentityManagement, authenticationService);
  }

  @Override
  protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue)
  {
    HttpServletRequest httpRequest = (HttpServletRequest) request;
    if ("GET".equals(httpRequest.getMethod()) || "PUT".equals(httpRequest.getMethod()))
    {
      return super.isAccessAllowed(request, response, mappedValue);
    }

    if (!authenticationService.isCurrentUserAuthenticated() ||
        !LmsRole.LMS_ADMIN.name().equals(accessIdentityManagement.getRole(accessIdentityManagement.getCurrentUsername())))
    {
      return false;
    }

    return super.isAccessAllowed(request, response, mappedValue);
  }
}

package mn.erin.lms.aim.shiro.filter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;

import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.lms.base.aim.AccessIdentityManagement;
import mn.erin.lms.base.aim.role.LmsRole;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class CustomShiroFilter extends FormAuthenticationFilter
{
  protected final AccessIdentityManagement accessIdentityManagement;
  protected final AuthenticationService authenticationService;

  public CustomShiroFilter(AccessIdentityManagement accessIdentityManagement, AuthenticationService authenticationService)
  {
    this.accessIdentityManagement = accessIdentityManagement;
    this.authenticationService = authenticationService;
  }

  @Override
  protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception
  {
    ((HttpServletResponse) response).sendError(HttpServletResponse.SC_UNAUTHORIZED);
    return false;
  }

  protected LmsRole getCurrentUserRole()
  {
    String username = accessIdentityManagement.getCurrentUsername();
    if (username != null)
    {
      String role = accessIdentityManagement.getRole(username);
      return LmsRole.valueOf(role);
    }

    return null;
  }
}

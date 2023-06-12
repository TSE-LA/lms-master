package mn.erin.lms.aim.shiro;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.support.DefaultSubjectContext;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;

import mn.erin.domain.aim.constant.AimConstants;
import mn.erin.domain.aim.service.AuthenticationService;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class ShiroAuthenticationService implements AuthenticationService
{

  @Override
  public String authenticate(String tenantId, String userId, String password)
  {
    Session previousSession = getPreviousSession(userId);

    Subject currentUser = SecurityUtils.getSubject();

    if (!currentUser.isAuthenticated() || currentUser.getPrincipals() != null)
    {
      currentUser.login(new UsernamePasswordToken(userId, password));
    }

    if (previousSession != null)
    {
      previousSession.stop();
    }

    Session session = currentUser.getSession();
    session.setAttribute(AimConstants.SESSION_ATTR_TENANT_ID, tenantId);

    return (String) session.getId();
  }

  @Override
  public String getToken()
  {
    return (String) SecurityUtils.getSubject().getSession().getId();
  }

  @Override
  public boolean isCurrentUserAuthenticated()
  {
    return SecurityUtils.getSubject().isAuthenticated();
  }

  @Override
  public String getCurrentUsername()
  {
    Subject currentUser = SecurityUtils.getSubject();
    return (String) currentUser.getPrincipal();
  }

  @Override
  public String logout()
  {
    SecurityUtils.getSubject().logout();
    return null;
  }

  private Session getPreviousSession(String userId)
  {
    DefaultWebSecurityManager defaultWebSecurityManager = (DefaultWebSecurityManager) SecurityUtils.getSecurityManager();
    DefaultWebSessionManager defaultSessionManager = (DefaultWebSessionManager) defaultWebSecurityManager.getSessionManager();

    for (Session session : defaultSessionManager.getSessionDAO().getActiveSessions())
    {
      SimplePrincipalCollection principal = (SimplePrincipalCollection) session.getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY);
      if (principal != null && userId.equals(principal.getPrimaryPrincipal()))
      {
        return session;
      }
    }

    return null;
  }
}

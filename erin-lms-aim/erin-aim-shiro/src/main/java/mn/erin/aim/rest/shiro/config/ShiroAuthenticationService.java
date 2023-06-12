/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.aim.rest.shiro.config;

import mn.erin.domain.aim.constant.AimConstants;
import mn.erin.domain.aim.service.AuthenticationService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.support.DefaultSubjectContext;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author EBazarragchaa
 */
public class ShiroAuthenticationService implements AuthenticationService
{
  private static final Logger LOGGER = LoggerFactory.getLogger(ShiroAuthenticationService.class);
  private final boolean killPreviousSession;

  public ShiroAuthenticationService()
  {
    this(false);
  }

  public ShiroAuthenticationService(boolean killPreviousSession)
  {
    this.killPreviousSession = killPreviousSession;
  }

  @Override
  public String authenticate(String tenantId, String username, String password)
  {
    Validate.notBlank(tenantId, "tenantId is missing!");
    Validate.notBlank(username, "username is missing!");
    Validate.notBlank(password, "password is missing!");

    Subject currentUser = SecurityUtils.getSubject();
    if (!currentUser.isAuthenticated() || currentUser.getPrincipals() != null)
    {
      LOGGER.info("Logging user [tenant={}, username={}]", tenantId, username);
      currentUser.login(new AimAuthenticationToken(tenantId, username, password));
      Session session = getCurrentSession();
      if (null != session)
      {
        session.setAttribute(AimConstants.SESSION_ATTR_TENANT_ID, tenantId);
      }
    }

    if (killPreviousSession)
    {
      Session previousSession = getPreviousSession(tenantId, username);
      // in case of successful login remove old session
      if (previousSession != null)
      {
        LOGGER.warn("Removing old session id={} for user [tenant={}, username={}]", previousSession.getId(), tenantId, username);
        previousSession.stop();
      }
    }

    Session session = getCurrentSession();

    if (null != session)
    {
      return (String) session.getId();
    }
    return null;
  }

  @Override
  public String logout()
  {
    Subject currentUser = SecurityUtils.getSubject();
    String username = getCurrentUsername();
    if (currentUser.isAuthenticated())
    {
      currentUser.logout();
    }

    return username;
  }

  @Override
  public String getCurrentUsername()
  {
    Subject currentUser = SecurityUtils.getSubject();
    return (String) currentUser.getPrincipal();
  }

  @Override
  public boolean isCurrentUserAuthenticated()
  {
    return SecurityUtils.getSubject().isAuthenticated();
  }

  @Override
  public String getToken()
  {
    return (String) SecurityUtils.getSubject().getSession().getId();
  }

  private Session getPreviousSession(String tenantId, String username)
  {
    DefaultWebSecurityManager defaultWebSecurityManager = (DefaultWebSecurityManager) SecurityUtils.getSecurityManager();
    DefaultWebSessionManager defaultSessionManager = (DefaultWebSessionManager) defaultWebSecurityManager.getSessionManager();

    for (Session session : defaultSessionManager.getSessionDAO().getActiveSessions())
    {
      SimplePrincipalCollection principal = (SimplePrincipalCollection) session.getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY);
      String sessionTenantId = getCurrentTenantId();
      if (principal != null && username.equals(principal.getPrimaryPrincipal())
          && !StringUtils.isBlank(sessionTenantId) && sessionTenantId.equals(tenantId))
      {
        return session;
      }
    }

    return null;
  }

  private Session getCurrentSession()
  {
    try
    {
      return SecurityUtils.getSubject().getSession();
    }
    catch (IllegalArgumentException e)
    {
      return null;
    }
  }

  private String getCurrentTenantId()
  {
    Session session = getCurrentSession();
    if (null != session)
    {
      return (String) session.getAttribute(AimConstants.SESSION_ATTR_TENANT_ID);
    }
    else
    {
      return null;
    }
  }
}

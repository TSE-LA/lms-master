package mn.erin.lms.aim.shiro;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.UnavailableSecurityManagerException;
import org.apache.shiro.session.InvalidSessionException;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.aim.constant.AimConstants;
import mn.erin.domain.aim.service.TenantIdProvider;

/**
 * @author EBazarragchaa
 */
public class ShiroTenantIdProvider implements TenantIdProvider
{
  private static final Logger LOG = LoggerFactory.getLogger(ShiroTenantIdProvider.class);

  @Override
  public String getCurrentUserTenantId()
  {
    try
    {
      Subject currentUser = SecurityUtils.getSubject();
      return (String) currentUser.getSession().getAttribute(AimConstants.SESSION_ATTR_TENANT_ID);
    }
    catch (InvalidSessionException | UnavailableSecurityManagerException e)
    {
      LOG.trace("No session available, tenant ID cannot be retrieved", e);
    }
    return null;
  }

  @Override
  public String getUserTenantId(String userId)
  {
    throw new UnsupportedOperationException();
  }
}
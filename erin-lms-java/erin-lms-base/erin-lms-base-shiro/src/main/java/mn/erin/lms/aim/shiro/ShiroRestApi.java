package mn.erin.lms.aim.shiro;

import java.util.ArrayList;
import java.util.List;

import io.swagger.annotations.Api;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.support.DefaultSubjectContext;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import mn.erin.infrastucture.rest.common.response.RestResponse;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Api("Sessions")
@RequestMapping(value = "/sessions")
public class ShiroRestApi
{
  @GetMapping
  public ResponseEntity readAll()
  {
    DefaultWebSecurityManager defaultWebSecurityManager = (DefaultWebSecurityManager) SecurityUtils.getSecurityManager();
    DefaultWebSessionManager defaultSessionManager = (DefaultWebSessionManager) defaultWebSecurityManager.getSessionManager();

    List<String> activeUsers = new ArrayList<>();

    for (Session session : defaultSessionManager.getSessionDAO().getActiveSessions())
    {
      SimplePrincipalCollection principal = (SimplePrincipalCollection) session.getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY);
      activeUsers.add((String) principal.getPrimaryPrincipal());
    }

    ActiveSessionsResponse response = new ActiveSessionsResponse();
    response.setActiveUsers(activeUsers);
    response.setActiveUsersCount(activeUsers.size());

    return RestResponse.success(response);
  }
}

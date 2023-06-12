package mn.erin.lms.unitel.test;

import javax.inject.Inject;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.SecurityManager;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import mn.erin.domain.aim.service.AuthenticationService;

/**
 * @author Bat-Erdene Tsogoo.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ShiroTestBeanConfig.class })
public class AuthenticationTest
{
  @Inject
  private AuthenticationService authenticationService;

  @Inject
  private SecurityManager securityManager;

  @Test
  @Ignore
  public void authenticate()
  {
    SecurityUtils.setSecurityManager(securityManager);
    String token = authenticationService.authenticate("erin", "test", "test");
    Assert.assertNotNull(token);
  }
}

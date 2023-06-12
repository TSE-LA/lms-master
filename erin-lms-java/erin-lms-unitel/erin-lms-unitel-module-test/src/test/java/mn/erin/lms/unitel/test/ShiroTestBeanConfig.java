package mn.erin.lms.unitel.test;

import mn.erin.lms.aim.shiro.ShiroBeanConfig;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.realm.ldap.DefaultLdapRealm;
import org.apache.shiro.realm.ldap.JndiLdapContextFactory;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.context.annotation.Bean;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class ShiroTestBeanConfig extends ShiroBeanConfig
{
  @Bean
  public Realm realm()
  {
    DefaultLdapRealm realm = new DefaultLdapRealm();
    JndiLdapContextFactory contextFactory = new JndiLdapContextFactory();
    contextFactory.setUrl("ldap://erin.systems:389");
    contextFactory.setSystemUsername("CN=admin,DC=erin,DC=systems");
    contextFactory.setSystemPassword("Erin@Admin");

    realm.setContextFactory(contextFactory);
    realm.setUserDnTemplate("uid={0},DC=test_users,DC=erin,DC=systems");

    return realm;
  }

  @Bean
  public SecurityManager securityManager()
  {
    DefaultWebSecurityManager manager = new DefaultWebSecurityManager();
    manager.setRealm(realm());
    DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
    sessionManager.setSessionIdUrlRewritingEnabled(false);
    manager.setSessionManager(sessionManager);
    return manager;
  }
}

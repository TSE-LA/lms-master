package mn.erin.lms.aim.shiro;

import javax.inject.Inject;

import org.apache.shiro.authc.credential.PasswordService;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.config.ShiroAnnotationProcessorConfiguration;
import org.apache.shiro.spring.config.ShiroBeanConfiguration;
import org.apache.shiro.spring.web.config.ShiroWebConfiguration;
import org.apache.shiro.spring.web.config.ShiroWebFilterConfiguration;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import mn.erin.domain.aim.service.PasswordEncryptService;
import mn.erin.lms.aim.shiro.realm.RealmFactory;
import mn.erin.lms.aim.shiro.realm.RealmFactoryImpl;
import mn.erin.lms.aim.shiro.realm.RealmType;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Configuration
@Import({ ShiroBeanConfiguration.class,
    ShiroAnnotationProcessorConfiguration.class,
    ShiroWebConfiguration.class,
    ShiroWebFilterConfiguration.class })
public class ShiroBeanConfig
{
  private static final Logger LOGGER = LoggerFactory.getLogger(ShiroBeanConfig.class);
  private static final long MINUTE = (long) 60 * 1000;

  @Inject
  BeanFactory beanFactory;

  @Bean
  public ShiroRestApi shiroRestApi()
  {
    return new ShiroRestApi();
  }

  @Bean
  public SecurityManager securityManager()
  {
    DefaultWebSecurityManager manager = new DefaultWebSecurityManager(realm());
    DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
    sessionManager.setSessionIdUrlRewritingEnabled(false);
    long sessionTimeout;
    try
    {
      sessionTimeout = Long.parseLong(System.getProperty("session.timeout", "30")) * MINUTE;
      sessionManager.setGlobalSessionTimeout(sessionTimeout);
    }
    catch (NumberFormatException e)
    {
      LOGGER.error("Invalid session timeout variable could not setting using default, cause: {}", e.getMessage());
    }
    manager.setSessionManager(sessionManager);
    return manager;
  }

  @Bean
  public Realm realm()
  {
    return realmFactory().getRealm(realmType());
  }

  @Bean
  public RealmFactory realmFactory()
  {
    return new RealmFactoryImpl(beanFactory);
  }

  @Bean
  public RealmType realmType()
  {
    try
    {
      return RealmType.valueOf(System.getProperty("REALM_TYPE", "mongo").toUpperCase());
    }
    catch (IllegalArgumentException ignored)
    {
      return RealmType.MONGO;
    }
  }

  @Bean
  public PasswordService passwordService()
  {
    return new BCryptPasswordService();
  }

  @Bean
  public PasswordEncryptService passwordEncryptService()
  {
    return (PasswordEncryptService) passwordService();
  }
}

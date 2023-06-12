/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.aim.rest.shiro.config;

import javax.inject.Inject;

import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.aim.service.PermissionService;
import mn.erin.domain.aim.service.TenantIdProvider;
import org.apache.shiro.authc.credential.PasswordService;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.config.ShiroAnnotationProcessorConfiguration;
import org.apache.shiro.spring.config.ShiroBeanConfiguration;
import org.apache.shiro.spring.web.config.DefaultShiroFilterChainDefinition;
import org.apache.shiro.spring.web.config.ShiroFilterChainDefinition;
import org.apache.shiro.spring.web.config.ShiroWebConfiguration;
import org.apache.shiro.spring.web.config.ShiroWebFilterConfiguration;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Configuration
@PropertySource("classpath:shiro.properties")
@Import({ ShiroBeanConfiguration.class,
  ShiroAnnotationProcessorConfiguration.class,
  ShiroWebConfiguration.class,
  ShiroWebFilterConfiguration.class })
public abstract class ShiroBaseBeanConfig
{
  private static final Logger LOGGER = LoggerFactory.getLogger(ShiroBaseBeanConfig.class);

  @Bean
  @Inject
  SecurityManager securityManager(Realm realm)
  {
    DefaultWebSecurityManager manager = new DefaultWebSecurityManager();
    manager.setRealm(realm);
    return manager;
  }

  @Bean
  @Inject
  ShiroFilterChainDefinition shiroFilterChainDefinition(Environment environment)
  {
    // Always remember to define your filter chains based on a FIRST MATCH WINS policy!
    DefaultShiroFilterChainDefinition definition = new DefaultShiroFilterChainDefinition();

    definition.addPathDefinition("/aim/login", "anon");
    definition.addPathDefinition("swagger-ui.html", "anon");

    String[] resources = environment.getProperty("secure.resources").split(",");
    for (String resource : resources)
    {
      definition.addPathDefinition("/" + resource, "authc");
      LOGGER.info("### secure resource: {}", resource);
    }

    definition.addPathDefinition("/**", "anon");
    return definition;
  }

  @Bean
  AuthenticationService authenticationService()
  {
    return new ShiroAuthenticationService();
  }

  @Bean
  AuthorizationService authorizationService()
  {
    return new ShiroAuthorizationService();
  }

  @Bean
  PermissionService permissionService()
  {
    return new ShiroPermissionService();
  }

  @Bean
  TenantIdProvider tenantIdProvider()
  {
    return new ShiroTenantIdProvider();
  }

  @Bean
  PasswordService passwordService()
  {
    return new BCryptPasswordService();
  }
}

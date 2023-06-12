package mn.erin.lms.jarvis.server.config;

import org.apache.shiro.spring.config.ShiroAnnotationProcessorConfiguration;
import org.apache.shiro.spring.config.ShiroBeanConfiguration;
import org.apache.shiro.spring.web.config.DefaultShiroFilterChainDefinition;
import org.apache.shiro.spring.web.config.ShiroFilterChainDefinition;
import org.apache.shiro.spring.web.config.ShiroWebConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import mn.erin.lms.aim.shiro.SecureResources;
import mn.erin.lms.aim.shiro.ShiroBeanConfig;
import mn.erin.lms.jarvis.shiro.JarvisShiroWebFilterConfiguration;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Configuration
@Import({ ShiroBeanConfiguration.class,
    ShiroAnnotationProcessorConfiguration.class,
    ShiroWebConfiguration.class,
    ShiroBeanConfig.class,
    JarvisShiroWebFilterConfiguration.class })
public class JarvisShiroBeanConfig
{

  @Bean
  ShiroFilterChainDefinition shiroFilterChainDefinition()
  {
    // Always remember to define your filter chains based on a FIRST MATCH WINS policy!
    DefaultShiroFilterChainDefinition definition = new DefaultShiroFilterChainDefinition();

    addPathDefinition(definition, "lms_admin", SecureResources.getAdminResources());
    addPathDefinition(definition, "authority", SecureResources.getAuthorityResources());
    addPathDefinition(definition, "secure", SecureResources.getSecureResources());
    addPathDefinition(definition, "anon", SecureResources.getAnonResources());

    return definition;
  }

  private void addPathDefinition(DefaultShiroFilterChainDefinition definition, String def, String... resources)
  {
    for (String resource : resources)
    {
      definition.addPathDefinition("/" + resource, def);
    }
  }
}

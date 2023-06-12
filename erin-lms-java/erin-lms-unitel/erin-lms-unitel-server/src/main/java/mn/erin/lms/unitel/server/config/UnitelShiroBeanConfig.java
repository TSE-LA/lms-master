package mn.erin.lms.unitel.server.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
import mn.erin.lms.unitel.shiro.UnitelShiroWebFilterConfiguration;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Configuration
@Import({ ShiroBeanConfiguration.class,
    ShiroAnnotationProcessorConfiguration.class,
    ShiroWebConfiguration.class,
    ShiroBeanConfig.class,
    RepositoryBeanConfig.class,
    UnitelShiroWebFilterConfiguration.class })
public class UnitelShiroBeanConfig
{
  @Bean
  ShiroFilterChainDefinition shiroFilterChainDefinition()
  {
    // Always remember to define your filter chains based on a FIRST MATCH WINS policy!
    DefaultShiroFilterChainDefinition definition = new DefaultShiroFilterChainDefinition();

    addPathDefinition(definition, "lms_admin", SecureResources.getAdminResources());

    List<String> authorityResources = new ArrayList<>(Arrays.asList(SecureResources.getAuthorityResources()));
    authorityResources.add("legacy/lms/readerships/move");
    authorityResources.add("legacy/lms/promo-report/promo-excel");
    authorityResources.add("legacy/lms/promo-report/download-promotion-report");
    authorityResources.add("legacy/lms/readerships");

    addPathDefinition(definition, "authority", authorityResources.toArray(new String[] {}));

    List<String> secureResource = new ArrayList<>(Arrays.asList(SecureResources.getSecureResources()));
    secureResource.add("legacy/course-notifications/**");

    addPathDefinition(definition, "secure", secureResource.toArray(new String[] {}));

    addPathDefinition(definition, "anon", SecureResources.getAnonResources());

    addPathDefinition(definition, "promo",
        "legacy/courses/**",
        "legacy/promotion-category/**",
        "legacy/course-contents/**",
        "legacy/course-assessments/**",
        "legacy/lms/course-report",
        "legacy/lms/course-activity/**",
        "legacy/lms/promotion-relations/**",
        "legacy/lms/promo-report/**",
        "legacy/lms/promotion-statistics/**",
        "legacy/lms/employee-analytics/**",
        "legacy/lms/readerships/**");
    addPathDefinition(definition, "scorm", "legacy/scorm-contents/**");

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

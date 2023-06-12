package mn.erin.lms.unitel.shiro;

import java.util.Map;
import javax.inject.Inject;
import javax.servlet.Filter;

import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.lms.aim.shiro.filter.CustomShiroFilter;
import mn.erin.lms.aim.shiro.filter.LmsAdminOnlyFilter;
import mn.erin.lms.aim.shiro.filter.LmsAuthorityFilter;
import mn.erin.lms.aim.shiro.filter.SCORMFilter;
import mn.erin.lms.base.aim.AccessIdentityManagement;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.spring.web.config.AbstractShiroWebFilterConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Configuration
public class UnitelShiroWebFilterConfiguration extends AbstractShiroWebFilterConfiguration
{
  @Inject
  private AccessIdentityManagement accessIdentityManagement;

  @Inject
  private AuthenticationService authenticationService;

  @Bean
  @Override
  protected ShiroFilterFactoryBean shiroFilterFactoryBean()
  {
    ShiroFilterFactoryBean filterFactoryBean = new ShiroFilterFactoryBean();

    filterFactoryBean.setLoginUrl("/login");
    filterFactoryBean.setSuccessUrl(super.successUrl);
    filterFactoryBean.setUnauthorizedUrl(super.unauthorizedUrl);
    filterFactoryBean.setSecurityManager(super.securityManager);
    filterFactoryBean.setFilterChainDefinitionMap(super.shiroFilterChainDefinition.getFilterChainMap());

    Map<String, Filter> filters = filterFactoryBean.getFilters();
    filters.put("secure", new CustomShiroFilter(accessIdentityManagement, authenticationService));
    filters.put("lms_admin", new LmsAdminOnlyFilter(accessIdentityManagement, authenticationService));
    filters.put("authority", new LmsAuthorityFilter(accessIdentityManagement, authenticationService));
    filters.put("promo", new PromotionFilter(accessIdentityManagement, authenticationService));
    filters.put("scorm", new SCORMFilter(accessIdentityManagement, authenticationService));

    return filterFactoryBean;
  }
}

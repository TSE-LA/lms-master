package mn.erin.lms.base.analytics.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import mn.erin.lms.base.analytics.repository.AnalyticsRepositoryRegistry;
import mn.erin.lms.base.analytics.repository.AnalyticsRepositoryRegistryImpl;

/**
 * @author Munkh
 */
@Configuration
public class AnalyticsRepositoryBeanConfig
{
  @Bean
  public AnalyticsRepositoryRegistry analyticsRepositoryRegistry()
  {
    return new AnalyticsRepositoryRegistryImpl();
  }
}

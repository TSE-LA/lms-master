package mn.erin.aim.repository.memory;

import mn.erin.domain.aim.repository.AimRepositoryRegistry;
import mn.erin.domain.aim.service.AimApplicationDataChecker;
import mn.erin.domain.aim.service.TenantIdProvider;
import mn.erin.domain.aim.service.UserAggregateService;
import mn.erin.domain.aim.service.UserAggregateServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("mn.erin.aim.repository.memory")
public class MemoryBasedAimRepositoryBeanConfig
{
  @Bean
  public UserAggregateService userAggregateService(AimRepositoryRegistry aimRepositoryRegistry, TenantIdProvider tenantIdProvider, AimApplicationDataChecker dataChecker)
  {
    return new UserAggregateServiceImpl(aimRepositoryRegistry, tenantIdProvider, dataChecker);
  }
}

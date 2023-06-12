package mn.erin.aim.repository.mongo;

import javax.inject.Inject;

import com.mongodb.MongoClient;
import mn.erin.domain.aim.repository.AimRepositoryRegistry;
import mn.erin.domain.aim.service.AimApplicationDataChecker;
import mn.erin.domain.aim.service.TenantIdProvider;
import mn.erin.domain.aim.service.UserAggregateService;
import mn.erin.domain.aim.service.UserAggregateServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Configuration
@EnableMongoRepositories(value = "mn.erin.aim.repository.mongo", mongoTemplateRef = "aimMongoTemplate")
@ComponentScan("mn.erin.aim.repository.mongo")
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class AimMongoBeanConfig
{
  @Inject // Mongo client provided by application
  private MongoClient mongoClient;

  @Bean
  public MongoTemplate aimMongoTemplate()
  {
    return new MongoTemplate(mongoClient, "AIM");
  }

  @Bean
  public UserAggregateService userAggregateService(AimRepositoryRegistry aimRepositoryRegistry, TenantIdProvider tenantIdProvider, AimApplicationDataChecker dataChecker)
  {
    return new UserAggregateServiceImpl(aimRepositoryRegistry, tenantIdProvider, dataChecker);
  }
}

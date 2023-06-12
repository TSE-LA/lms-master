package mn.erin.lms.base.test.mongo;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import mn.erin.lms.base.aim.AuthorIdProvider;
import mn.erin.lms.base.domain.service.CourseTypeResolver;
import mn.erin.lms.base.test.DummyCourseTypeResolver;
import mn.erin.lms.base.test.TestAuthorIdProvider;

/**
 * @author Bat-Erdene Tsogoo.
 */
@PropertySource("classpath:test-mongo.properties")
@Configuration
@EnableMongoRepositories(basePackages = "mn.erin.lms.base.mongo.repository")
public class MongoTestBeanConfig extends AbstractMongoConfiguration
{
  @Value("${lms.mongodb.database}")
  private String database;

  @Value("${lms.mongodb.host}")
  private String host;

  @Value("${lms.mongodb.port}")
  private String port;

  @NotNull
  @Override
  @Bean
  public MongoClient mongoClient()
  {
    MongoClientOptions.Builder options = MongoClientOptions.builder()
        .connectionsPerHost(4)
        .maxConnectionIdleTime((60 * 1_000))
        .maxConnectionLifeTime((120 * 1_000));
    MongoClientURI uri = new MongoClientURI("mongodb://" + host + ":" + port, options);

    return new MongoClient(uri);
  }

  @NotNull
  @Override
  protected String getDatabaseName()
  {
    return this.database;
  }

  @Bean
  public AuthorIdProvider authorIdProvider()
  {
    return new TestAuthorIdProvider();
  }

  @Bean
  public CourseTypeResolver courseTypeResolver()
  {
    return new DummyCourseTypeResolver();
  }
}

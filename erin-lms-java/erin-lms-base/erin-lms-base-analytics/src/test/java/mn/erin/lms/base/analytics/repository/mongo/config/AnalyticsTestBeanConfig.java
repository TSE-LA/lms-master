package mn.erin.lms.base.analytics.repository.mongo.config;

import java.util.Objects;
import javax.inject.Inject;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import mn.erin.lms.base.analytics.config.AnalyticsBeanConfig;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

/**
 * @author Munkh
 */
@Import({AnalyticsBeanConfig.class})
@PropertySource("classpath:report-mongo.properties")
public class AnalyticsTestBeanConfig
{

  @Inject
  private Environment env;

  @NotNull
  @Bean
  public MongoClient mongoClient()
  {
    CodecRegistry pojoCodecRegistry = fromRegistries(MongoClient.getDefaultCodecRegistry(),
        fromProviders(PojoCodecProvider.builder().automatic(true).build()));

    MongoClientOptions.Builder options = MongoClientOptions.builder()
        .connectionsPerHost(200)
        .codecRegistry(pojoCodecRegistry)
        .maxConnectionIdleTime((60 * 1_000))
        .maxConnectionLifeTime((120 * 1_000));
    MongoClientURI uri = new MongoClientURI("mongodb://" + env.getProperty("lms.mongodb.host") + ":" + Integer.parseInt(
        Objects.requireNonNull(env.getProperty("lms.mongodb.port"))), options);

    return new MongoClient(uri);
  }
}

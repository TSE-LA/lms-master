/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.infrastructure.scorm.repository.mongo;

import com.mongodb.MongoClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;

import mn.erin.lms.base.mongo.config.BaseLmsMongoBeanConfig;
import mn.erin.lms.legacy.domain.scorm.factory.DataModelFactory;
import mn.erin.lms.legacy.domain.scorm.repository.RuntimeDataRepository;
import mn.erin.lms.legacy.infrastructure.scorm.base.factory.DataModelFactoryImpl;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Configuration
@Import({ BaseLmsMongoBeanConfig.class })
public class MongoSCORMBeanConfig
{
  private final MongoTemplate mongoTemplate;

  public MongoSCORMBeanConfig(MongoClient mongoClient)
  {
    mongoTemplate = new MongoTemplate(mongoClient, "SCORM");
  }

  @Bean(name = "legacyDataModelFactory")
  public DataModelFactory dataModelFactory()
  {
    return new DataModelFactoryImpl();
  }

  @Bean(name = "legacyRuntimeDataRepo")
  public RuntimeDataRepository runtimeDataRepository()
  {
    return new MongoRuntimeDataRepository(mongoTemplate, dataModelFactory());
  }

  @Bean(name = "legacyRuntimeDataStore")
  public RuntimeDataRepository runtimeDataStore()
  {
    return new RuntimeDataStore(mongoTemplate, dataModelFactory());
  }
}

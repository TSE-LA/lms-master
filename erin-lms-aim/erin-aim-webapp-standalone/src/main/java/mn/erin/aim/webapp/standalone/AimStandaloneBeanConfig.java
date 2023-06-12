/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.aim.webapp.standalone;

import javax.inject.Inject;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import mn.erin.aim.config.AimBeanConfig;
import mn.erin.aim.config.AimConfigProviderImpl;
import mn.erin.aim.repository.mongo.AimMongoBeanConfig;
import mn.erin.aim.repository.registry.spring.AimRepositoryRegistryImpl;
import mn.erin.aim.rest.config.AimRestBeanConfig;
import mn.erin.aim.rest.shiro.config.AimRealmShiroBeanConfig;
import mn.erin.domain.aim.repository.AimFileSystem;
import mn.erin.domain.aim.repository.AimRepositoryRegistry;
import mn.erin.domain.aim.repository.AlfrescoAimFileSystem;
import mn.erin.domain.aim.service.AimApplicationDataChecker;
import mn.erin.domain.aim.service.AimConfigProvider;
import mn.erin.domain.aim.service.DummyApplicationDataChecker;
import mn.erin.domain.aim.usecase.user.CreateInitialAdminUser;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.dms.repository.DocumentRepository;
import mn.erin.domain.dms.repository.FolderRepository;
import mn.erin.rest.swagger.ui.SwaggerConfiguration;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

@Configuration
@EnableWebMvc
@Import({
  AimRestBeanConfig.class,
  AimRealmShiroBeanConfig.class,
  SwaggerConfiguration.class,
  AimMongoBeanConfig.class,
  AimBeanConfig.class
})
@ComponentScan(basePackages = "mn.erin.aim.webapp.standalone")
public class AimStandaloneBeanConfig implements WebMvcConfigurer
{
  private FolderRepository folderRepository;
  private DocumentRepository documentRepository;

  @Inject
  public void setFolderRepository(FolderRepository folderRepository)
  {
    this.folderRepository = folderRepository;
  }

  @Inject
  public void setDocumentRepository(DocumentRepository documentRepository)
  {
    this.documentRepository = documentRepository;
  }

  @Inject
  ApplicationContext context;

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry)
  {
    registry.addResourceHandler("swagger-ui.html")
      .addResourceLocations("classpath:/META-INF/resources/");

    registry.addResourceHandler("/webjars/**")
      .addResourceLocations("classpath:/META-INF/resources/webjars/");
  }

  @Bean
  public MongoClient mongoClient(Environment env)
  {
    CodecRegistry pojoCodecRegistry =
      fromRegistries(MongoClient.getDefaultCodecRegistry(),
        fromProviders(PojoCodecProvider.builder().automatic(true).build()));
    MongoClientOptions.Builder options = MongoClientOptions.builder().codecRegistry(pojoCodecRegistry);

    String mongoDBHost = env.getProperty("mongodb.host", "localhost:27017");
    return new MongoClient(new MongoClientURI("mongodb://" + mongoDBHost, options));
  }

  @Bean
  public AimApplicationDataChecker aimApplicationDataChecker()
  {
    return new DummyApplicationDataChecker();
  }

  @Bean
  public AimRepositoryRegistry aimRepositoryRegistry()
  {
    return new AimRepositoryRegistryImpl();
  }

  @Bean
  public AimConfigProvider aimConfigProvider()
  {
    return new AimConfigProviderImpl();
  }

  @Bean(name = "multipartResolver")
  public CommonsMultipartResolver multipartResolver()
  {
    CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
    // 300MB
    multipartResolver.setMaxUploadSize(5000000000L);
    return multipartResolver;
  }

  @Bean
  public CreateInitialAdminUser createInitialAdminUser() throws UseCaseException
  {
    // run once bean
    CreateInitialAdminUser createInitialAdmin = new CreateInitialAdminUser(aimRepositoryRegistry(), aimConfigProvider());
    createInitialAdmin.execute(null);
    return createInitialAdmin;
  }

  @Bean
  public AimFileSystem aimFileSystem()
  {
    return new AlfrescoAimFileSystem(documentRepository, folderRepository);
  }
}

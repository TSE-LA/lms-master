package mn.erin.lms.jarvis.server.config;

import java.util.Locale;
import java.util.ResourceBundle;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.mapping.event.ValidatingMongoEventListener;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import mn.erin.lms.base.analytics.config.AnalyticsBeanConfig;
import mn.erin.lms.jarvis.LmsSetupListener;
import mn.erin.lms.jarvis.rest.config.JarvisLmsRestBeanConfig;
import mn.erin.rest.swagger.ui.SwaggerConfiguration;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Configuration
@EnableWebMvc
@EnableScheduling
@Import({
    JarvisLmsRepositoryBeanConfig.class,
    JarvisServiceBeanConfig.class,
    JarvisLmsRestBeanConfig.class,
    JarvisAimBeanConfig.class,
    JarvisShiroBeanConfig.class,
    AnalyticsBeanConfig.class,
    SwaggerConfiguration.class })
public class JarvisServerBeanConfig implements WebMvcConfigurer
{
  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry)
  {
    registry.addResourceHandler("swagger-ui.html")
        .addResourceLocations("classpath:/META-INF/resources/");

    registry.addResourceHandler("/webjars/**")
        .addResourceLocations("classpath:/META-INF/resources/webjars/");

    registry.addResourceHandler("/assets/**")
        .addResourceLocations("classpath:/assets/");

    registry.addResourceHandler("/errors/**")
        .addResourceLocations("classpath:/errors/");

    registry.addResourceHandler("/alfresco/**")
        .addResourceLocations("file:///Z:/");

    registry.addResourceHandler("/client/**")
        .addResourceLocations("/client/");

    registry.addResourceHandler("/old/**")
        .addResourceLocations("/old/");
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
  public ResourceBundle messageSet()
  {
    return ResourceBundle.getBundle("message-set", Locale.forLanguageTag("mn"));
  }

  @Bean
  public LmsSetupListener lmsSetupListener()
  {
    return new LmsSetupListener();
  }

  @Bean
  public ValidatingMongoEventListener validatingMongoEventListener()
  {
    return new ValidatingMongoEventListener(validator());
  }

  @Bean
  public LocalValidatorFactoryBean validator()
  {
    return new LocalValidatorFactoryBean();
  }
}

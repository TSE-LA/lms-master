package mn.erin.lms.jarvis.rest.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;

import mn.erin.domain.aim.repository.GroupRepository;
import mn.erin.domain.aim.service.AimConfigProvider;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.rest.config.BaseLmsRestBeanConfig;
import mn.erin.lms.base.scorm.repository.RuntimeDataRepository;
import mn.erin.lms.jarvis.domain.report.repository.LearnerSuccessRepository;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Configuration
@Import(BaseLmsRestBeanConfig.class)
@ComponentScan({ "mn.erin.lms.jarvis.rest.api" })
public class JarvisLmsRestBeanConfig
{
  @Bean(name = "jarvisGenerateLearnerSuccessScheduler")
  @Scope(value = "singleton")
  public LearnerSuccessGenerateScheduler learnerSuccessGenerateScheduler(
      GroupRepository groupRepository,
      LmsServiceRegistry lmsServiceRegistry,
      RuntimeDataRepository runtimeDataRepository,
      LearnerSuccessRepository learnerSuccessRepository,
      AimConfigProvider aimConfigProvider)
  {
    return new LearnerSuccessGenerateScheduler(groupRepository, lmsServiceRegistry, runtimeDataRepository, learnerSuccessRepository, aimConfigProvider);
  }
}

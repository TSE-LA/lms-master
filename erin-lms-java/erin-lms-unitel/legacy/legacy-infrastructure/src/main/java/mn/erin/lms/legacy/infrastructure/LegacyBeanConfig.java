package mn.erin.lms.legacy.infrastructure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;

import mn.erin.lms.legacy.domain.lms.repository.CourseCategoryRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseRepository;
import mn.erin.lms.legacy.infrastructure.lms.rest.ErinLmsRestBeanConfig;
import mn.erin.lms.legacy.infrastructure.scorm.rest.SCORMRestBeanConfig;
import mn.erin.lms.legacy.infrastructure.scorm.rest.SCORMUseCaseBeanConfig;
import mn.erin.lms.legacy.infrastructure.unitel.rest.LmsRestBeanConfig;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Configuration
@Import({ ErinLmsRestBeanConfig.class, SCORMRestBeanConfig.class, LmsRestBeanConfig.class, SCORMUseCaseBeanConfig.class })
public class LegacyBeanConfig
{
  @Bean(name = "legacyChangePromoStateScheduler")
  @Scope(value = "singleton")
  public ChangePromotionStateScheduler changePromotionStateScheduler(CourseRepository courseRepository, CourseCategoryRepository courseCategoryRepository)
  {
    return new ChangePromotionStateScheduler(courseRepository, courseCategoryRepository);
  }
}

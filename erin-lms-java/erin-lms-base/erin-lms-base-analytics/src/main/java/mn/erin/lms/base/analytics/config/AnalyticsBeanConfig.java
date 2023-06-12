package mn.erin.lms.base.analytics.config;

import javax.inject.Inject;

import com.mongodb.MongoClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import mn.erin.domain.aim.repository.UserIdentityRepository;
import mn.erin.domain.aim.service.AimConfigProvider;
import mn.erin.lms.base.analytics.repository.mongo.CourseAnalyticsRepository;
import mn.erin.lms.base.analytics.repository.mongo.CourseStatisticsRepository;
import mn.erin.lms.base.analytics.repository.mongo.ExamAnalyticsRepository;
import mn.erin.lms.base.analytics.repository.mongo.LearnerActivityRepository;
import mn.erin.lms.base.analytics.repository.mongo.LearnerAnalyticsRepository;
import mn.erin.lms.base.analytics.repository.mongo.LearnerSuccessAnalyticRepository;
import mn.erin.lms.base.analytics.repository.mongo.PromotionAnalyticsRepository;
import mn.erin.lms.base.analytics.repository.mongo.SurveyAnalyticsRepository;
import mn.erin.lms.base.analytics.repository.mongo.constants.MongoCollectionProvider;
import mn.erin.lms.base.analytics.repository.mongo.implementation.AnalyticRepository;
import mn.erin.lms.base.analytics.repository.mongo.implementation.AnalyticRepositoryImpl;
import mn.erin.lms.base.analytics.repository.mongo.implementation.ExamAnalyticsRepositoryImpl;
import mn.erin.lms.base.analytics.repository.mongo.implementation.LearnerSuccessAnalyticRepositoryImpl;
import mn.erin.lms.base.analytics.repository.mongo.implementation.OnlineCourseAnalyticsRepository;
import mn.erin.lms.base.analytics.repository.mongo.implementation.OnlineCourseStatisticsRepository;
import mn.erin.lms.base.analytics.repository.mongo.implementation.OnlineLearnerActivityRepository;
import mn.erin.lms.base.analytics.repository.mongo.implementation.OnlineLearnerAnalyticsRepository;
import mn.erin.lms.base.analytics.repository.mongo.implementation.PromotionAnalyticsRepositoryImpl;
import mn.erin.lms.base.analytics.repository.mongo.implementation.SurveyAnalyticsRepositoryImpl;
import mn.erin.lms.base.analytics.service.UserService;
import mn.erin.lms.base.analytics.service.UserServiceImpl;

/**
 * @author Munkh.
 */
@Configuration
@ComponentScan({ "mn.erin.lms.base.analytics.api" })
@PropertySource("classpath:report-mongo.properties")
@EnableMongoRepositories(basePackages = "mn.erin.lms.base.analytics.repository.mongo")
@Import({ AnalyticsRepositoryBeanConfig.class })
public class AnalyticsBeanConfig
{
  private MongoClient mongoClient;

  private UserIdentityRepository userIdentityRepository;

  private AimConfigProvider aimConfigProvider;

  @Inject
  public void setMongoClient(MongoClient mongoClient)
  {
    this.mongoClient = mongoClient;
  }

  @Inject
  public void setUserIdentityRepository(UserIdentityRepository userIdentityRepository)
  {
    this.userIdentityRepository = userIdentityRepository;
  }

  @Inject
  public void setAimConfigProvider(AimConfigProvider aimConfigProvider)
  {
    this.aimConfigProvider = aimConfigProvider;
  }

  @Bean
  public CourseAnalyticsRepository courseAnalyticsRepository()
  {
    return new OnlineCourseAnalyticsRepository(mongoCollectionProvider(), userService());
  }

  @Bean
  public CourseStatisticsRepository courseStatisticsRepository()
  {
    return new OnlineCourseStatisticsRepository(mongoCollectionProvider(), userService());
  }

  @Bean
  public LearnerAnalyticsRepository learnerAnalyticsRepository()
  {
    return new OnlineLearnerAnalyticsRepository(mongoCollectionProvider(), userService());
  }

  @Bean
  public SurveyAnalyticsRepository surveyAnalyticsRepository()
  {
    return new SurveyAnalyticsRepositoryImpl(mongoCollectionProvider(), userService());
  }

  @Bean
  public PromotionAnalyticsRepository promotionAnalyticsRepository()
  {
    return new PromotionAnalyticsRepositoryImpl(mongoCollectionProvider(), userService());
  }

  @Bean
  public LearnerActivityRepository learnerActivityRepository()
  {
    return new OnlineLearnerActivityRepository(mongoCollectionProvider(), userService());
  }

  @Bean
  public AnalyticRepository analyticRepository()
  {
    return new AnalyticRepositoryImpl(mongoCollectionProvider(), userService());
  }

  @Bean
  public MongoCollectionProvider mongoCollectionProvider()
  {
    return new MongoCollectionProvider(mongoClient);
  }

  @Bean
  public UserService userService()
  {
    return new UserServiceImpl(userIdentityRepository, aimConfigProvider);
  }

  @Bean
  public ExamAnalyticsRepository examAnalyticsRepository()
  {
    return new ExamAnalyticsRepositoryImpl(mongoCollectionProvider(), userService());
  }

  @Bean
  public LearnerSuccessAnalyticRepository learnerSuccessAnalyticRepository()
  {
    return new LearnerSuccessAnalyticRepositoryImpl(mongoCollectionProvider(), userService());
  }

  @Bean(name = "GenerateLearnerSuccessScheduler")
  @Scope(value = "singleton")
  public LearnerSuccessGenerateScheduler learnerSuccessGenerateScheduler(
      LearnerSuccessAnalyticRepository learnerSuccessAnalyticRepository)
  {
    return new LearnerSuccessGenerateScheduler(learnerSuccessAnalyticRepository, aimConfigProvider);
  }
}

package mn.erin.lms.jarvis.mongo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import mn.erin.lms.base.domain.repository.CourseSuggestedUsersRepository;
import mn.erin.lms.base.mongo.config.BaseLmsMongoBeanConfig;
import mn.erin.lms.jarvis.domain.report.repository.AssessmentReportRepository;
import mn.erin.lms.jarvis.domain.report.repository.CourseReportRepository;
import mn.erin.lms.jarvis.domain.report.repository.FieldRepository;
import mn.erin.lms.jarvis.domain.report.repository.LearnerSuccessRepository;
import mn.erin.lms.jarvis.mongo.implementation.AssessmentReportRepositoryImpl;
import mn.erin.lms.jarvis.mongo.implementation.CourseReportRepositoryImpl;
import mn.erin.lms.jarvis.mongo.implementation.CourseSuggestedUsersRepositoryImpl;
import mn.erin.lms.jarvis.mongo.implementation.FieldRepositoryImpl;
import mn.erin.lms.jarvis.mongo.implementation.LearnerSuccessRepositoryImpl;
import mn.erin.lms.jarvis.mongo.repository.MongoAssessmentReportRepository;
import mn.erin.lms.jarvis.mongo.repository.MongoCourseReportRepository;
import mn.erin.lms.jarvis.mongo.repository.MongoCourseSuggestedUsersRepository;
import mn.erin.lms.jarvis.mongo.repository.MongoFieldRepository;
import mn.erin.lms.jarvis.mongo.repository.MongoLearnerSuccessRepository;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Configuration
@Import({ BaseLmsMongoBeanConfig.class })
@EnableMongoRepositories(basePackages = "mn.erin.lms.jarvis.mongo.repository")
public class JarvisLmsMongoBeanConfig
{
  @Bean
  public AssessmentReportRepository assessmentReportRepository(MongoAssessmentReportRepository mongoAssessmentReportRepository)
  {
    return new AssessmentReportRepositoryImpl(mongoAssessmentReportRepository);
  }

  @Bean
  public CourseReportRepository courseReportRepository(MongoCourseReportRepository mongoCourseReportRepository)
  {
    return new CourseReportRepositoryImpl(mongoCourseReportRepository);
  }

  @Bean
  public LearnerSuccessRepository learnerSuccessRepository(MongoLearnerSuccessRepository mongoGroupAverageRepository)
  {
    return new LearnerSuccessRepositoryImpl(mongoGroupAverageRepository);
  }

  @Bean
  public CourseSuggestedUsersRepository courseSuggestedUsersRepository(MongoCourseSuggestedUsersRepository mongoCourseSuggestedUsersRepository)
  {
    return new CourseSuggestedUsersRepositoryImpl(mongoCourseSuggestedUsersRepository);
  }

  @Bean
  public FieldRepository fieldRepository(MongoFieldRepository mongoFieldRepository)
  {
    return new FieldRepositoryImpl(mongoFieldRepository);
  }
}

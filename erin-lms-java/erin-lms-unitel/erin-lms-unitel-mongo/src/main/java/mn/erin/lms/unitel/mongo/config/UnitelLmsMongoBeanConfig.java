package mn.erin.lms.unitel.mongo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import mn.erin.lms.base.domain.repository.CourseSuggestedUsersRepository;
import mn.erin.lms.base.mongo.config.BaseLmsMongoBeanConfig;
import mn.erin.lms.unitel.domain.repository.AssessmentReportRepository;
import mn.erin.lms.unitel.domain.repository.CourseReportRepository;
import mn.erin.lms.unitel.domain.repository.LearnerSuccessRepository;
import mn.erin.lms.unitel.mongo.implementation.AssessmentReportRepositoryImpl;
import mn.erin.lms.unitel.mongo.implementation.CourseReportRepositoryImpl;
import mn.erin.lms.unitel.mongo.implementation.CourseSuggestedUsersRepositoryImpl;
import mn.erin.lms.unitel.mongo.implementation.LearnerSuccessRepositoryImpl;
import mn.erin.lms.unitel.mongo.repository.MongoAssessmentReportRepository;
import mn.erin.lms.unitel.mongo.repository.MongoCourseReportRepository;
import mn.erin.lms.unitel.mongo.repository.MongoCourseSuggestedUsersRepository;
import mn.erin.lms.unitel.mongo.repository.MongoLearnerSuccessRepository;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Configuration
@Import({ BaseLmsMongoBeanConfig.class, UnitelAimMongoBeanConfig.class })
@EnableMongoRepositories(basePackages = "mn.erin.lms.unitel.mongo.repository")
public class UnitelLmsMongoBeanConfig
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
  public CourseSuggestedUsersRepository courseSuggestedUsersRepository(MongoCourseSuggestedUsersRepository mongoCourseSuggestedUsersRepository)
  {
    return new CourseSuggestedUsersRepositoryImpl(mongoCourseSuggestedUsersRepository);
  }

  @Bean
  public LearnerSuccessRepository learnerSuccessRepository(MongoLearnerSuccessRepository mongoGroupAverageRepository)
  {
    return new LearnerSuccessRepositoryImpl(mongoGroupAverageRepository);
  }
}

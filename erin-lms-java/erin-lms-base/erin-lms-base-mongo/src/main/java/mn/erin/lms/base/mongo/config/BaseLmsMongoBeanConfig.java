package mn.erin.lms.base.mongo.config;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Objects;
import javax.inject.Inject;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import org.apache.commons.lang3.StringUtils;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import mn.erin.lms.base.domain.repository.AnnouncementRepository;
import mn.erin.lms.base.domain.repository.AnnouncementRuntimeRepository;
import mn.erin.lms.base.domain.repository.AssessmentRepository;
import mn.erin.lms.base.domain.repository.CertificateRepository;
import mn.erin.lms.base.domain.repository.ClassroomCourseAttendanceRepository;
import mn.erin.lms.base.domain.repository.CourseAssessmentRepository;
import mn.erin.lms.base.domain.repository.CourseCategoryRepository;
import mn.erin.lms.base.domain.repository.CourseContentRepository;
import mn.erin.lms.base.domain.repository.CourseEnrollmentRepository;
import mn.erin.lms.base.domain.repository.CourseRepository;
import mn.erin.lms.base.domain.repository.CourseSuggestedUsersRepository;
import mn.erin.lms.base.domain.repository.EnrollSupervisorRepository;
import mn.erin.lms.base.domain.repository.ExamCategoryRepository;
import mn.erin.lms.base.domain.repository.ExamEnrollmentRepository;
import mn.erin.lms.base.domain.repository.ExamGroupRepository;
import mn.erin.lms.base.domain.repository.ExamRepository;
import mn.erin.lms.base.domain.repository.ExamResultRepository;
import mn.erin.lms.base.domain.repository.ExamRuntimeDataRepository;
import mn.erin.lms.base.domain.repository.LearnerCertificateRepository;
import mn.erin.lms.base.domain.repository.LearnerCourseAssessmentRepository;
import mn.erin.lms.base.domain.repository.LearnerCourseHistoryRepository;
import mn.erin.lms.base.domain.repository.SystemConfigRepository;
import mn.erin.lms.base.domain.repository.QuestionCategoryRepository;
import mn.erin.lms.base.domain.repository.QuestionGroupRepository;
import mn.erin.lms.base.domain.repository.QuestionRepository;
import mn.erin.lms.base.domain.repository.QuestionStateRepository;
import mn.erin.lms.base.domain.repository.QuizRepository;
import mn.erin.lms.base.mongo.implementation.AnnouncementRepositoryImpl;
import mn.erin.lms.base.mongo.implementation.AnnouncementRuntimeRepositoryImpl;
import mn.erin.lms.base.mongo.implementation.AssessmentRepositoryImpl;
import mn.erin.lms.base.mongo.implementation.CertificateRepositoryImpl;
import mn.erin.lms.base.mongo.implementation.ClassroomCourseAttendanceRepositoryImpl;
import mn.erin.lms.base.mongo.implementation.CourseAssessmentRepositoryImpl;
import mn.erin.lms.base.mongo.implementation.CourseCategoryRepositoryImpl;
import mn.erin.lms.base.mongo.implementation.CourseContentRepositoryImpl;
import mn.erin.lms.base.mongo.implementation.CourseEnrollmentRepositoryImpl;
import mn.erin.lms.base.mongo.implementation.CourseRepositoryImpl;
import mn.erin.lms.base.mongo.implementation.CourseSuggestedUsersRepositoryImpl;
import mn.erin.lms.base.mongo.implementation.EnrollSupervisorRepositoryImpl;
import mn.erin.lms.base.mongo.implementation.ExamCategoryRepositoryImpl;
import mn.erin.lms.base.mongo.implementation.ExamEnrollmentRepositoryImpl;
import mn.erin.lms.base.mongo.implementation.ExamGroupRepositoryImpl;
import mn.erin.lms.base.mongo.implementation.ExamRepositoryImpl;
import mn.erin.lms.base.mongo.implementation.ExamResultRepositoryImpl;
import mn.erin.lms.base.mongo.implementation.ExamRuntimeDataRepositoryImpl;
import mn.erin.lms.base.mongo.implementation.LearnerCertificateRepositoryImpl;
import mn.erin.lms.base.mongo.implementation.LearnerCourseAssessmentRepositoryImpl;
import mn.erin.lms.base.mongo.implementation.LearnerCourseHistoryRepositoryImpl;
import mn.erin.lms.base.mongo.implementation.SystemConfigRepositoryImpl;
import mn.erin.lms.base.mongo.implementation.QuestionCategoryRepositoryImpl;
import mn.erin.lms.base.mongo.implementation.QuestionGroupRepositoryImpl;
import mn.erin.lms.base.mongo.implementation.QuestionRepositoryImpl;
import mn.erin.lms.base.mongo.implementation.QuestionStateRepositoryImpl;
import mn.erin.lms.base.mongo.implementation.QuizRepositoryImpl;
import mn.erin.lms.base.mongo.implementation.RuntimeDataRepositoryImpl;
import mn.erin.lms.base.mongo.repository.MongoAnnouncementRepository;
import mn.erin.lms.base.mongo.repository.MongoAnnouncementRuntimeRepository;
import mn.erin.lms.base.mongo.repository.MongoAssessmentRepository;
import mn.erin.lms.base.mongo.repository.MongoCertificateRepository;
import mn.erin.lms.base.mongo.repository.MongoClassroomCourseAttendanceRepository;
import mn.erin.lms.base.mongo.repository.MongoCourseAssessmentRepository;
import mn.erin.lms.base.mongo.repository.MongoCourseCategoryRepository;
import mn.erin.lms.base.mongo.repository.MongoCourseContentRepository;
import mn.erin.lms.base.mongo.repository.MongoCourseEnrollmentRepository;
import mn.erin.lms.base.mongo.repository.MongoCourseRepository;
import mn.erin.lms.base.mongo.repository.MongoCourseSuggestedUsersRepository;
import mn.erin.lms.base.mongo.repository.MongoEnrollSupervisorRepository;
import mn.erin.lms.base.mongo.repository.MongoExamCategoryRepository;
import mn.erin.lms.base.mongo.repository.MongoExamEnrollmentRepository;
import mn.erin.lms.base.mongo.repository.MongoExamGroupRepository;
import mn.erin.lms.base.mongo.repository.MongoExamRepository;
import mn.erin.lms.base.mongo.repository.MongoExamResultRepository;
import mn.erin.lms.base.mongo.repository.MongoExamRuntimeDataRepository;
import mn.erin.lms.base.mongo.repository.MongoLearnerCertificateRepository;
import mn.erin.lms.base.mongo.repository.MongoLearnerCourseAssessmentRepository;
import mn.erin.lms.base.mongo.repository.MongoLearnerCourseHistoryRepository;
import mn.erin.lms.base.mongo.repository.MongoSystemConfigRepository;
import mn.erin.lms.base.mongo.repository.MongoQuestionCategoryRepository;
import mn.erin.lms.base.mongo.repository.MongoQuestionGroupRepository;
import mn.erin.lms.base.mongo.repository.MongoQuestionRepository;
import mn.erin.lms.base.mongo.repository.MongoQuestionStateRepository;
import mn.erin.lms.base.mongo.repository.MongoQuizRepository;
import mn.erin.lms.base.scorm.factory.DataModelFactoryImpl;
import mn.erin.lms.base.scorm.repository.RuntimeDataRepository;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

/**
 * @author Bat-Erdene Tsogoo.
 */
@PropertySource(value = "classpath:lms-mongo.properties")
@Configuration
@EnableMongoRepositories(basePackages = "mn.erin.lms.base.mongo.repository")
public class BaseLmsMongoBeanConfig extends AbstractMongoConfiguration
{

  @Inject
  private Environment env;

  @Bean
  public CourseRepository courseRepository(MongoCourseRepository mongoCourseRepository)
  {
    return new CourseRepositoryImpl(mongoTemplate().getCollection("mongoCourse"), mongoCourseRepository);
  }

  @Bean
  public CourseContentRepository courseContentRepository(MongoCourseContentRepository mongoCourseContentRepository)
  {
    return new CourseContentRepositoryImpl(mongoCourseContentRepository);
  }

  @Bean
  public CourseCategoryRepository courseCategoryRepository(MongoCourseCategoryRepository mongoCourseCategoryRepository)
  {
    return new CourseCategoryRepositoryImpl(mongoCourseCategoryRepository);
  }

  @Bean
  public CourseEnrollmentRepository courseEnrollmentRepository(MongoCourseEnrollmentRepository mongoCourseEnrollmentRepository)
  {
    return new CourseEnrollmentRepositoryImpl(mongoCourseEnrollmentRepository);
  }

  @Bean
  public CourseAssessmentRepository courseAssessmentRepository(MongoCourseAssessmentRepository mongoCourseAssessmentRepository)
  {
    return new CourseAssessmentRepositoryImpl(mongoCourseAssessmentRepository);
  }

  @Bean
  public QuizRepository quizRepository(MongoQuizRepository mongoQuizRepository)
  {
    return new QuizRepositoryImpl(mongoQuizRepository);
  }

  @Bean
  public AssessmentRepository assessmentRepository(MongoAssessmentRepository mongoAssessmentRepository)
  {
    return new AssessmentRepositoryImpl(mongoAssessmentRepository);
  }

  @Bean
  public CertificateRepository certificateRepository(MongoCertificateRepository mongoCertificateRepository, MongoCourseRepository mongoCourseRepository)
  {
    return new CertificateRepositoryImpl(mongoCertificateRepository, mongoCourseRepository);
  }

  @Bean
  public RuntimeDataRepository runtimeDataRepository()
  {
    return new RuntimeDataRepositoryImpl(mongoTemplate().getCollection("SCORM"), new DataModelFactoryImpl());
  }

  @Bean
  public LearnerCertificateRepository learnerCertificateRepository(MongoLearnerCertificateRepository mongoLearnerCertificateRepository)
  {
    return new LearnerCertificateRepositoryImpl(mongoLearnerCertificateRepository);
  }

  @Bean
  public CourseSuggestedUsersRepository courseSuggestedUsersRepository(MongoCourseSuggestedUsersRepository mongoCourseSuggestedUsersRepository)
  {
    return new CourseSuggestedUsersRepositoryImpl(mongoCourseSuggestedUsersRepository);
  }

  @Bean
  public LearnerCourseAssessmentRepository learnerCourseAssessmentRepository(MongoLearnerCourseAssessmentRepository mongoLearnerCourseAssessmentRepository)
  {
    return new LearnerCourseAssessmentRepositoryImpl(mongoLearnerCourseAssessmentRepository);
  }

  @Bean
  public ClassroomCourseAttendanceRepository classroomCourseAttendanceRepository(
      MongoClassroomCourseAttendanceRepository mongoClassroomCourseAttendanceRepository)
  {
    return new ClassroomCourseAttendanceRepositoryImpl(mongoClassroomCourseAttendanceRepository);
  }

  @Bean
  public QuestionRepository questionRepository(MongoQuestionRepository mongoQuestionRepository)
  {
    return new QuestionRepositoryImpl(mongoQuestionRepository);
  }

  @Bean
  public QuestionCategoryRepository questionCategoryRepository(MongoQuestionCategoryRepository mongoQuestionCategoryRepository)
  {
    return new QuestionCategoryRepositoryImpl(mongoQuestionCategoryRepository);
  }

  @Bean
  public QuestionGroupRepository questionGroupRepository(MongoQuestionGroupRepository mongoQuestionGroupRepository)
  {
    return new QuestionGroupRepositoryImpl(mongoQuestionGroupRepository);
  }

  @Bean
  public QuestionStateRepository questionStateRepository(MongoQuestionStateRepository mongoQuestionStateRepository)
  {
    return new QuestionStateRepositoryImpl(mongoQuestionStateRepository);
  }

  @Bean
  public ExamRepository examRepository(MongoExamRepository mongoExamRepository)
  {
    return new ExamRepositoryImpl(mongoExamRepository);
  }

  @Bean
  public ExamGroupRepository examGroupRepository(MongoExamGroupRepository mongoExamGroupRepository)
  {
    return new ExamGroupRepositoryImpl(mongoExamGroupRepository);
  }

  @Bean
  public ExamEnrollmentRepository examEnrollmentRepository(MongoExamEnrollmentRepository mongoExamEnrollmentRepository)
  {
    return new ExamEnrollmentRepositoryImpl(mongoExamEnrollmentRepository);
  }

  @Bean
  public ExamCategoryRepository examCategoryRepository(MongoExamCategoryRepository mongoExamCategoryRepository)
  {
    return new ExamCategoryRepositoryImpl(mongoExamCategoryRepository);
  }

  @Bean
  public ExamResultRepository examResultRepository(MongoExamResultRepository mongoExamResultRepository)
  {
    return new ExamResultRepositoryImpl(mongoExamResultRepository);
  }

  @Bean
  public EnrollSupervisorRepository enrollSupervisorRepository(MongoEnrollSupervisorRepository mongoEnrollSupervisorRepository)
  {
    return new EnrollSupervisorRepositoryImpl(mongoEnrollSupervisorRepository);
  }

  @Bean
  ExamRuntimeDataRepository examRuntimeDataRepository(MongoExamRuntimeDataRepository mongoExamRuntimeDataRepository)
  {
    return new ExamRuntimeDataRepositoryImpl(mongoExamRuntimeDataRepository);
  }

  @Bean
  public SystemConfigRepository systemConfigRepository(MongoSystemConfigRepository mongoSystemConfigRepository)
  {
    return new SystemConfigRepositoryImpl(mongoSystemConfigRepository);
  }

  @Bean
  public LearnerCourseHistoryRepository learnerCourseHistoryRepository(MongoLearnerCourseHistoryRepository mongoLearnerCourseHistoryRepository)
  {
    return new LearnerCourseHistoryRepositoryImpl(mongoLearnerCourseHistoryRepository);
  }

  @Bean
  public AnnouncementRepository announcementRepository(MongoAnnouncementRepository mongoAnnouncementRepository)
  {
    return new AnnouncementRepositoryImpl(mongoAnnouncementRepository);
  }

  @Bean
  public AnnouncementRuntimeRepository announcementRuntimeRepository(MongoAnnouncementRuntimeRepository mongoAnnouncementRuntimeRepository)
  {
    return new AnnouncementRuntimeRepositoryImpl(mongoAnnouncementRuntimeRepository);
  }

  @NotNull
  @Override
  @Bean
  public MongoTemplate mongoTemplate()
  {
    return new MongoTemplate(mongoClient(), getDatabaseName());
  }

  @NotNull
  @Override
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

    return new MongoClient(getMongoURI(options));
  }

  @NotNull
  @Override
  protected String getDatabaseName()
  {
    return Objects.requireNonNull(env.getProperty("lms.mongodb.database"));
  }

  private MongoClientURI getMongoURI(MongoClientOptions.Builder options)
  {
    int port = Integer.parseInt(Objects.requireNonNull(env.getProperty("lms.mongodb.port")));
    String host = Objects.requireNonNull(env.getProperty("lms.mongodb.host"));

    if (!StringUtils.isBlank(env.getProperty("lms.mongodb.username")) && !StringUtils.isBlank(env.getProperty("lms.mongodb.password")))
    {
      String userName = env.getProperty("lms.mongodb.username");
      String password = env.getProperty("lms.mongodb.password");
      try
      {
        String encodedUsername = URLEncoder.encode(userName, "UTF-8");
        String encodedPassword = URLEncoder.encode(password, "UTF-8");
        return new MongoClientURI("mongodb://" + encodedUsername + ":" + encodedPassword + "@" + host + ":" + port + "/?authSource=admin", options);
      }
      catch (UnsupportedEncodingException e)
      {
        throw new IllegalStateException("Mongo username and password cannot be encoded!");
      }
    }

    return new MongoClientURI("mongodb://" + host + ":" + port, options);
  }
}

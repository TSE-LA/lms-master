/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.infrastructure.lms.repository.mongo;

import javax.inject.Inject;

import com.mongodb.MongoClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;

import mn.erin.lms.base.mongo.config.BaseLmsMongoBeanConfig;
import mn.erin.lms.legacy.domain.lms.repository.CourseAssessmentRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseAuditRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseCategoryRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseContentRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseEnrollmentRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseGroupRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseQuestionnaireRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseRepository;
import mn.erin.lms.legacy.domain.lms.repository.CourseTestRepository;
import mn.erin.lms.legacy.domain.lms.service.CourseService;
import mn.erin.lms.legacy.infrastructure.lms.repository.service.MongoCourseService;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Configuration
@Import({ BaseLmsMongoBeanConfig.class })
public class LmsMongoBeanConfig
{
  private static final String LMS_DATABASE = "LMS";
  private MongoTemplate mongoTemplate;

  @Inject
  private MongoClient mongoClient;

  @Bean(name = "legacyCourseRepo")
  public CourseRepository courseRepository()
  {
    return new MongoCourseRepository(getMongoTemplate());
  }

  @Bean(name = "legacyCourseCategoryRepo")
  public CourseCategoryRepository courseCategoryRepository()
  {
    return new MongoCourseCategoryRepository(getMongoTemplate());
  }

  @Bean(name = "legacyCourseContentRepo")
  public CourseContentRepository courseContentRepository()
  {
    return new MongoCourseContentRepository(getMongoTemplate());
  }

  @Bean(name = "legacyCourseService")
  public CourseService courseService()
  {
    return new MongoCourseService(getMongoTemplate());
  }

  @Bean(name = "legacyCourseEnrollment")
  public CourseEnrollmentRepository courseEnrollmentRepository()
  {
    return new MongoCourseEnrollmentRepository(getMongoTemplate());
  }

  @Bean(name = "legacyCourseAssessment")
  public CourseAssessmentRepository mongoCourseAssessmentRepo()
  {
    return new MongoCourseAssessmentRepository(getMongoTemplate());
  }

  @Bean(name = "legacyCourseTestRepo")
  public CourseTestRepository mongoCourseTestRepo()
  {
    return new MongoCourseTestRepository(getMongoTemplate());
  }

  @Bean(name = "legacyCourseQuestionnaireRepo")
  public CourseQuestionnaireRepository mongoCourseQuestionnaireRepository()
  {
    return new MongoCourseQuestionnaireRepository(getMongoTemplate());
  }

  @Bean(name = "legacyCourseGroupRepo")
  public CourseGroupRepository courseGroupRepository()
  {
    return new MongoCourseGroupRepository(getMongoTemplate().getCollection("CourseGroup"));
  }

  @Bean(name = "legacyCourseAuditRepo")
  public CourseAuditRepository courseAuditRepository()
  {
    return new MongoCourseAuditRepository(getMongoTemplate());
  }

  private MongoTemplate getMongoTemplate()
  {
    if (mongoTemplate == null)
    {

      mongoTemplate = new MongoTemplate(mongoClient, LMS_DATABASE);
    }
    return mongoTemplate;
  }
}

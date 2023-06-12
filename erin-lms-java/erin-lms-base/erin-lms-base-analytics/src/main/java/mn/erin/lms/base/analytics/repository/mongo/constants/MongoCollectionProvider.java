package mn.erin.lms.base.analytics.repository.mongo.constants;

import javax.print.Doc;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Value;

/**
 * @author Munkh
 */
public class MongoCollectionProvider
{
  private final MongoClient mongoClient;

  @Value("${lms.mongodb.database.aim}")
  private String databaseAim;

  @Value("${lms.mongodb.database.new}")
  private String databaseNew;

  // only PROMOTION
  @Value("${lms.mongodb.database.legacy}")
  private String databaseLegacy;

  @Value("${lms.mongodb.database.scorm}")
  private String databaseScorm;

  public MongoCollectionProvider(MongoClient mongoClient)
  {
    this.mongoClient = mongoClient;
  }

  public MongoCollection<Document> getMembershipsCollection()
  {
    return mongoClient.getDatabase(databaseAim).getCollection("Memberships");
  }

  public MongoCollection<Document> getGroupsCollection()
  {
    return mongoClient.getDatabase(databaseAim).getCollection("groups");
  }

  public MongoCollection<Document> getCourseCollection()
  {
    return mongoClient.getDatabase(databaseNew).getCollection("mongoCourse");
  }

  public MongoCollection<Document> getCourseEnrollmentCollection()
  {
    return mongoClient.getDatabase(databaseNew).getCollection("mongoCourseEnrollment");
  }

  public MongoCollection<Document> getSCORMCollection()
  {
    return mongoClient.getDatabase(databaseNew).getCollection("SCORM");
  }

  public MongoCollection<Document> getCategoryCollection()
  {
    return mongoClient.getDatabase(databaseNew).getCollection("mongoCourseCategory");
  }

  public MongoCollection<Document> getLearnerCertificateCollection()
  {
    return mongoClient.getDatabase(databaseNew).getCollection("mongoLearnerCertificate");
  }

  public MongoCollection<Document> getLegacySCORMCollection()
  {
    return mongoClient.getDatabase(databaseScorm).getCollection("Run-Time Data");
  }

  public MongoCollection<Document> getLegacyCourseCollection()
  {
    return mongoClient.getDatabase(databaseLegacy).getCollection("Courses");
  }

  public MongoCollection<Document> getLegacyCategoryCollection()
  {
    return mongoClient.getDatabase(databaseLegacy).getCollection("Course Categories");
  }

  public MongoCollection<Document> getLegacyCourseGroupCollection()
  {
    return mongoClient.getDatabase(databaseLegacy).getCollection("CourseGroup");
  }

  public MongoCollection<Document> getLegacyEnrollmentCollection()
  {
    return mongoClient.getDatabase(databaseLegacy).getCollection("CourseEnrollment");
  }

  public MongoCollection<Document> getLegacyAssessmentCollection()
  {
    return mongoClient.getDatabase(databaseLegacy).getCollection("CourseAssessments");
  }

  public MongoCollection<Document> getLegacyTestCollection()
  {
    return mongoClient.getDatabase(databaseLegacy).getCollection("CourseTests");
  }

  public MongoCollection<Document> getSurveyCollection()
  {
    return mongoClient.getDatabase(databaseNew).getCollection("mongoAssessment");
  }

  public MongoCollection<Document> getQuizCollection()
  {
    return mongoClient.getDatabase(databaseNew).getCollection("mongoQuiz");
  }

  public MongoCollection<Document> getExamCollection()
  {
    return mongoClient.getDatabase(databaseNew).getCollection("mongoExam");
  }

  public MongoCollection<Document> getExamCategoryCollection()
  {
    return mongoClient.getDatabase(databaseNew).getCollection("mongoExamCategory");
  }

  public MongoCollection<Document> getExamGroupCollection()
  {
    return mongoClient.getDatabase(databaseNew).getCollection("mongoExamGroup");
  }

  public MongoCollection<Document> getLearnerSuccessCollection()
  {
    return mongoClient.getDatabase(databaseNew).getCollection("mongoLearnerSuccess");
  }
}

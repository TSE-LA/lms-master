/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.infrastructure.lms.repository.mongo;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import org.apache.commons.lang3.Validate;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;

import mn.erin.lms.legacy.domain.lms.model.assessment.Assessment;
import mn.erin.lms.legacy.domain.lms.model.assessment.AssessmentId;
import mn.erin.lms.legacy.domain.lms.model.assessment.TestId;
import mn.erin.lms.legacy.domain.lms.model.course.CourseId;
import mn.erin.lms.legacy.domain.lms.repository.CourseAssessmentRepository;
import mn.erin.lms.legacy.domain.lms.repository.LMSRepositoryException;

import static com.mongodb.client.model.Filters.eq;

/**
 * @author Oyungerel Chuluunsukh.
 */
public class MongoCourseAssessmentRepository implements CourseAssessmentRepository
{
  private static final String COLLECTION_NAME = "CourseAssessments";
  private final MongoCollection<Document> courseAssessmentCollection;

  private static final String FIELD_ID = "_id";
  private static final String FIELD_COURSE_ID = "courseId";
  private static final String FIELD_COURSE_TEST_IDS = "testIds";

  private static final String ERROR_MSG_COURSE_ID = "Course id cannot be null!";
  private static final String ERROR_MSG_COURSE_TESTS = "Course test id cannot be null!";

  public MongoCourseAssessmentRepository(MongoTemplate mongoTemplate)
  {
    Objects.requireNonNull(mongoTemplate, "Mongo template cannot be null!");
    this.courseAssessmentCollection = mongoTemplate.getCollection(COLLECTION_NAME);
  }

  @Override
  public Assessment create(CourseId courseId, List<TestId> courseTests) throws LMSRepositoryException
  {
    Validate.notNull(courseId, ERROR_MSG_COURSE_ID);
    Validate.notNull(courseTests, ERROR_MSG_COURSE_TESTS);

    ObjectId objectId = new ObjectId(new Date());
    Document document = new Document();

    document.put(FIELD_ID, objectId);
    document.put(FIELD_COURSE_ID, courseId);
    document.put(FIELD_COURSE_TEST_IDS, courseTests);

    courseAssessmentCollection.insertOne(document);

    return createAssessment(courseTests, objectId.toHexString(), courseId);
  }

  @Override
  public Assessment update(CourseId courseId, List<TestId> courseTestList) throws LMSRepositoryException
  {
    Validate.notNull(courseId, ERROR_MSG_COURSE_ID);
    Validate.notNull(courseTestList, ERROR_MSG_COURSE_TESTS);
    Bson filter = eq(FIELD_COURSE_ID, courseId);

    Document setDocument = new Document(FIELD_COURSE_TEST_IDS, courseTestList);
    Document update = new Document("$set", setDocument);

    FindOneAndUpdateOptions options = new FindOneAndUpdateOptions();
    options.upsert(false);
    options.returnDocument(ReturnDocument.AFTER);

    Document updated = courseAssessmentCollection.findOneAndUpdate(filter, update, options);
    return toAssessment(updated);
  }


  @Override
  public Assessment get(CourseId courseId) throws LMSRepositoryException
  {
    Validate.notNull(courseId, ERROR_MSG_COURSE_ID);

    Bson filter = eq(FIELD_COURSE_ID, courseId);
    FindIterable<Document> documents = courseAssessmentCollection.find(filter);

    Iterator<Document> iterator = documents.iterator();

    if(iterator.hasNext())
    {
      Document document = iterator.next();
      return toAssessment(document);
    }else {
      throw new LMSRepositoryException("Course assessment with course ID : [" + courseId.getId() + "] doesn\'t exist");
    }
  }

  @Override
  public boolean delete(CourseId courseId) throws LMSRepositoryException
  {
    Validate.notNull(courseId, ERROR_MSG_COURSE_ID);
    Bson filter = eq(FIELD_COURSE_ID, courseId);

    Document deletedDoc = courseAssessmentCollection.findOneAndDelete(filter);
    // if it returns not null it means delete operation was successful.
    return null != deletedDoc;
  }

  private Assessment toAssessment(Document document)
  {
    String assessmentId = document.getObjectId(FIELD_ID).toHexString();
    Document courseIdDocument = (Document) document.get(FIELD_COURSE_ID);
    List<Document> courseTests = (List<Document>) document.get(FIELD_COURSE_TEST_IDS);

    CourseId courseId = new CourseId((String) courseIdDocument.get(FIELD_ID));
    return createAssessment(getCourseTest(courseTests), assessmentId, courseId);
  }

  private Assessment createAssessment(List<TestId> courseTests, String id, CourseId courseId)
  {
    Assessment assessment = new Assessment(new AssessmentId(id), courseId);
    for (TestId courseTest : courseTests)
    {
      assessment.addCourseTest(courseTest);
    }
    return assessment;
  }

  private List<TestId> getCourseTest(List<Document> courseTests)
  {
    List<TestId> courseTestList = new ArrayList<>();
    for (Document testAsDocument : courseTests)
    {
      TestId courseTest = new TestId((String) testAsDocument.get(FIELD_ID));
      courseTestList.add(courseTest);
    }
    return courseTestList;
  }

}

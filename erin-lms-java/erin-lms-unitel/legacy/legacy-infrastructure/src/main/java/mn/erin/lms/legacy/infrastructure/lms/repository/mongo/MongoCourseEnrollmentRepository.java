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
import java.util.Set;
import java.util.stream.Collectors;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import com.mongodb.client.result.DeleteResult;
import org.apache.commons.lang3.Validate;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;

import mn.erin.lms.legacy.domain.lms.model.course.CourseId;
import mn.erin.lms.legacy.domain.lms.model.enrollment.CourseEnrollment;
import mn.erin.lms.legacy.domain.lms.model.enrollment.CourseEnrollmentId;
import mn.erin.lms.legacy.domain.lms.model.enrollment.CourseEnrollmentState;
import mn.erin.lms.legacy.domain.lms.model.enrollment.LearnerId;
import mn.erin.lms.legacy.domain.lms.repository.CourseEnrollmentRepository;
import mn.erin.lms.legacy.domain.lms.repository.LMSRepositoryException;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gte;
import static com.mongodb.client.model.Filters.in;
import static com.mongodb.client.model.Filters.lte;

/**
 * @author Oyungerel Chuluunsukh.
 */
public class MongoCourseEnrollmentRepository implements CourseEnrollmentRepository
{
  private static final String COLLECTION_NAME = "CourseEnrollment";

  private static final String ERR_MSG_COURSE_ID = "Course ID cannot be null!";
  private static final String ERR_MSG_COURSE_ENROLLMENT_IDS = "Course enrollment IDs cannot be null!";
  private static final String ERR_MSG_LEARNER_ID = "Learner id cannot be null!";

  private static final String FIELD_ID = "_id";
  private static final String FIELD_LEARNER_ID = "learnerId";
  private static final String FIELD_COURSE_ID = "courseId";
  private static final String FIELD_ENROLLMENT_STATE = "enrollmentState";
  private static final String FIELD_ENROLLED_DATE = "enrolledDate";
  private static final String ERR_MSG_ENROLLMENT_STATE = "Enrollment state id cannot be null!";

  private final MongoCollection<Document> courseCollection;

  public MongoCourseEnrollmentRepository(MongoTemplate mongoTemplate)
  {
    this.courseCollection = mongoTemplate.getCollection(COLLECTION_NAME);
  }

  @Override
  public CourseEnrollment createEnrollment(CourseId courseId, LearnerId learnerId, CourseEnrollmentState state)
  {
    Validate.notNull(courseId, ERR_MSG_COURSE_ID);
    Validate.notNull(learnerId, ERR_MSG_LEARNER_ID);
    ObjectId objectId = new ObjectId(new Date());

    Document courseEnrollmentAsDocument = new Document();
    courseEnrollmentAsDocument.put(FIELD_ID, objectId);
    courseEnrollmentAsDocument.put(FIELD_COURSE_ID, courseId);
    courseEnrollmentAsDocument.put(FIELD_LEARNER_ID, learnerId);
    courseEnrollmentAsDocument.put(FIELD_ENROLLMENT_STATE, state.name());
    courseEnrollmentAsDocument.put(FIELD_ENROLLED_DATE, new Date());
    courseCollection.insertOne(courseEnrollmentAsDocument);
    CourseEnrollmentId courseEnrollmentId = new CourseEnrollmentId(objectId.toHexString());
    return new CourseEnrollment(courseEnrollmentId, learnerId, courseId, state);
  }

  @Override
  public CourseEnrollment getEnrollment(LearnerId learnerId, CourseId courseId)
  {
    Bson filter = validateGetBson(learnerId, courseId);
    FindIterable<Document> documents = courseCollection.find(filter);

    CourseEnrollment courseEnrollment = null;
    Iterator<Document> iterator = documents.iterator();
    while (iterator.hasNext())
    {
      Document document = iterator.next();
      courseEnrollment = mapToCourseEnrollment(document);
    }

    return courseEnrollment;
  }

  @Override
  public List<CourseEnrollment> getEnrollmentList(LearnerId learnerId)
  {
    Validate.notNull(learnerId, ERR_MSG_LEARNER_ID);
    Bson filter = eq(FIELD_LEARNER_ID, learnerId);
    return getCourseEnrollmentsWithFilter(filter);
  }

  @Override
  public List<CourseEnrollment> getEnrollmentList(CourseId courseId)
  {
    Validate.notNull(courseId, ERR_MSG_COURSE_ID);
    Bson filter = eq(FIELD_COURSE_ID, courseId);
    return getCourseEnrollmentsWithFilter(filter);
  }

  @Override
  public List<CourseEnrollment> getEnrollmentList(LearnerId learnerId, CourseEnrollmentState enrollmentState)
  {
    Validate.notNull(learnerId, ERR_MSG_LEARNER_ID);
    Validate.notNull(enrollmentState, ERR_MSG_ENROLLMENT_STATE);
    Bson filter = and(eq(FIELD_ENROLLMENT_STATE, enrollmentState.name()), eq(FIELD_LEARNER_ID, learnerId));

    return getCourseEnrollmentsWithFilter(filter);
  }

  @Override
  public List<CourseEnrollment> getEnrollments(Set<CourseId> courseIds)
  {
    Validate.notNull(courseIds, ERR_MSG_COURSE_ID);
    Bson filter = in(FIELD_COURSE_ID, courseIds);
    return getCourseEnrollmentsWithFilter(filter);
  }

  @Override
  public List<CourseEnrollment> getEnrollmentList(Date startDate, Date endDate)
  {
    Bson dateFilter = and(lte(FIELD_ENROLLED_DATE, endDate), gte(FIELD_ENROLLED_DATE, startDate));
    return getCourseEnrollmentsWithFilter(dateFilter);
  }

  @Override
  public CourseEnrollment updateEnrollmentState(LearnerId learnerId, CourseId courseId, CourseEnrollmentState state) throws LMSRepositoryException
  {
    Bson filter = validateGetBson(learnerId, courseId);
    Validate.notNull(state, ERR_MSG_ENROLLMENT_STATE);
    FindIterable<Document> documents = courseCollection.find(filter);
    if (documents == null)
    {
      throw new LMSRepositoryException("Course enrollment for course ID[" + courseId + "]not found!");
    }
    Document changeState = new Document(FIELD_ENROLLMENT_STATE, state.name());
    Document update = new Document("$set", changeState);

    FindOneAndUpdateOptions options = new FindOneAndUpdateOptions();
    options.upsert(false);
    options.returnDocument(ReturnDocument.AFTER);

    Document updated = courseCollection.findOneAndUpdate(filter, update, options);
    return mapToCourseEnrollment(updated);
  }

  @Override
  public boolean deleteEnrollments(CourseId courseId)
  {
    Validate.notNull(courseId, ERR_MSG_COURSE_ID);
    Bson filter = eq(FIELD_COURSE_ID, courseId);
    DeleteResult deleteResult = courseCollection.deleteMany(filter);
    /* if it returns not null it means delete operation was successful.*/
    return deleteResult.wasAcknowledged();
  }

  @Override
  public boolean deleteEnrollment(CourseId courseId, LearnerId learnerId)
  {
    Validate.notNull(courseId, ERR_MSG_COURSE_ID);
    Bson filter = and(eq(FIELD_COURSE_ID, courseId), eq(FIELD_LEARNER_ID, learnerId));
    DeleteResult deleteResult = courseCollection.deleteMany(filter);
    return deleteResult.wasAcknowledged();
  }

  @Override
  public void deleteEnrollments(Set<String> enrollmentIds) throws LMSRepositoryException
  {
    Validate.notNull(enrollmentIds, ERR_MSG_COURSE_ENROLLMENT_IDS);
    Bson filter = in(FIELD_ID, enrollmentIds.stream().map(ObjectId::new).collect(Collectors.toList()));
    DeleteResult deletedDocs = courseCollection.deleteMany(filter);

    if (deletedDocs.getDeletedCount() != enrollmentIds.size())
    {
      throw new LMSRepositoryException("Could not delete " + (enrollmentIds.size() - deletedDocs.getDeletedCount()) + " enrollments");
    }
  }

  private Bson validateGetBson(LearnerId learnerId, CourseId courseId)
  {
    Validate.notNull(learnerId, ERR_MSG_LEARNER_ID);
    Validate.notNull(courseId, ERR_MSG_COURSE_ID);

    return and(eq(FIELD_COURSE_ID, courseId), eq(FIELD_LEARNER_ID, learnerId));
  }

  private CourseEnrollment mapToCourseEnrollment(Document document)
  {
    Document learnerIdAsDocument = (Document) document.get(FIELD_LEARNER_ID);
    Document courseIdDocument = (Document) document.get(FIELD_COURSE_ID);
    CourseEnrollmentId id = new CourseEnrollmentId(document.getObjectId(FIELD_ID).toHexString());
    CourseEnrollmentState state = CourseEnrollmentState.valueOf((String) document.get(FIELD_ENROLLMENT_STATE));
    LearnerId learnerId = new LearnerId((String) learnerIdAsDocument.get(FIELD_ID));
    CourseId courseId = new CourseId((String) courseIdDocument.get(FIELD_ID));
    Date enrolledDate = (Date) document.get(FIELD_ENROLLED_DATE);
    CourseEnrollment courseEnrollment = new CourseEnrollment(id, learnerId, courseId, state);
    courseEnrollment.setEnrolledDate(enrolledDate);
    return courseEnrollment;
  }

  private List<CourseEnrollment> getCourseEnrollmentsWithFilter(Bson filter)
  {
    FindIterable<Document> documents = courseCollection.find(filter);
    Iterator<Document> iterator = documents.iterator();
    List<CourseEnrollment> enrollmentList = new ArrayList<>();
    while (iterator.hasNext())
    {
      Document document = iterator.next();
      enrollmentList.add(mapToCourseEnrollment(document));
    }
    return enrollmentList;
  }
}

package mn.erin.lms.legacy.infrastructure.lms.repository.mongo;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import org.apache.commons.lang3.Validate;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;

import mn.erin.lms.legacy.domain.lms.model.audit.CourseAudit;
import mn.erin.lms.legacy.domain.lms.model.audit.CourseAuditId;
import mn.erin.lms.legacy.domain.lms.model.course.CourseId;
import mn.erin.lms.legacy.domain.lms.model.enrollment.LearnerId;
import mn.erin.lms.legacy.domain.lms.repository.CourseAuditRepository;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class MongoCourseAuditRepository implements CourseAuditRepository
{
  private static final String FIELD_ID = "_id";
  private static final String FIELD_COURSE_ID = "courseId";
  private static final String FIELD_LEARNER_ID = "learnerId";

  private static final String ERR_MSG_COURSE_ID = "Course ID cannot be null!";
  private static final String ERR_MSG_LEARNER_ID = "Learner ID cannot be null!";
  private static final String ERR_MSG_ID = "ID is required!";

  private final MongoCollection<Document> collection;

  public MongoCourseAuditRepository(MongoTemplate mongoTemplate)
  {
    collection = mongoTemplate.getCollection("CourseAudit");
  }

  @Override
  public CourseAudit create(CourseId courseId, LearnerId learnerId)
  {
    Validate.notNull(courseId, ERR_MSG_COURSE_ID);
    Validate.notNull(learnerId, ERR_MSG_LEARNER_ID);

    ObjectId objectId = new ObjectId(new Date());

    Document courseAuditAsDocument = new Document();

    courseAuditAsDocument.put(FIELD_ID, objectId);
    courseAuditAsDocument.put(FIELD_LEARNER_ID, learnerId.getId());
    courseAuditAsDocument.put(FIELD_COURSE_ID, courseId.getId());

    collection.insertOne(courseAuditAsDocument);

    return new CourseAudit(CourseAuditId.valueOf(objectId.toHexString()), courseId, learnerId);
  }

  @Override
  public List<CourseAudit> listAll(LearnerId learnerId)
  {
    Validate.notNull(learnerId, ERR_MSG_LEARNER_ID);
    Bson filter = eq(FIELD_LEARNER_ID, learnerId.getId());

    FindIterable<Document> documents = collection.find(filter);
    return mapToCourseAudit(documents);
  }

  @Override
  public List<CourseAudit> listAll()
  {
    return mapToCourseAudit(collection.find());
  }

  @Override
  public boolean isExist(CourseId courseId, LearnerId learnerId)
  {
    Validate.notNull(courseId, ERR_MSG_COURSE_ID);
    Validate.notNull(learnerId, ERR_MSG_LEARNER_ID);
    Bson filter = and(eq(FIELD_COURSE_ID, courseId.getId()),eq(FIELD_LEARNER_ID, learnerId.getId()));
    return collection.find(filter).iterator().hasNext();
  }

  @Override
  public boolean delete(CourseAuditId id)
  {
    Validate.notNull(id, ERR_MSG_ID);
    Bson filter = eq(FIELD_ID, new ObjectId(id.getId()));
    Document deletedDoc = collection.findOneAndDelete(filter);
    return deletedDoc != null;
  }

  @Override
  public boolean delete(CourseId courseId, LearnerId learnerId)
  {
    Validate.notNull(courseId, FIELD_COURSE_ID);
    Validate.notNull(learnerId, FIELD_LEARNER_ID);
    Bson filter = and(eq(FIELD_COURSE_ID, courseId.getId()), eq(FIELD_LEARNER_ID, learnerId.getId()));
    Document deleteDoc = collection.findOneAndDelete(filter);
    return deleteDoc != null;
  }

  @Override
  public void delete(CourseId courseId)
  {
    Validate.notNull(courseId, ERR_MSG_COURSE_ID);
    Bson filter = eq(FIELD_COURSE_ID, courseId.getId());
    collection.deleteMany(filter);
  }

  private List<CourseAudit> mapToCourseAudit(FindIterable<Document> documents)
  {
    Iterator<Document> iterator = documents.iterator();

    List<CourseAudit> result = new ArrayList<>();
    while (iterator.hasNext())
    {
      Document document = iterator.next();

      CourseAuditId id = CourseAuditId.valueOf(document.getObjectId(FIELD_ID).toHexString());
      CourseId courseId = new CourseId((String) document.get(FIELD_COURSE_ID));
      LearnerId learnerId = new LearnerId((String) document.get(FIELD_LEARNER_ID));

      result.add(new CourseAudit(id, courseId, learnerId));
    }

    return result;
  }
}

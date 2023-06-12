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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.lms.legacy.domain.lms.model.course.CourseGroup;
import mn.erin.lms.legacy.domain.lms.model.course.CourseGroupId;
import mn.erin.lms.legacy.domain.lms.model.course.CourseId;
import mn.erin.lms.legacy.domain.lms.repository.CourseGroupRepository;

import static com.mongodb.client.model.Filters.eq;

/**
 * @author Zorig
 */
public class MongoCourseGroupRepository implements CourseGroupRepository
{
  protected static final String FIELD_ID = "_id";
  protected static final String FIELD_GROUP_ID = "groupId";
  protected static final String FIELD_COURSE_ID = "courseId";

  private static final String ERR_MSG_COURSE_ID = "Course id cannot be null!";
  private static final String ERR_MSG_GROUP_ID = "Group Id cannot be null!";
  private static final String ERR_MSG_COURSE_GROUP_ID = "Course Group Id cannot be null!";

  private static final Logger LOGGER = LoggerFactory.getLogger(MongoCourseGroupRepository.class);

  protected final MongoCollection<Document> courseGroupCollection;

  public MongoCourseGroupRepository(MongoCollection<Document> mongoCollection)
  {
    this.courseGroupCollection = mongoCollection;
  }

  @Override
  public CourseGroup create(CourseId courseId, String groupId)
  {
    Validate.notNull(courseId, ERR_MSG_COURSE_ID);
    Validate.notNull(groupId, ERR_MSG_GROUP_ID);

    ObjectId courseGroupId = new ObjectId(new Date());

    Document courseGroupDocumentToInsert = new Document();
    courseGroupDocumentToInsert.put(FIELD_ID, courseGroupId);
    courseGroupDocumentToInsert.put(FIELD_GROUP_ID, groupId);
    courseGroupDocumentToInsert.put(FIELD_COURSE_ID, courseId);

    courseGroupCollection.insertOne(courseGroupDocumentToInsert);

    CourseGroup courseGroupToReturn = new CourseGroup(new CourseGroupId(courseGroupId.toHexString()), groupId, courseId);
    return courseGroupToReturn;
  }

  @Override
  public List<CourseGroup> listGroupCourses(String groupId)
  {
    Validate.notBlank(groupId, "GroupID cannot be null or blank!");

    Bson filter = eq(FIELD_GROUP_ID, groupId);

    FindIterable<Document> documents = courseGroupCollection.find(filter);
    Iterator<Document> iterator = documents.iterator();

    List<CourseGroup> result = new ArrayList<>();

    while (iterator.hasNext())
    {
      Document document = iterator.next();
      result.add(mapToCourseGroup(document));
    }

    return result;
  }

  @Override
  public List<CourseGroup> listGroups(String courseId)
  {
    Validate.notBlank(courseId, "CourseID cannot be null or blank!");

    Bson filter = eq("courseId._id", courseId);

    FindIterable<Document> documents = courseGroupCollection.find(filter);
    Iterator<Document> iterator = documents.iterator();

    List<CourseGroup> result = new ArrayList<>();

    while (iterator.hasNext())
    {
      Document document = iterator.next();
      result.add(mapToCourseGroup(document));
    }

    return result;
  }

  @Override
  public boolean delete(CourseGroupId courseGroupId)
  {
    Validate.notNull(courseGroupId, ERR_MSG_COURSE_GROUP_ID);

    Bson filter = eq(FIELD_ID, new ObjectId(courseGroupId.getId()));

    Document deletedCourseGroupAsDocument = courseGroupCollection.findOneAndDelete(filter);
    return deletedCourseGroupAsDocument != null;
  }

  @Override
  public void delete(CourseId courseId)
  {
    Validate.notNull(courseId, ERR_MSG_COURSE_ID);
    Bson filter = eq("courseId._id", courseId.getId());
    courseGroupCollection.deleteMany(filter);
  }

  private CourseGroup mapToCourseGroup(Document document)
  {
    CourseGroupId courseGroupId = CourseGroupId.valueOf(document.getObjectId(FIELD_ID).toHexString());
    CourseId courseId = new CourseId((String) ((Document) document.get(FIELD_COURSE_ID)).get(FIELD_ID));
    String groupId = (String) document.get(FIELD_GROUP_ID);
    return new CourseGroup(courseGroupId, groupId, courseId);
  }
}

/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.infrastructure.lms.repository.mongo;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mongodb.client.FindIterable;
import org.apache.commons.lang3.Validate;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;

import mn.erin.lms.legacy.domain.lms.model.content.CourseContentId;
import mn.erin.lms.legacy.domain.lms.model.course.AuthorId;
import mn.erin.lms.legacy.domain.lms.model.course.Course;
import mn.erin.lms.legacy.domain.lms.model.course.CourseCategoryId;
import mn.erin.lms.legacy.domain.lms.model.course.CourseDetail;
import mn.erin.lms.legacy.domain.lms.model.course.CourseId;
import mn.erin.lms.legacy.domain.lms.model.course.PublishStatus;
import mn.erin.lms.legacy.domain.lms.model.course.UserGroup;
import mn.erin.lms.legacy.domain.lms.repository.CourseRepository;
import mn.erin.lms.legacy.domain.lms.repository.LMSRepositoryException;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gte;
import static com.mongodb.client.model.Filters.in;
import static com.mongodb.client.model.Filters.lte;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class MongoCourseRepository extends BaseMongoCourseRepository implements CourseRepository
{
  private static final String ERR_MSG_CATEGORY_ID = "Course category id cannot be null!";
  private static final String ERR_MSG_COURSE_DETAIL = "Course detail cannot be null!";

  public MongoCourseRepository(MongoTemplate mongoTemplate)
  {
    super(mongoTemplate);
  }

  @Override
  public Course createCourse(CourseDetail courseDetail, CourseCategoryId courseCategoryId, UserGroup userGroup)
  {
    Validate.notNull(courseDetail, ERR_MSG_COURSE_DETAIL);
    Validate.notNull(courseCategoryId, ERR_MSG_CATEGORY_ID);
    AuthorId authorId = new AuthorId(accessIdentityManagement.getCurrentUsername());

    ObjectId objectId = new ObjectId(new Date());

    Document courseAsDocument = new Document();

    courseAsDocument.put(FIELD_ID, objectId);
    courseAsDocument.put(FIELD_AUTHOR_ID, authorId);
    courseAsDocument.put(FIELD_COURSE_CATEGORY_ID, courseCategoryId);
    courseAsDocument.put(FIELD_COURSE_DETAIL, courseDetail);
    courseAsDocument.put(FIELD_USER_GROUP, userGroup);

    courseCollection.insertOne(courseAsDocument);

    CourseId courseId = new CourseId(objectId.toHexString());
    return new Course(courseId, courseCategoryId, authorId, courseDetail);
  }

  @Override
  public Course getCourse(CourseId courseId) throws LMSRepositoryException
  {
    Validate.notNull(courseId, ERR_MSG_COURSE_ID);

    Bson filter = eq(FIELD_ID, new ObjectId(courseId.getId()));

    FindIterable<Document> result = courseCollection.find(filter);

    if (result == null)
    {
      throw new LMSRepositoryException("Course with the ID: [" + courseId.getId() + "] was not found!");
    }

    Iterator<Document> iterator = result.iterator();

    if (iterator.hasNext())
    {
      Document document = iterator.next();
      return mapToCourse(document);
    }
    else
    {
      throw new LMSRepositoryException("Course with the ID: [" + courseId.getId() + "] was not found!");
    }
  }

  @Override
  public Course getCourse(CourseContentId contentId) throws LMSRepositoryException
  {
    Validate.notNull(contentId);

    Bson filter = eq(FIELD_COURSE_CONTENT_ID, eq(FIELD_ID, contentId.getId()));

    Iterable<Document> result = courseCollection.find(filter);

    Iterator<Document> iterator = result.iterator();

    if (!iterator.hasNext())
    {
      throw new LMSRepositoryException("Course with the content ID: [" + contentId.getId() + "] was not found!");
    }

    Document document = iterator.next();
    return mapToCourse(document);
  }

  @Override
  public boolean removeCourse(CourseId courseId)
  {
    Validate.notNull(courseId, ERR_MSG_COURSE_ID);

    Bson filter = eq(FIELD_ID, new ObjectId(courseId.getId()));

    Document deletedCourseAsDocument = courseCollection.findOneAndDelete(filter);
    return deletedCourseAsDocument != null;
  }

  @Override
  public List<Course> getCourseList(CourseCategoryId courseCategoryId)
  {
    Validate.notNull(courseCategoryId, ERR_MSG_CATEGORY_ID);

    Bson filter = eq(FIELD_COURSE_CATEGORY_ID, courseCategoryId);

    return getCourseList(filter);
  }

  @Override
  public List<Course> getCourseList(CourseCategoryId courseCategoryId, PublishStatus status)
  {
    Validate.notNull(courseCategoryId, ERR_MSG_CATEGORY_ID);
    Validate.notNull(status, "Publish status is required!");

    Bson filter = and(eq(FIELD_COURSE_CATEGORY_ID, courseCategoryId),
        eq(EMBEDDED_FIELD_PUBLISH_STATUS, status.name()));

    return getCourseList(filter);
  }

  @Override
  public List<Course> getCourseList(CourseCategoryId courseCategoryId, PublishStatus status, Date startDate, Date endDate)
  {
    Bson dateFilter = and(lte("courseDetail.createdDate", endDate), gte("courseDetail.createdDate", startDate));
    Bson filter = and(eq(FIELD_COURSE_CATEGORY_ID, courseCategoryId),
        eq(EMBEDDED_FIELD_PUBLISH_STATUS, status.name()), dateFilter);

    return getCourseList(filter);
  }

  @Override
  public List<Course> getCourseList(CourseCategoryId courseCategoryId, PublishStatus status, Date startDate, Date endDate, String state)
  {
    Bson dateFilter = and(lte("courseDetail.createdDate", endDate), gte("courseDetail.createdDate", startDate));
    Bson filter = and(eq(FIELD_COURSE_CATEGORY_ID, courseCategoryId),
        eq(EMBEDDED_FIELD_PUBLISH_STATUS, status.name()), eq("courseDetail.properties.state", state), dateFilter);
    return getCourseList(filter);
  }

  @Override
  public List<Course> getCourseList(CourseCategoryId courseCategoryId, Map<String, Object> properties)
  {
    Validate.notNull(courseCategoryId, ERR_MSG_CATEGORY_ID);
    Validate.notNull(properties, ERR_MSG_PROPERTIES);

    List<Bson> filters = getPropertiesFilters(properties);
    filters.add(eq(FIELD_COURSE_CATEGORY_ID, courseCategoryId));

    Bson filter = and(filters);

    return getCourseList(filter);
  }

  @Override
  public List<Course> getCourseList(CourseCategoryId courseCategoryId, PublishStatus status, Map<String, Object> properties)
  {
    Validate.notNull(courseCategoryId, ERR_MSG_CATEGORY_ID);
    Validate.notNull(status, "Publish status is required!");
    Validate.notNull(properties, ERR_MSG_PROPERTIES);

    List<Bson> filters = getPropertiesFilters(properties);
    filters.add(eq(FIELD_COURSE_CATEGORY_ID, courseCategoryId));
    filters.add(eq(EMBEDDED_FIELD_PUBLISH_STATUS, status.name()));

    Bson filter = and(filters);

    return getCourseList(filter);
  }

  @Override
  public List<Course> getCourseList(PublishStatus status)
  {
    Bson filter = eq(EMBEDDED_FIELD_PUBLISH_STATUS, PublishStatus.PUBLISHED.name());
    return getCourseList(filter);
  }

  @Override
  public List<Course> getCourseList(PublishStatus status, Date startDate, Date endDate)
  {
    Bson dateFilter = and(lte("courseDetail.createdDate", endDate), gte("courseDetail.createdDate", startDate));
    Bson filter = and(eq(EMBEDDED_FIELD_PUBLISH_STATUS, PublishStatus.PUBLISHED.name()), dateFilter);
    return getCourseList(filter);
  }

  @Override
  public List<Course> getCourseList(PublishStatus status, Date startDate, Date endDate, String state)
  {
    Bson dateFilter = and(lte("courseDetail.createdDate", endDate), gte("courseDetail.createdDate", startDate));
    Bson filter = and(eq(EMBEDDED_FIELD_PUBLISH_STATUS, PublishStatus.PUBLISHED.name()), eq("courseDetail.properties.state", state), dateFilter);
    return getCourseList(filter);
  }

  @Override
  public List<Course> getCourseList(Date startDate, Date endDate)
  {
    Bson dateFilter = and(lte("courseDetail.createdDate", endDate), gte("courseDetail.createdDate", startDate));
    return getCourseList(dateFilter);
  }

  @Override
  public List<Course> getCourseList(String group)
  {
    Bson filter = in("userGroup.groups", group);
    return getCourseList(filter);
  }

  @Override
  public List<Course> getCourseList(Set<String> enrolledGroups)
  {
    Bson filter = and(in("courseDetail.properties.enrolledGroups", enrolledGroups), eq(EMBEDDED_FIELD_PUBLISH_STATUS, PublishStatus.PUBLISHED.name()));
    return getCourseList(filter);
  }

  @Override
  public List<Course> getCourseList(Set<String> enrolledGroups, Date startDate, Date endDate, String state)
  {
    Bson dateFilter = and(lte("courseDetail.createdDate", endDate), gte("courseDetail.createdDate", startDate));
    Bson filter = and(in("courseDetail.properties.enrolledGroups", enrolledGroups),
        eq(EMBEDDED_FIELD_PUBLISH_STATUS, PublishStatus.PUBLISHED.name()), eq("courseDetail.properties.state", state), dateFilter);
    return getCourseList(filter);
  }

  @Override
  public List<Course> getCourseList(Set<String> enrolledGroups, Date startDate, Date endDate)
  {
    Bson dateFilter = and(lte("courseDetail.createdDate", endDate), gte("courseDetail.createdDate", startDate));
    Bson filter = and(in("courseDetail.properties.enrolledGroups", enrolledGroups),
        eq(EMBEDDED_FIELD_PUBLISH_STATUS, PublishStatus.PUBLISHED.name()), dateFilter);
    return getCourseList(filter);
  }

  @Override
  public Course update(CourseId courseId, CourseDetail courseDetail)
  {
    Validate.notNull(courseId, ERR_MSG_COURSE_ID);
    Validate.notNull(courseDetail, ERR_MSG_COURSE_DETAIL);

    Bson filter = eq(FIELD_ID, new ObjectId(courseId.getId()));

    Document setCourseDetail = new Document(FIELD_COURSE_DETAIL, courseDetail);
    Document updated = update(filter, setCourseDetail);

    return mapToCourse(updated);
  }

  @Override
  public Course update(CourseId courseId, UserGroup userGroup)
  {
    Validate.notNull(courseId, ERR_MSG_COURSE_ID);
    Validate.notNull(userGroup, "User Group cannot be null!");

    Bson filter = eq(FIELD_ID, new ObjectId(courseId.getId()));

    Document setCourseUserGroup = new Document(FIELD_USER_GROUP, userGroup);
    Document updated = update(filter, setCourseUserGroup);

    return mapToCourse(updated);
  }

  @Override
  public Course update(CourseId courseId, CourseCategoryId categoryId, CourseDetail courseDetail)
  {
    Validate.notNull(courseId, ERR_MSG_COURSE_ID);
    Validate.notNull(categoryId, ERR_MSG_CATEGORY_ID);
    Validate.notNull(courseDetail, ERR_MSG_COURSE_DETAIL);

    Bson filter = eq(FIELD_ID, new ObjectId(courseId.getId()));

    Document setCourseDetail = new Document(FIELD_COURSE_DETAIL, courseDetail);
    Document setCourseCategoryId = new Document(FIELD_COURSE_CATEGORY_ID, categoryId);

    update(filter, setCourseDetail);
    Document updatedCategory = update(filter, setCourseCategoryId);

    return mapToCourse(updatedCategory);
  }

  @Override
  public Course update(CourseId courseId, CourseContentId courseContentId)
  {
    Validate.notNull(courseId, ERR_MSG_COURSE_ID);
    Validate.notNull(courseContentId, "Course content ID cannot be null!");

    Bson filter = eq(FIELD_ID, new ObjectId(courseId.getId()));

    Document setCourseContent = new Document(FIELD_COURSE_CONTENT_ID, courseContentId);
    Document updated = update(filter, setCourseContent);

    return mapToCourse(updated);
  }

  @Override
  public Course update(CourseId courseId, PublishStatus publishStatus)
  {
    Validate.notNull(courseId, ERR_MSG_COURSE_ID);
    Validate.notNull(publishStatus, "Publish status cannot be null");

    Bson filter = eq(FIELD_ID, new ObjectId(courseId.getId()));

    Document changePublishStatus = new Document(EMBEDDED_FIELD_PUBLISH_STATUS, publishStatus.name());
    Document updated = update(filter, changePublishStatus);

    return mapToCourse(updated);
  }
}

/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.legacy.infrastructure.lms.repository.mongo;

import java.io.Serializable;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.data.mongodb.core.MongoTemplate;

import mn.erin.lms.base.aim.AccessIdentityManagement;
import mn.erin.lms.legacy.domain.lms.model.content.CourseContentId;
import mn.erin.lms.legacy.domain.lms.model.course.AuthorId;
import mn.erin.lms.legacy.domain.lms.model.course.Course;
import mn.erin.lms.legacy.domain.lms.model.course.CourseCategoryId;
import mn.erin.lms.legacy.domain.lms.model.course.CourseDetail;
import mn.erin.lms.legacy.domain.lms.model.course.CourseId;
import mn.erin.lms.legacy.domain.lms.model.course.CourseNote;
import mn.erin.lms.legacy.domain.lms.model.course.PublishStatus;
import mn.erin.lms.legacy.domain.lms.model.course.UserGroup;

import static com.mongodb.client.model.Filters.eq;

/**
 * @author Bat-Erdene Tsogoo.
 */
public abstract class BaseMongoCourseRepository
{
  private static final String COLLECTION_NAME = "Courses";

  protected static final String ERR_MSG_COURSE_ID = "Course ID cannot be null!";
  protected static final String ERR_MSG_CATEGORY_ID = "Course category ID cannot be null!";
  protected static final String ERR_MSG_PROPERTIES = "Course properties cannot be null or empty!";

  protected static final String FIELD_ID = "_id";
  protected static final String FIELD_AUTHOR_ID = "authorId";
  protected static final String FIELD_COURSE_CATEGORY_ID = "courseCategoryId";
  protected static final String FIELD_COURSE_CONTENT_ID = "courseContentId";
  protected static final String FIELD_COURSE_DETAIL = "courseDetail";
  protected static final String FIELD_USER_GROUP = "userGroup";
  protected static final String FIELD_PUBLISH_STATUS = "publishStatus";
  protected static final String FIELD_CREATED_DATE = "createdDate";
  protected static final String FIELD_MODIFIED_DATE = "modifiedDate";
  protected static final String FIELD_PROPERTIES = "properties";
  protected static final String FIELD_COURSE_TITLE = "title";
  protected static final String FIELD_COURSE_DESCRIPTION = "description";
  protected static final String FIELD_COURSE_NOTE = "note";

  protected static final String PROPERTY_USERS = "users";
  protected static final String PROPERTY_GROUPS = "groups";

  protected static final String EMBEDDED_FIELD_PUBLISH_STATUS = "courseDetail.publishStatus";
  protected static final String EMBEDDED_FIELD_PROPERTIES = "courseDetail.properties";

  protected AccessIdentityManagement accessIdentityManagement;

  protected final MongoCollection<Document> courseCollection;

  public BaseMongoCourseRepository(MongoTemplate mongoTemplate)
  {
    this.courseCollection = mongoTemplate.getCollection(COLLECTION_NAME);
  }

  @Inject
  public void setAccessIdentityManagement(AccessIdentityManagement accessIdentityManagement)
  {
    this.accessIdentityManagement = accessIdentityManagement;
  }

  protected List<Course> getCourseList()
  {
    FindIterable<Document> documents = courseCollection.find();
    return getCourseList(documents);
  }

  protected List<Course> getCourseList(Bson filter)
  {
    FindIterable<Document> documents = courseCollection.find(filter);
    return getCourseList(documents);
  }

  protected Course mapToCourse(Document document)
  {
    Document courseCategoryIdAsDocument = (Document) document.get(FIELD_COURSE_CATEGORY_ID);
    Document authorIdAsDocument = (Document) document.get(FIELD_AUTHOR_ID);
    Document courseDetailAsDocument = (Document) document.get(FIELD_COURSE_DETAIL);
    Document courseUserGroupAsDocument = (Document) document.get(FIELD_USER_GROUP);

    CourseId courseId = new CourseId(document.getObjectId(FIELD_ID).toHexString());
    AuthorId authorId = new AuthorId((String) authorIdAsDocument.get(FIELD_ID));
    CourseCategoryId courseCategoryId = new CourseCategoryId((String) courseCategoryIdAsDocument.get(FIELD_ID));
    CourseDetail courseDetail = mapToCourseDetail(courseDetailAsDocument);

    Course course = new Course(courseId, courseCategoryId, authorId, courseDetail);
    if (courseUserGroupAsDocument != null)
    {
      UserGroup userGroup = mapToUserGroups(courseUserGroupAsDocument);
      course.setUserGroup(userGroup);
    }

    Document courseContentIdAsDocument = (Document) document.get(FIELD_COURSE_CONTENT_ID);
    if (courseContentIdAsDocument != null)
    {
      CourseContentId courseContentId = new CourseContentId((String) courseContentIdAsDocument.get(FIELD_ID));
      course.setCourseContentId(courseContentId);
    }

    return course;
  }

  @SuppressWarnings("unchecked")
  protected UserGroup mapToUserGroups(Document document)
  {
    List<String> users = (List<String>) document.get(PROPERTY_USERS);
    List<String> groups = (List<String>) document.get(PROPERTY_GROUPS);
    UserGroup userGroup = new UserGroup();
    for (String user : users)
    {
      userGroup.addUser(user);
    }
    for (String group : groups)
    {
      userGroup.addGroup(group);
    }
    return userGroup;
  }

  @SuppressWarnings("unchecked")
  protected CourseDetail mapToCourseDetail(Document document)
  {
    String title = (String) document.get(FIELD_COURSE_TITLE);
    String description = (String) document.get(FIELD_COURSE_DESCRIPTION);
    PublishStatus publishStatus = PublishStatus.valueOf((String) document.get(FIELD_PUBLISH_STATUS));
    Date createdDate = (Date) document.get(FIELD_CREATED_DATE);
    Date modifiedDate = (Date) document.get(FIELD_MODIFIED_DATE);
    Map<String, Object> properties = (Map<String, Object>) document.get(FIELD_PROPERTIES);
    List<Document> notesAsDocuments = (List<Document>) document.get("notes");

    CourseDetail courseDetail = new CourseDetail(title);
    courseDetail.setDescription(description);

    if (notesAsDocuments != null)
    {
      for (Document noteAsDocument : notesAsDocuments)
      {
        courseDetail.addNote(new CourseNote((((Date) noteAsDocument.get("date")).toInstant()).atZone(ZoneId.systemDefault()).toLocalDate(),
            (String) noteAsDocument.get("note")));
      }
    }

    courseDetail.changePublishStatus(publishStatus);
    courseDetail.setCreatedDate(createdDate);
    courseDetail.setModifiedDate(modifiedDate);

    if (properties != null && !properties.isEmpty())
    {
      for (Map.Entry<String, Object> property : properties.entrySet())
      {
        courseDetail.addProperty(property.getKey(), (Serializable) property.getValue());
      }
    }

    return courseDetail;
  }

  protected Document update(Bson filter, Document documentToUpdate)
  {
    Document update = new Document("$set", documentToUpdate);

    FindOneAndUpdateOptions options = new FindOneAndUpdateOptions();
    options.upsert(false);
    options.returnDocument(ReturnDocument.AFTER);

    return courseCollection.findOneAndUpdate(filter, update, options);
  }

  protected List<Bson> getPropertiesFilters(Map<String, Object> properties)
  {
    List<Bson> filters = new ArrayList<>();

    for (Map.Entry<String, Object> property : properties.entrySet())
    {
      String field = EMBEDDED_FIELD_PROPERTIES + "." + property.getKey();
      Bson propertyFilter = eq(field, property.getValue());
      filters.add(propertyFilter);
    }

    return filters;
  }

  private List<Course> getCourseList(FindIterable<Document> documents)
  {
    if (documents == null)
    {
      return Collections.emptyList();
    }

    Iterator<Document> iterator = documents.iterator();

    List<Course> result = new ArrayList<>();

    while (iterator.hasNext())
    {
      Document document = iterator.next();

      result.add(mapToCourse(document));
    }

    return result;
  }
}

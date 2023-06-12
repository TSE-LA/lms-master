/*
 * (C)opyright, 2018, ERIN SYSTEMS LLC
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

import mn.erin.lms.legacy.domain.lms.model.content.Attachment;
import mn.erin.lms.legacy.domain.lms.model.content.AttachmentId;
import mn.erin.lms.legacy.domain.lms.model.content.CourseContent;
import mn.erin.lms.legacy.domain.lms.model.content.CourseContentId;
import mn.erin.lms.legacy.domain.lms.model.content.CourseModule;
import mn.erin.lms.legacy.domain.lms.model.content.CourseSection;
import mn.erin.lms.legacy.domain.lms.model.course.CourseId;
import mn.erin.lms.legacy.domain.lms.repository.CourseContentRepository;
import mn.erin.lms.legacy.domain.lms.repository.LMSRepositoryException;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

/**
 * Mongo Database repository implementation for {@link CourseContent}.
 * <p>
 * author Tamir Batmagnai.
 */
public class MongoCourseContentRepository implements CourseContentRepository
{
  private static final String COLLECTION_NAME = "CourseContents";

  private static final String NAME_PROPERTY = "name";
  private static final String INDEX_PROPERTY = "index";
  private static final String FILE_TYPE_PROPERTY = "fileType";
  private static final String SECTION_LIST = "sectionList";

  private static final String SET_KEY = "$set";
  private static final String ATTACHMENT_ID = "attachmentId";

  private static final String FIELD_ID = "_id";
  private static final String FIELD_NAME = "name";
  private static final String FIELD_COURSE_ID = "courseId";
  private static final String FIELD_COURSE_MODULES = "courseModules";
  private static final String FIELD_COURSE_ATTACHMENTS = "courseAttachments";
  private static final String FIELD_COURSE_ADDITIONAL_ATTACHMENT_FILES = "courseAdditionalFiles";

  private static final String ERROR_MSG_COURSE_ID = "Course id cannot be null!";
  private static final String ERROR_MSG_ATTACHMENT_ID = "Attachment id cannot be null!";
  private static final String ERROR_MSG_COURSE_MODULES = "Course modules cannot be null!";
  private static final String ERROR_MSG_COURSE_ATTACHMENTS = "Course attachments cannot be null!";

  private static final String ERROR_MSG_NOT_FOUND = "Course content was not found with COURSE ID: ";
  public static final String UNSET_KEY = "$unset";
  public static final String FIELD_TYPE = "type";
  private final MongoCollection<Document> courseContentCollection;

  public MongoCourseContentRepository(MongoTemplate mongoTemplate)
  {
    Objects.requireNonNull(mongoTemplate, "Mongo template cannot be null!");
    this.courseContentCollection = mongoTemplate.getCollection(COLLECTION_NAME);
  }

  @Override
  public CourseContent create(CourseId courseId, List<CourseModule> courseModules, List<Attachment> attachments, List<Attachment> additionalFiles)
  {
    Validate.notNull(courseId, ERROR_MSG_COURSE_ID);
    Validate.notNull(courseModules, ERROR_MSG_COURSE_MODULES);
    Validate.notNull(attachments, ERROR_MSG_COURSE_ATTACHMENTS);

    ObjectId objectId = new ObjectId(new Date());

    Document documentBson = createDocumentFrom(objectId, courseId, courseModules, attachments, additionalFiles);
    CourseContentId contentId = new CourseContentId(objectId.toHexString());
    courseContentCollection.insertOne(documentBson);

    return createCourseContent(contentId, courseId, courseModules, attachments, additionalFiles);
  }

  @Override
  public CourseContent get(CourseId courseId) throws LMSRepositoryException
  {
    Validate.notNull(courseId, ERROR_MSG_COURSE_ID);

    return filterById(courseId);
  }

  @Override
  public CourseContent update(CourseId courseId, List<CourseModule> courseModules, List<Attachment> attachments, List<Attachment> additionalIds)
      throws LMSRepositoryException
  {
    Validate.notNull(courseId, ERROR_MSG_COURSE_ID);
    Validate.notNull(courseModules, ERROR_MSG_COURSE_MODULES);

    Bson filter = eq(FIELD_COURSE_ID, courseId);

    return updateContent(courseId.getId(), filter, courseModules, attachments, additionalIds);
  }

  @Override
  public boolean deleteById(CourseId courseId)
  {
    Validate.notNull(courseId, ERROR_MSG_COURSE_ID);
    Bson filter = eq(FIELD_COURSE_ID, courseId);

    Document deletedDoc = courseContentCollection.findOneAndDelete(filter);
    // if returns not null which means delete operation is successful.
    return null != deletedDoc;
  }

  @Override
  public boolean deleteAllAttachmentsByCourseId(CourseId courseId, List<Attachment> attachments)
  {
    Validate.notNull(courseId, ERROR_MSG_COURSE_ID);
    Document setDocument = new Document(FIELD_COURSE_ATTACHMENTS, attachments);
    Bson filter = eq(FIELD_COURSE_ID, courseId);
    Document document = new Document();
    document.put(UNSET_KEY, setDocument);

    Document deletedDoc = courseContentCollection.findOneAndUpdate(filter, document, getFindOptions());
    return null != deletedDoc;
  }

  @Override
  public boolean deleteAllAdditionalFilesByCourseId(CourseId courseId, List<Attachment> attachments)
  {
    Validate.notNull(courseId, ERROR_MSG_COURSE_ID);
    Document setDocument = new Document(FIELD_COURSE_ADDITIONAL_ATTACHMENT_FILES, attachments);
    Bson filter = eq(FIELD_COURSE_ID, courseId);
    Document document = new Document();
    document.put(UNSET_KEY, setDocument);

    Document deletedDoc = courseContentCollection.findOneAndUpdate(filter, document, getFindOptions());
    return null != deletedDoc;
  }

  @Override
  public boolean deleteAttachmentByAttachmentId(CourseId courseId, List<Attachment> attachments)
  {
    Validate.notNull(courseId, ERROR_MSG_COURSE_ID);

    Document setDocument = new Document(FIELD_COURSE_ATTACHMENTS, attachments);
    List<Bson> filters = new ArrayList<>();
    filters.add(eq(FIELD_COURSE_ID, courseId));

    Bson filter = and(filters);
    Document updatedContent = new Document();
    updatedContent.put(SET_KEY, setDocument);
    Document deletedDoc = courseContentCollection.findOneAndUpdate(filter, updatedContent, getFindOptions());
    return null != deletedDoc;
  }

  @Override
  public boolean deleteAdditionalFileByAttachmentId(CourseId courseId, List<Attachment> attachments)
  {
    Validate.notNull(courseId, ERROR_MSG_COURSE_ID);

    Document setDocument = new Document(FIELD_COURSE_ADDITIONAL_ATTACHMENT_FILES, attachments);
    List<Bson> filters = new ArrayList<>();
    filters.add(eq(FIELD_COURSE_ID, courseId));

    Bson filter = and(filters);
    Document updatedContent = new Document();
    updatedContent.put(SET_KEY, setDocument);
    Document deletedDoc = courseContentCollection.findOneAndUpdate(filter, updatedContent, getFindOptions());
    return null != deletedDoc;
  }

  private CourseContent updateContent(String id, Bson filter, List<CourseModule> courseModules, List<Attachment> attachments, List<Attachment> additionalFiles)
      throws LMSRepositoryException
  {
    Document setDocument = new Document(
        FIELD_COURSE_MODULES, courseModules).append
        (FIELD_COURSE_ATTACHMENTS, attachments).append
        (FIELD_COURSE_ADDITIONAL_ATTACHMENT_FILES, additionalFiles);

    Document updatedDocument = new Document();
    updatedDocument.put(SET_KEY, setDocument);
    Document updatedContent = courseContentCollection.findOneAndUpdate(filter, updatedDocument, getFindOptions());

    if (null == updatedContent)
    {
      throw new LMSRepositoryException("Course content doesn't exist with course id : " + id);
    }

    return toCourseContent(updatedContent);
  }

  private FindOneAndUpdateOptions getFindOptions()
  {
    FindOneAndUpdateOptions options = new FindOneAndUpdateOptions();

    options.upsert(false);
    options.returnDocument(ReturnDocument.AFTER);

    return options;
  }

  private Document createDocumentFrom(ObjectId id, CourseId courseId, List<CourseModule> courseModules, List<Attachment> attachments,
      List<Attachment> additionalFiles)
  {
    Document documentBson = new Document();

    documentBson.put(FIELD_ID, id);
    documentBson.put(FIELD_COURSE_ID, courseId);
    documentBson.put(FIELD_COURSE_MODULES, courseModules);
    documentBson.put(FIELD_COURSE_ATTACHMENTS, attachments);
    documentBson.put(FIELD_COURSE_ADDITIONAL_ATTACHMENT_FILES, additionalFiles);

    return documentBson;
  }

  private CourseContent filterById(CourseId courseId) throws LMSRepositoryException
  {
    Bson filter = eq(FIELD_COURSE_ID, courseId);
    FindIterable<Document> documents = courseContentCollection.find(filter);

    if (null == documents)
    {
      throw new LMSRepositoryException("Course content doesn't exist with course ID : " + courseId);
    }

    Iterator<Document> iterator = documents.iterator();

    while (iterator.hasNext())
    {
      Document document = iterator.next();
      return toCourseContent(document);
    }
    return null;
  }

  private CourseContent toCourseContent(Document document)
  {
    CourseContent courseContent = null;

    courseContent = convertToContent(document);

    return courseContent;
  }

  private CourseContent convertToContent(Document document)
  {
    List<Document> attachmentsAsDocument = (List<Document>) document.get(FIELD_COURSE_ATTACHMENTS);
    List<Document> additionalFilesAsDocument = (List<Document>) document.get(FIELD_COURSE_ADDITIONAL_ATTACHMENT_FILES);
    Document courseIdAsDocument = (Document) document.get(FIELD_COURSE_ID);

    String courseId = (String) courseIdAsDocument.get(FIELD_ID);
    List<Attachment> attachments = getAttachmentsFiles(attachmentsAsDocument);
    List<Attachment> additionalFiles = getAttachmentsFiles(additionalFilesAsDocument);
    CourseContentId courseContentId = new CourseContentId(document.getObjectId(FIELD_ID).toHexString());

    List<Document> modules = (List<Document>) document.get(FIELD_COURSE_MODULES);
    List<CourseModule> courseModules = getCourseModules(modules);

    return createCourseContent(courseContentId, new CourseId(courseId), courseModules, attachments, additionalFiles);
  }

  private CourseContent createCourseContent(CourseContentId contentId, CourseId courseId,
      List<CourseModule> courseModules, List<Attachment> attachments, List<Attachment> additionalFiles)
  {

    CourseContent courseContent = new CourseContent(contentId, courseId);

    for (CourseModule courseModule : courseModules)
    {
      courseContent.addModule(courseModule);
    }
    if (attachments == null)
    {
      courseContent.setAttachmentsList(null);
    }
    else
    {
      for (Attachment attachment : attachments)
      {
        courseContent.setAttachmentsList(attachment);
      }
    }
    if (additionalFiles == null)
    {
      courseContent.setAdditionalFileList(null);
    }
    else
    {
      for (Attachment files : additionalFiles)
      {
        courseContent.setAdditionalFileList(files);
      }
    }

    return courseContent;
  }

  private List<Attachment> getAttachmentsFiles(List<Document> filesAsDocument)
  {
    List<Attachment> attachments = new ArrayList<>();
    if (filesAsDocument == null)
    {
      return null;
    }
    else
    {
      for (Document attachs : filesAsDocument)
      {
        String id = (String) ((Document) attachs.get(FIELD_ID)).get(FIELD_ID);
        String name = (String) attachs.get(FIELD_NAME);
        String type = (String) attachs.get(FIELD_TYPE);
        attachments.add(new Attachment(new AttachmentId(id), name, type));
      }
      return attachments;
    }
  }

  private List<CourseModule> getCourseModules(List<Document> modules)
  {
    List<CourseModule> courseModules = new ArrayList<>();

    for (Document moduleAsDocument : modules)
    {
      String moduleName = (String) moduleAsDocument.get(NAME_PROPERTY);
      Integer index = (Integer) moduleAsDocument.get(INDEX_PROPERTY);
      String fileType = (String) moduleAsDocument.get(FILE_TYPE_PROPERTY);

      List<Document> sectionsAsDocument = (List<Document>) moduleAsDocument.get(SECTION_LIST);

      List<CourseSection> courseSections = getCourseSections(sectionsAsDocument);
      CourseModule courseModule = new CourseModule(moduleName, index, fileType);

      for (CourseSection courseSection : courseSections)
      {
        courseModule.addSection(courseSection);
      }
      courseModules.add(courseModule);
    }
    return courseModules;
  }

  private List<CourseSection> getCourseSections(List<Document> sectionsAsDocument)
  {
    List<CourseSection> courseSections = new ArrayList<>();

    for (Document courseSection : sectionsAsDocument)
    {
      String sectionName = (String) courseSection.get(NAME_PROPERTY);
      Document attachIdAsDocument = (Document) courseSection.get(ATTACHMENT_ID);
      Integer sectionIndex = (Integer) courseSection.get(INDEX_PROPERTY);

      String attachIdAsString = (String) attachIdAsDocument.get(FIELD_ID);
      courseSections.add(new CourseSection(sectionName, new AttachmentId(attachIdAsString), sectionIndex));
    }

    return courseSections;
  }
}

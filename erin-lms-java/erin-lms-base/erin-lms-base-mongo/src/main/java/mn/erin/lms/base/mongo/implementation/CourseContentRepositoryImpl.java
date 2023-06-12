package mn.erin.lms.base.mongo.implementation;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.lms.base.domain.model.content.Attachment;
import mn.erin.lms.base.domain.model.content.AttachmentId;
import mn.erin.lms.base.domain.model.content.CourseContent;
import mn.erin.lms.base.domain.model.content.CourseContentId;
import mn.erin.lms.base.domain.model.content.CourseModule;
import mn.erin.lms.base.domain.model.content.CourseSection;
import mn.erin.lms.base.domain.model.course.CourseId;
import mn.erin.lms.base.domain.repository.CourseContentRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.mongo.document.content.MongoCourseAttachment;
import mn.erin.lms.base.mongo.document.content.MongoCourseContent;
import mn.erin.lms.base.mongo.document.content.MongoCourseModule;
import mn.erin.lms.base.mongo.document.content.MongoCourseSection;
import mn.erin.lms.base.mongo.repository.MongoCourseContentRepository;
import mn.erin.lms.base.mongo.util.IdGenerator;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class CourseContentRepositoryImpl implements CourseContentRepository
{
  private static final Logger LOGGER = LoggerFactory.getLogger(CourseContentRepositoryImpl.class);

  private final MongoCourseContentRepository mongoCourseContentRepository;

  public CourseContentRepositoryImpl(MongoCourseContentRepository mongoCourseContentRepository)
  {
    this.mongoCourseContentRepository = mongoCourseContentRepository;
  }

  @Override
  public CourseContent create(CourseId courseId, List<CourseModule> courseModules, List<Attachment> attachments) throws LmsRepositoryException
  {
    MongoCourseContent mongoCourseContent = mapMongoCourseContent(courseId, courseModules, attachments);
    if (mongoCourseContentRepository.existsByCourseId(courseId.getId()))
    {
      mongoCourseContentRepository.delete(mongoCourseContentRepository.findFirstByCourseId(courseId.getId()));
    }
    mongoCourseContentRepository.save(mongoCourseContent);

    CourseContent courseContent = new CourseContent(CourseContentId.valueOf(mongoCourseContent.getId()), courseId);

    courseModules.forEach(courseContent::addModule);
    attachments.forEach(courseContent::addAttachment);
    return courseContent;
  }

  @Override
  public CourseContent fetchById(CourseId courseId) throws LmsRepositoryException
  {
    MongoCourseContent mongoCourseContent = getMongoCourseContent(courseId);

    return mapToCourseContent(mongoCourseContent);
  }

  @Override
  public CourseContent update(CourseId courseId, List<CourseModule> courseModules, List<Attachment> attachments) throws LmsRepositoryException
  {
    MongoCourseContent mongoCourseContent = getMongoCourseContent(courseId);
    List<MongoCourseModule> mongoCourseModules = mapCourseModules(courseModules);

    mongoCourseContent.setModules(mongoCourseModules);
    mongoCourseContent.addAttachments(mapToMongoCourseAttachment(attachments));

    mongoCourseContentRepository.save(mongoCourseContent);

    return mapToCourseContent(mongoCourseContent);
  }

  @Override
  public CourseContent updateAttachment(CourseId courseId, List<Attachment> attachments) throws LmsRepositoryException
  {
    MongoCourseContent mongoCourseContent = getMongoCourseContent(courseId);
    mongoCourseContent.setAttachments(mapToMongoCourseAttachment(attachments));

    return mapToCourseContent(mongoCourseContentRepository.save(mongoCourseContent));
  }

  @Override
  public boolean delete(CourseId courseId)
  {
    MongoCourseContent mongoCourseContent;
    try
    {
      mongoCourseContent = getMongoCourseContent(courseId);
    }
    catch (LmsRepositoryException e)
    {
      LOGGER.error(e.getMessage(), e);
      return false;
    }

    mongoCourseContentRepository.delete(mongoCourseContent);

    return true;
  }

  @Override
  public boolean exists(CourseId courseId)
  {
    try
    {
      getMongoCourseContent(courseId);
    }
    catch (LmsRepositoryException e)
    {
      return false;
    }
    return true;
  }

  private CourseContent mapToCourseContent(MongoCourseContent mongoCourseContent)
  {
    CourseContent courseContent = new CourseContent(CourseContentId.valueOf(mongoCourseContent.getId()), CourseId.valueOf(mongoCourseContent.getCourseId()));
    mongoCourseContent.getModules().forEach(mongoCourseModule -> courseContent.addModule(mapToCourseModule(mongoCourseModule)));
    mongoCourseContent.getAttachments().forEach(mongoCourseAttachment -> courseContent.addAttachment(mapToCourseAttachment(mongoCourseAttachment)));
    return courseContent;
  }

  private CourseModule mapToCourseModule(MongoCourseModule mongoCourseModule)
  {
    CourseModule courseModule = new CourseModule(mongoCourseModule.getName(), mongoCourseModule.getIndex(), mongoCourseModule.getFileType(),
        mongoCourseModule.getModuleFolderId() != null ? mongoCourseModule.getModuleFolderId() : String.valueOf(mongoCourseModule.getIndex()));
    mongoCourseModule.getSections().forEach(mongoCourseSection -> courseModule.addSection(mapToCourseSection(mongoCourseSection)));
    // Asserting index for folderId is necessary for the older courses so please do not delete that part
    return courseModule;
  }

  private CourseSection mapToCourseSection(MongoCourseSection mongoCourseSection)
  {
    return new CourseSection(mongoCourseSection.getName(), AttachmentId.valueOf(mongoCourseSection.getAttachmentId()),
        mongoCourseSection.getIndex());
  }

  private MongoCourseContent getMongoCourseContent(CourseId courseId) throws LmsRepositoryException
  {
    MongoCourseContent courseContent = mongoCourseContentRepository.findFirstByCourseId(courseId.getId());
    if (courseContent == null)
    {
      throw new LmsRepositoryException("No course content was found for the course: [" + courseId.getId() + "]");
    }

    return courseContent;
  }

  private MongoCourseContent mapMongoCourseContent(CourseId courseId, List<CourseModule> courseModules, List<Attachment> attachments)
  {
    String id = IdGenerator.generateId();
    return new MongoCourseContent(id, courseId.getId(), mapToMongoCourseAttachment(attachments), mapCourseModules(courseModules));
  }

  private List<MongoCourseModule> mapCourseModules(List<CourseModule> courseModules)
  {
    List<MongoCourseModule> mongoCourseModules = new ArrayList<>();

    for (CourseModule courseModule : courseModules)
    {
      mongoCourseModules.add(new MongoCourseModule(courseModule.getName(), courseModule.getIndex(), mapCourseSection(courseModule.getSectionList()), courseModule.getFileType(),
          courseModule.getModuleFolderId()));
    }

    return mongoCourseModules;
  }

  private List<MongoCourseSection> mapCourseSection(List<CourseSection> courseSections)
  {
    List<MongoCourseSection> mongoCourseSections = new ArrayList<>();

    for (CourseSection courseSection : courseSections)
    {
      mongoCourseSections.add(new MongoCourseSection(courseSection.getName(), courseSection.getIndex(), courseSection.getAttachmentId().getId()));
    }

    return mongoCourseSections;
  }

  private List<MongoCourseAttachment> mapToMongoCourseAttachment(List<Attachment> attachments)
  {
    List<MongoCourseAttachment> mongoCourseAttachments = new ArrayList<>();
    for (Attachment attachment : attachments)
    {
      mongoCourseAttachments.add(new MongoCourseAttachment(attachment.getAttachmentId().getId(), attachment.getName(), attachment.getAttachmentFolderId()));
    }
    return mongoCourseAttachments;
  }

  private Attachment mapToCourseAttachment(MongoCourseAttachment mongoCourseAttachment)
  {
    return new Attachment(
            AttachmentId.valueOf(mongoCourseAttachment.getAttachmentId()),
            mongoCourseAttachment.getName(),
            mongoCourseAttachment.getAttachmentFolderId()
    );
  }
}

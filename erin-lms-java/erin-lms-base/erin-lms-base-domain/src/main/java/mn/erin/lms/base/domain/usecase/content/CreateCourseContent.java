package mn.erin.lms.base.domain.usecase.content;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.dms.repository.DMSRepositoryException;
import mn.erin.lms.base.aim.user.Author;
import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.base.domain.model.CourseConstants;
import mn.erin.lms.base.domain.model.content.Attachment;
import mn.erin.lms.base.domain.model.content.CourseContent;
import mn.erin.lms.base.domain.model.content.CourseModule;
import mn.erin.lms.base.domain.model.course.Course;
import mn.erin.lms.base.domain.model.course.CourseId;
import mn.erin.lms.base.domain.repository.CourseContentRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsFileSystemService;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.content.dto.CourseContentDto;
import mn.erin.lms.base.domain.usecase.content.dto.CourseContentInput;
import mn.erin.lms.base.domain.usecase.course.CourseUseCase;

import static mn.erin.lms.base.domain.util.ContentUtils.getModuleDetails;
import static mn.erin.lms.base.domain.util.ContentUtils.setSectionFiles;
import static mn.erin.lms.base.domain.util.ContentUtils.toCourseModules;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Authorized(users = { Author.class })
public class CreateCourseContent extends CourseUseCase<CourseContentInput, CourseContentDto>
{
  private final CourseContentRepository courseContentRepository;
  private final LmsFileSystemService lmsFileSystemService;

  private String contentFolderId;
  private String attachmentFolderId;

  public CreateCourseContent(LmsRepositoryRegistry lmsRepositoryRegistry, LmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
    this.courseContentRepository = lmsRepositoryRegistry.getCourseContentRepository();
    this.lmsFileSystemService = lmsServiceRegistry.getLmsFileSystemService();
  }

  @Override
  public CourseContentDto execute(CourseContentInput input) throws UseCaseException
  {
    try
    {
      return executeImpl(input);
    }
    catch (UseCaseException e)
    {
      lmsFileSystemService.deleteFolder(this.contentFolderId);
      throw e;
    }
  }

  private CourseContentDto executeImpl(CourseContentInput input) throws UseCaseException
  {
    Validate.notNull(input);
    CourseId courseId = CourseId.valueOf(input.getCourseId());
    validateCourse(courseId);

    //Get course folder if it doesn't exist create
    String courseFolderId = lmsFileSystemService.getCourseFolderId(courseId.getId());
    Course course = getCourse(courseId);
    if (courseFolderId == null)
    {
      courseFolderId = lmsFileSystemService.createCourseFolder(courseId.getId(), course.getCourseDetail().getTitle());
    }

    if (courseFolderId == null)
    {
      throw new UseCaseException("Could not create course folder!");
    }

    Map<String, String> courseProperties = course.getCourseDetail().getProperties();
    courseProperties.put(CourseConstants.PROPERTY_COURSE_FOLDER_ID, courseFolderId);
    try
    {
      courseRepository.updateCourseProperties(courseId, courseProperties);
    }
    catch (LmsRepositoryException e)
    {
      throw new UseCaseException("Could not save course properties!: [{}]", e);
    }
    updateCourse(course.getCourseId(), course.getCourseCategoryId(), course.getCourseDetail(), course.getCourseType(), course.getAssessmentId(),
        course.getCertificateId());

    //Check if the course content exists delete and create new
    try
    {
      this.contentFolderId = lmsFileSystemService.getCourseContentFolderId(courseFolderId);
      this.attachmentFolderId = lmsFileSystemService.getCourseAttachmentFolderId(courseFolderId);
      lmsFileSystemService.deleteFolder(this.contentFolderId);
      lmsFileSystemService.deleteFolder(this.attachmentFolderId);
    }
    catch (DMSRepositoryException ignored)
    {
      // Deletes only if exist (no need to do anything)
    }

    this.contentFolderId = lmsFileSystemService.createCourseContentFolder(courseFolderId);
    this.attachmentFolderId = lmsFileSystemService.createCourseAttachmentFolder(courseFolderId);

    if (StringUtils.isBlank(this.contentFolderId) || StringUtils.isBlank(this.attachmentFolderId))
    {
      throw new UseCaseException("Could not create course content or attachment folder!");
    }

    //Create content modules inside course content folder
    List<LmsFileSystemService.ModuleDetail> moduleDetails = getModuleDetails(input.getSectionFiles(), input.getModuleInfoList());

    Map<String, String> sectionFileIdMap = lmsFileSystemService.createModule(contentFolderId, moduleDetails);
    setSectionFiles(input.getModuleInfoList(), sectionFileIdMap);
    List<Attachment> attachments = uploadAttachment(input.getAttachments(), this.attachmentFolderId);

    List<CourseModule> courseModules = toCourseModules(input.getModuleInfoList());

    //Create course content repo
    CourseContent courseContent = createCourseContent(courseId, courseModules, attachments);
    return getCourseContent(courseContent.getCourseId());
  }

  private CourseContent createCourseContent(CourseId courseId, List<CourseModule> courseModules,
      List<Attachment> attachments) throws UseCaseException
  {
    try
    {
      return courseContentRepository.create(courseId, courseModules, attachments);
    }
    catch (LmsRepositoryException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }
  }

  private CourseContentDto getCourseContent(CourseId courseId) throws UseCaseException
  {
    GetCourseContent getCourseContent = new GetCourseContent(courseContentRepository);
    return getCourseContent.execute(courseId.getId());
  }
}

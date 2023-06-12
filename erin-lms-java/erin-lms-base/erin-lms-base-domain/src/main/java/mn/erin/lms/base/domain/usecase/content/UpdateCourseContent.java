package mn.erin.lms.base.domain.usecase.content;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.dms.repository.DMSRepositoryException;
import mn.erin.lms.base.domain.model.Authorized;
import mn.erin.lms.base.domain.model.content.Attachment;
import mn.erin.lms.base.domain.model.content.CourseContent;
import mn.erin.lms.base.domain.model.content.CourseModule;
import mn.erin.lms.base.domain.model.course.CourseId;
import mn.erin.lms.base.aim.user.Author;
import mn.erin.lms.base.domain.repository.CourseContentRepository;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsFileSystemService;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.course.CourseUseCase;
import mn.erin.lms.base.domain.usecase.content.dto.CourseContentDto;
import mn.erin.lms.base.domain.usecase.content.dto.CourseContentInput;
import mn.erin.lms.base.domain.util.ContentUtils;

import static mn.erin.lms.base.domain.util.ContentUtils.getModuleDetails;
import static mn.erin.lms.base.domain.util.ContentUtils.setSectionFiles;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Authorized(users = { Author.class })
public class UpdateCourseContent extends CourseUseCase<CourseContentInput, CourseContentDto>
{
  private final CourseContentRepository courseContentRepository;
  private final LmsFileSystemService lmsFileSystemService;

  public UpdateCourseContent(LmsRepositoryRegistry lmsRepositoryRegistry,
      LmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
    this.courseContentRepository = lmsRepositoryRegistry.getCourseContentRepository();
    this.lmsFileSystemService = lmsServiceRegistry.getLmsFileSystemService();
  }

  @Override
  public CourseContentDto execute(CourseContentInput input) throws UseCaseException
  {
    Validate.notNull(input);
    CourseId courseId = CourseId.valueOf(input.getCourseId());
    validateCourse(courseId);

    List<LmsFileSystemService.ModuleDetail> moduleDetails = getModuleDetails(input.getSectionFiles(), input.getModuleInfoList());
    String courseFolderId = lmsFileSystemService.getCourseFolderId(courseId.getId());
    String courseContentFolderId;
    String courseAttachmentFolderId;
    try
    {
      courseContentFolderId = lmsFileSystemService.getCourseContentFolderId(courseFolderId);
      courseAttachmentFolderId = lmsFileSystemService.getCourseAttachmentFolderId(courseFolderId);
    }
    catch (DMSRepositoryException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }

    Map<String, String> updatedSectionFileIdMap = lmsFileSystemService.updateModuleFolders(courseContentFolderId, moduleDetails);

    setSectionFiles(input.getModuleInfoList(), updatedSectionFileIdMap);
    List<Attachment> attachments = uploadAttachment(input.getAttachments(), courseAttachmentFolderId);
    List<CourseModule> courseModules = ContentUtils.toCourseModules(input.getModuleInfoList());

    try
    {
      CourseContent updatedCourseContent = courseContentRepository.update(CourseId.valueOf(input.getCourseId()), courseModules, attachments);
      return new CourseContentDto(updatedCourseContent.getCourseId().getId(),
          updatedCourseContent.getAttachments(), ContentUtils.toModuleInfos(updatedCourseContent.getModules()));
    }
    catch (LmsRepositoryException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }
  }
}

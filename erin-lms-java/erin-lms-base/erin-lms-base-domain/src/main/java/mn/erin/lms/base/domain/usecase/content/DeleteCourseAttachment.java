package mn.erin.lms.base.domain.usecase.content;

import java.util.List;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.dms.model.document.DocumentId;
import mn.erin.lms.base.domain.model.content.Attachment;
import mn.erin.lms.base.domain.model.content.CourseContent;
import mn.erin.lms.base.domain.model.content.CourseModule;
import mn.erin.lms.base.domain.model.course.CourseId;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.content.dto.CourseContentDto;
import mn.erin.lms.base.domain.usecase.content.dto.DeleteCourseAttachmentInput;
import mn.erin.lms.base.domain.usecase.content.dto.ModuleInfo;
import mn.erin.lms.base.domain.usecase.course.CourseUseCase;
import mn.erin.lms.base.domain.util.ContentUtils;

public class DeleteCourseAttachment extends CourseUseCase<DeleteCourseAttachmentInput, CourseContentDto>
{
  public DeleteCourseAttachment(LmsRepositoryRegistry lmsRepositoryRegistry, LmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
  }

  @Override
  public CourseContentDto execute(DeleteCourseAttachmentInput input) throws UseCaseException
  {
    try
    {
      Validate.notBlank(input.getCourseId(), "Course ID can not be null or blank!");
      Validate.notBlank(input.getAttachmentId());
      CourseContent courseContent = lmsRepositoryRegistry.getCourseContentRepository().fetchById(CourseId.valueOf(input.getCourseId()));
      Attachment deletingAttachment = null;
      for (Attachment attachment : courseContent.getAttachments())
      {
        if (attachment.getAttachmentId().getId().equals(input.getAttachmentId()))
        {
          deletingAttachment = attachment;
          lmsServiceRegistry.getLmsFileSystemService().deleteDocument(DocumentId.valueOf(input.getAttachmentId()));
        }
      }
      courseContent.getAttachments().remove(deletingAttachment);


      return toOutput(lmsRepositoryRegistry.getCourseContentRepository().updateAttachment(CourseId.valueOf(input.getCourseId()), courseContent.getAttachments()));
    }
    catch (IllegalArgumentException | NullPointerException | LmsRepositoryException e)
    {
      throw new UseCaseException(e.getMessage());
    }
  }

  private CourseContentDto toOutput(CourseContent courseContent)
  {
    CourseId courseId = courseContent.getCourseId();

    List<CourseModule> courseModules = courseContent.getModules();
    List<ModuleInfo> moduleInfos = ContentUtils.toModuleInfos(courseModules);

    return new CourseContentDto(courseId.getId(), courseContent.getAttachments(), moduleInfos);
  }
}

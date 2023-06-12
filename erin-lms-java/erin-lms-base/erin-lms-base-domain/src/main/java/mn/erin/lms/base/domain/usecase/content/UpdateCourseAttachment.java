package mn.erin.lms.base.domain.usecase.content;

import java.util.List;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.dms.repository.DMSRepositoryException;
import mn.erin.lms.base.domain.model.content.Attachment;
import mn.erin.lms.base.domain.model.content.CourseContent;
import mn.erin.lms.base.domain.model.course.CourseId;
import mn.erin.lms.base.domain.repository.LmsRepositoryException;
import mn.erin.lms.base.domain.repository.LmsRepositoryRegistry;
import mn.erin.lms.base.domain.service.LmsServiceRegistry;
import mn.erin.lms.base.domain.usecase.content.dto.CourseContentDto;
import mn.erin.lms.base.domain.usecase.content.dto.UpdateCourseAttachmentInput;
import mn.erin.lms.base.domain.usecase.course.CourseUseCase;
import mn.erin.lms.base.domain.util.ContentUtils;

public class UpdateCourseAttachment extends CourseUseCase<UpdateCourseAttachmentInput, CourseContentDto>
{
  public UpdateCourseAttachment(LmsRepositoryRegistry lmsRepositoryRegistry, LmsServiceRegistry lmsServiceRegistry)
  {
    super(lmsRepositoryRegistry, lmsServiceRegistry);
  }

  @Override
  public CourseContentDto execute(UpdateCourseAttachmentInput input) throws UseCaseException
  {
    try
    {
      Validate.notBlank(input.getCourseId());
      String courseFolderId = lmsServiceRegistry.getLmsFileSystemService().getCourseFolderId(input.getCourseId());
      String attachmentFolderId = lmsServiceRegistry.getLmsFileSystemService().getCourseAttachmentFolderId(courseFolderId);
      List<Attachment> attachments = uploadAttachment(input.getFiles(), attachmentFolderId);
      attachments.addAll(lmsRepositoryRegistry.getCourseContentRepository().fetchById(CourseId.valueOf(input.getCourseId())).getAttachments());
      CourseContent updatedContent =  lmsRepositoryRegistry.getCourseContentRepository().updateAttachment(CourseId.valueOf(input.getCourseId()), attachments);
      return new CourseContentDto(updatedContent.getCourseId().getId(),
          updatedContent.getAttachments(), ContentUtils.toModuleInfos(updatedContent.getModules()));
    }
    catch (DMSRepositoryException | NullPointerException | IllegalArgumentException | LmsRepositoryException e)
    {
      throw new UseCaseException(e.getMessage());
    }
  }
}

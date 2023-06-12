package mn.erin.lms.legacy.domain.lms.usecase.course_content.delete_course_attachment;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.lms.legacy.domain.lms.model.content.Attachment;
import mn.erin.lms.legacy.domain.lms.model.content.CourseContent;
import mn.erin.lms.legacy.domain.lms.model.course.CourseId;
import mn.erin.lms.legacy.domain.lms.repository.CourseContentRepository;
import mn.erin.lms.legacy.domain.lms.repository.LMSRepositoryException;

/**
 * @author Erdenetulga
 */
public class DeleteCourseAttachment implements UseCase<DeleteCourseAttachmentInput, DeleteCourseAttachmentOutput>
{
  private final CourseContentRepository contentRepository;

  public DeleteCourseAttachment(CourseContentRepository contentRepository)
  {
    this.contentRepository = Objects.requireNonNull(contentRepository, "Course content repository cannot be null!");
  }

  @Override
  public DeleteCourseAttachmentOutput execute(DeleteCourseAttachmentInput input)
  {
    Validate.notNull(input, "Delete course content input cannot be null!");
    Validate.notNull(input.getCourseId(), "Course content id cannot be null!");
    Validate.notNull(input.getAttachmentId(), "Course attachment id cannot be null!");
    List<Attachment> attachments = new ArrayList<>();
    boolean isDeleted;
    try
    {
      CourseContent courseContent = contentRepository.get(new CourseId(input.getCourseId()));
      attachments = courseContent.getAttachmentsList();
      attachments.removeIf(e -> (input.getAttachmentId().equals(e.getId().getId())));
    }
    catch (LMSRepositoryException e)
    {
      e.printStackTrace();
    }

    isDeleted = contentRepository.deleteAttachmentByAttachmentId(new CourseId(input.getCourseId()), attachments);

    return new DeleteCourseAttachmentOutput(isDeleted);
  }

  public DeleteCourseAttachmentOutput executeAdditionalFiles(DeleteCourseAttachmentInput input)
  {
    Validate.notNull(input, "Delete course content input cannot be null!");
    Validate.notNull(input.getCourseId(), "Course content id cannot be null!");
    Validate.notNull(input.getAttachmentId(), "Course attachment id cannot be null!");
    List<Attachment> attachments = new ArrayList<>();
    boolean isDeleted;
    try
    {
      CourseContent courseContent = contentRepository.get(new CourseId(input.getCourseId()));
      attachments = courseContent.getAdditionalFileList();
      attachments.removeIf(e -> (input.getAttachmentId().equals(e.getId().getId())));
    }
    catch (LMSRepositoryException e)
    {
      e.printStackTrace();
    }

    isDeleted = contentRepository.deleteAdditionalFileByAttachmentId(new CourseId(input.getCourseId()), attachments);

    return new DeleteCourseAttachmentOutput(isDeleted);
  }
}

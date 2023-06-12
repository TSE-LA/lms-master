package mn.erin.lms.legacy.domain.lms.usecase.course_content.delete_course_attachment;

import java.util.Objects;

/**
 * @author Erdenetulga
 */
public class DeleteCourseAttachmentInput
{
  private final String courseId;
  private final String attachmentId;

  public DeleteCourseAttachmentInput(String courseId, String attachmentId)
  {
    this.courseId = Objects.requireNonNull(courseId, "Course id cannot be null!");
    this.attachmentId = Objects.requireNonNull(attachmentId, "Attachment id cannot be null!");
  }

  public String getCourseId()
  {
    return courseId;
  }

  public String getAttachmentId()
  {
    return attachmentId;
  }
}

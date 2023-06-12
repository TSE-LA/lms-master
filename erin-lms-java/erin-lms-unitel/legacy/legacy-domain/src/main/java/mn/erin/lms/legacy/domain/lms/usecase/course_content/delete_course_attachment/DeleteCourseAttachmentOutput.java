package mn.erin.lms.legacy.domain.lms.usecase.course_content.delete_course_attachment;

import java.util.Objects;

/**
 * @author Erdenetulga
 */
public class DeleteCourseAttachmentOutput
{
  private final boolean isDeleted;

  public DeleteCourseAttachmentOutput(boolean isDeleted)
  {
    this.isDeleted = Objects.requireNonNull(isDeleted, "Delete course content output must be boolean!");
  }

  public boolean isDeleted()
  {
    return isDeleted;
  }
}

package mn.erin.lms.base.domain.usecase.content.dto;

import java.io.File;
import java.util.List;

public class UpdateCourseAttachmentInput
{
  private String courseId;
  private List<File> files;

  public UpdateCourseAttachmentInput(String courseId, List<File> files)
  {
    this.courseId = courseId;
    this.files = files;
  }

  public String getCourseId()
  {
    return courseId;
  }

  public List<File> getFiles()
  {
    return files;
  }
}

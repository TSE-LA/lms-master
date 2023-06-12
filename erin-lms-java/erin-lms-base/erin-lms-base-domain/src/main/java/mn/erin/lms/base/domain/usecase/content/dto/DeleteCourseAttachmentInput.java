package mn.erin.lms.base.domain.usecase.content.dto;

public class DeleteCourseAttachmentInput
{
  private String courseId;
  private String attachmentId;

  public String getCourseId()
  {
    return courseId;
  }

  public void setCourseId(String courseId)
  {
    this.courseId = courseId;
  }

  public String getAttachmentId()
  {
    return attachmentId;
  }

  public void setAttachmentId(String attachmentId)
  {
    this.attachmentId = attachmentId;
  }
}

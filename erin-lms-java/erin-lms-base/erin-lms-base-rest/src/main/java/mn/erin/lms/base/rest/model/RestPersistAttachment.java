package mn.erin.lms.base.rest.model;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class RestPersistAttachment
{
  private String courseId;
  private String attachmentId;

  public String getCourseId()
  {
    return courseId;
  }

  public String getAttachmentId()
  {
    return attachmentId;
  }

  public void setCourseId(String courseId)
  {
    this.courseId = courseId;
  }

  public void setAttachmentId(String attachmentId)
  {
    this.attachmentId = attachmentId;
  }
}

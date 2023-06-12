package mn.erin.lms.base.domain.model.content;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class Attachment
{
  private final AttachmentId attachmentId;
  private String name;
  private String attachmentFolderId;

  public Attachment(AttachmentId attachmentId, String name, String attachmentFolderId)
  {
    this.attachmentId = attachmentId;
    this.name = name;
    this.attachmentFolderId = attachmentFolderId;
  }

  public AttachmentId getAttachmentId()
  {
    return attachmentId;
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public String getAttachmentFolderId()
  {
    return attachmentFolderId;
  }

  public void setAttachmentFolderId(String attachmentFolderId)
  {
    this.attachmentFolderId = attachmentFolderId;
  }
}

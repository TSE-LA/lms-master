package mn.erin.lms.base.mongo.document.content;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author Temuulen Naranbold
 */
@Document
public class MongoCourseAttachment
{
  @Id
  private String attachmentId;
  private String name;
  private String attachmentFolderId;

  public MongoCourseAttachment()
  {
  }

  public MongoCourseAttachment(String attachmentId, String name, String attachmentFolderId)
  {
    this.attachmentId = attachmentId;
    this.name = name;
    this.attachmentFolderId = attachmentFolderId;
  }

  public String getAttachmentId()
  {
    return attachmentId;
  }

  public void setAttachmentId(String attachmentId)
  {
    this.attachmentId = attachmentId;
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

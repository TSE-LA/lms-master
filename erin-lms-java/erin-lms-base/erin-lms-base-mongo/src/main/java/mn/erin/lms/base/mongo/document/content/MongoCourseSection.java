package mn.erin.lms.base.mongo.document.content;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class MongoCourseSection
{
  private String name;
  private Integer index;
  private String attachmentId;

  public MongoCourseSection()
  {
  }

  public MongoCourseSection(String name, Integer index, String attachmentId)
  {
    this.name = name;
    this.index = index;
    this.attachmentId = attachmentId;
  }

  public String getName()
  {
    return name;
  }

  public Integer getIndex()
  {
    return index;
  }

  public String getAttachmentId()
  {
    return attachmentId;
  }
}

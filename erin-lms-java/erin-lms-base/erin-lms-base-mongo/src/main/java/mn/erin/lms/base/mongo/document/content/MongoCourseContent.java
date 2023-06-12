package mn.erin.lms.base.mongo.document.content;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author Bat-Erdene Tsogoo.
 */
@Document
public class MongoCourseContent
{
  @Id
  private String id;

  @Indexed
  private String courseId;

  private List<MongoCourseAttachment> attachments;

  private List<MongoCourseModule> modules;

  public MongoCourseContent()
  {
  }

  public MongoCourseContent(String id, String courseId, List<MongoCourseAttachment> attachments,
      List<MongoCourseModule> modules)
  {
    this.id = id;
    this.courseId = courseId;
    this.attachments = attachments;
    this.modules = modules;
  }

  public void setAttachments(List<MongoCourseAttachment> attachments)
  {
    this.attachments = attachments;
  }

  public void addAttachments(List<MongoCourseAttachment> attachments)
  {
    this.attachments.addAll(attachments);
  }

  public void setModules(List<MongoCourseModule> modules)
  {
    this.modules = modules;
  }

  public String getId()
  {
    return id;
  }

  public String getCourseId()
  {
    return courseId;
  }

  public List<MongoCourseAttachment> getAttachments()
  {
    return attachments;
  }

  public List<MongoCourseModule> getModules()
  {
    return modules;
  }
}

package mn.erin.lms.base.rest.model;

import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotNull;

public class RestUpdateAttachment
{
  @NotNull
  private String courseId;
  private List<String> attachmentIds = new ArrayList<>();

  public String getCourseId()
  {
    return courseId;
  }

  public void setCourseId(String courseId)
  {
    this.courseId = courseId;
  }

  public List<String> getAttachmentIds()
  {
    return attachmentIds;
  }

  public void setAttachmentIds(List<String> attachmentIds)
  {
    this.attachmentIds = attachmentIds;
  }
}

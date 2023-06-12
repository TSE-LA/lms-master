package mn.erin.lms.base.domain.usecase.content.dto;

import java.util.List;

import mn.erin.lms.base.domain.model.content.Attachment;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class CourseContentDto
{
  private final String courseId;
  private final List<Attachment> attachments;
  private final List<ModuleInfo> moduleInfoList;

  public CourseContentDto(String courseId, List<Attachment> attachments,
      List<ModuleInfo> moduleInfoList)
  {
    this.courseId = courseId;
    this.attachments = attachments;
    this.moduleInfoList = moduleInfoList;
  }

  public String getCourseId()
  {
    return courseId;
  }

  public List<Attachment> getAttachments()
  {
    return attachments;
  }

  public List<ModuleInfo> getModuleInfoList()
  {
    return moduleInfoList;
  }
}

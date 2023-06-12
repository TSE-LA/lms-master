package mn.erin.lms.base.rest.model;

import java.util.List;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class RestCourseContent
{
  private List<RestModule> modules;
  private List<String> attachmentIdList;

  public List<RestModule> getModules()
  {
    return modules;
  }

  public void setModules(List<RestModule> modules)
  {
    this.modules = modules;
  }

  public List<String> getAttachmentIdList()
  {
    return attachmentIdList;
  }

  public void setAttachmentIdList(List<String> attachmentIdList)
  {
    this.attachmentIdList = attachmentIdList;
  }
}

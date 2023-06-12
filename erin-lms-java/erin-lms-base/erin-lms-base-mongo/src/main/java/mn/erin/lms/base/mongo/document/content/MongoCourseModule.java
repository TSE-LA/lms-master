package mn.erin.lms.base.mongo.document.content;

import java.util.List;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class MongoCourseModule
{
  private String name;
  private Integer index;
  private List<MongoCourseSection> sections;
  private String fileType;
  private String moduleFolderId;
  public MongoCourseModule()
  {
  }

  public MongoCourseModule(String name, Integer index, List<MongoCourseSection> sections, String fileType, String moduleFolderId)
  {
    this.name = name;
    this.index = index;
    this.sections = sections;
    this.fileType = fileType;
    this.moduleFolderId = moduleFolderId;
  }

  public String getName()
  {
    return name;
  }

  public Integer getIndex()
  {
    return index;
  }

  public List<MongoCourseSection> getSections()
  {
    return sections;
  }

  public String getFileType() { return fileType;}

  public String getModuleFolderId() { return moduleFolderId;}
}

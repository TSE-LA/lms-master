package mn.erin.lms.base.rest.model;

import java.util.List;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class RestModule
{
  private String name;
  private Integer index;
  private List<RestSection> sectionList;
  private String fileType;
  private String moduleFolderId;
  public String getName()
  {
    return name;
  }

  public String getModuleFolderId() { return moduleFolderId;}
  public void setName(String name)
  {
    this.name = name;
  }

  public Integer getIndex()
  {
    return index;
  }

  public void setIndex(Integer index)
  {
    this.index = index;
  }

  public List<RestSection> getSectionList()
  {
    return sectionList;
  }

  public void setSectionList(List<RestSection> sectionList)
  {
    this.sectionList = sectionList;
  }

  public void setFileType(String fileType) {this.fileType = fileType;}

  public String getFileType() {return this.fileType;}
}

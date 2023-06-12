package mn.erin.lms.base.domain.usecase.content.dto;

import java.util.List;

import org.apache.commons.lang3.Validate;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class ModuleInfo
{
  private final String name;
  private final List<SectionInfo> sectionInfoList;
  private Integer index;
  private String fileType;
  private String moduleFolderId;
  public ModuleInfo(String name, List<SectionInfo> sectionInfoList)
  {
    this.name = Validate.notBlank(name);
    this.sectionInfoList = Validate.notEmpty(sectionInfoList);
  }

  public ModuleInfo(String name, List<SectionInfo> sectionInfoList, Integer index)
  {
    this.name = Validate.notBlank(name);
    this.sectionInfoList = Validate.notEmpty(sectionInfoList);
    this.index = Validate.notNull(index);
  }

  public ModuleInfo(String name, List<SectionInfo> sectionInfoList, Integer index, String fileType, String moduleFolderID)
  {
    this.name = Validate.notBlank(name);
    this.sectionInfoList = Validate.notEmpty(sectionInfoList);
    this.index = Validate.notNull(index);
    this.fileType = fileType;
    this.moduleFolderId = moduleFolderID;
  }

  public String getModuleFolderId() { return moduleFolderId;}
  public String getName()
  {
    return name;
  }

  public List<SectionInfo> getSectionInfoList()
  {
    return sectionInfoList;
  }

  public Integer getIndex()
  {
    return index;
  }

  public String getFileType() { return fileType;}
}

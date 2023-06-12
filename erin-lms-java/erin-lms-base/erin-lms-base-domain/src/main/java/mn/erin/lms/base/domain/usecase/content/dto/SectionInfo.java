package mn.erin.lms.base.domain.usecase.content.dto;

import org.apache.commons.lang3.Validate;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class SectionInfo
{
  private final String name;
  private String fileId;

  private Integer index;

  public SectionInfo(String name, String fileId)
  {
    this.name = Validate.notBlank(name);
    this.fileId = Validate.notBlank(fileId);
  }

  public SectionInfo(String name, String fileId, Integer index)
  {
    this.name = Validate.notBlank(name);
    this.index = Validate.notNull(index);
    this.fileId = fileId;
  }

  public String getName()
  {
    return name;
  }

  public String getFileId()
  {
    return fileId;
  }

  public Integer getIndex()
  {
    return index;
  }

  public void setFileId(String fileId)
  {
    this.fileId = fileId;
  }

  public void setIndex(Integer index)
  {
    this.index = index;
  }
}

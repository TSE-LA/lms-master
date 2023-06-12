package mn.erin.lms.base.rest.model;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class RestSection
{
  private String name;
  private Integer index;
  private String fileId;

  public RestSection()
  {
  }

  public RestSection(String name, Integer index, String fileId)
  {
    this.name = name;
    this.index = index;
    this.fileId = fileId;
  }

  public String getName()
  {
    return name;
  }

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

  public String getFileId()
  {
    return fileId;
  }

  public void setFileId(String fileId)
  {
    this.fileId = fileId;
  }
}

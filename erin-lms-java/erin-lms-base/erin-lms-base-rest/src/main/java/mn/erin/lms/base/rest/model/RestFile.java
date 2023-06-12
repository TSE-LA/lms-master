package mn.erin.lms.base.rest.model;

/**
 * @author Oyungerel Chuluunsukh.
 */
public class RestFile
{
  private String name;
  private String path;
  private String fileId;

  public RestFile()
  {
  }

  public RestFile(String name, String path, String fileId)
  {
    this.name = name;
    this.path = path;
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

  public String getPath()
  {
    return path;
  }

  public void setPath(String path)
  {
    this.path = path;
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

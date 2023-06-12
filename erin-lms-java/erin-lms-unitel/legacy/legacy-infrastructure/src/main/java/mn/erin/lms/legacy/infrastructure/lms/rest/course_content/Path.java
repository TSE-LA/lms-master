package mn.erin.lms.legacy.infrastructure.lms.rest.course_content;

public class Path{
  private String path;
  private Integer index;

  public Path(String path, Integer index)
  {
    this.path = path;
    this.index = index;
  }

  public String getPath()
  {
    return path;
  }

  public Integer getIndex()
  {
    return index;
  }
}

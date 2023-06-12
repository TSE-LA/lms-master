package mn.erin.lms.base.domain.usecase.content.dto;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class PathDto
{
  private final String path;
  private final Integer index;

  public PathDto(String path, Integer index)
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

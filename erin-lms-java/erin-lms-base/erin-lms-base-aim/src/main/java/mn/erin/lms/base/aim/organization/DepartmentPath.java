package mn.erin.lms.base.aim.organization;

/**
 * @author Oyungerel Chuluunsukh
 **/
public class DepartmentPath
{
  private final String path;
  private final int level;

  public DepartmentPath(String path, int level)
  {
    this.path = path;
    this.level = level;
  }

  public String getPath()
  {
    return path;
  }

  public int getLevel()
  {
    return level;
  }
}

package mn.erin.lms.legacy.infrastructure.lms.rest.course_content;

import java.util.ArrayList;
import java.util.List;

public class ModulePaths
{
  private List<Path> paths = new ArrayList<>();
  private Integer index;


  public void addPaths(Path path)
  {
    if (null != path)
    {
      this.paths.add(path);
    }
  }

  public List<Path> getPaths()
  {
    return paths;
  }

  public Integer getIndex()
  {
    return index;
  }

  public void setIndex(Integer index)
  {
    this.index = index;
  }
}


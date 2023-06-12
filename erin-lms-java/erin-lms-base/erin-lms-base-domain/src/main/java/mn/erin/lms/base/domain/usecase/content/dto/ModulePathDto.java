package mn.erin.lms.base.domain.usecase.content.dto;

import java.util.List;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class ModulePathDto
{
  private final List<PathDto> paths;
  private final Integer index;

  public ModulePathDto(List<PathDto> paths, Integer index)
  {
    this.paths = paths;
    this.index = index;
  }

  public List<PathDto> getPaths()
  {
    return paths;
  }

  public Integer getIndex()
  {
    return index;
  }
}

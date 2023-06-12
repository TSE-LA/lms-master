package mn.erin.lms.base.scorm;

import java.util.Map;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class SCOModel
{
  private String name;
  private String path;

  private Map<String, RuntimeDataInfo> runtimeData;

  public SCOModel(String name, String path, Map<String, RuntimeDataInfo> runtimeData)
  {
    this.name = name;
    this.path = path;
    this.runtimeData = runtimeData;
  }

  public String getName()
  {
    return name;
  }

  public String getPath()
  {
    return path;
  }

  public Map<String, RuntimeDataInfo> getRuntimeData()
  {
    return runtimeData;
  }
}

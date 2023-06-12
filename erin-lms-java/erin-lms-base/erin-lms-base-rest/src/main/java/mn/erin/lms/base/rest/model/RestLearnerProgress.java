package mn.erin.lms.base.rest.model;

import java.util.Map;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class RestLearnerProgress
{
  private String moduleName;
  private Map<String, Object> data;

  public String getModuleName()
  {
    return moduleName;
  }

  public void setModuleName(String moduleName)
  {
    this.moduleName = moduleName;
  }

  public Map<String, Object> getData()
  {
    return data;
  }

  public void setData(Map<String, Object> data)
  {
    this.data = data;
  }
}

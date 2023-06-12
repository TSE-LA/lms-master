package mn.erin.lms.base.domain.usecase.tracking.dto;

import java.util.Map;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class SaveLearnerProgressInput
{
  private final String courseId;
  private final String moduleName;
  private final Map<String, Object> data;

  public SaveLearnerProgressInput(String courseId, String moduleName, Map<String, Object> data)
  {
    this.courseId = courseId;
    this.moduleName = moduleName;
    this.data = data;
  }

  public String getCourseId()
  {
    return courseId;
  }

  public String getModuleName()
  {
    return moduleName;
  }

  public Map<String, Object> getData()
  {
    return data;
  }
}

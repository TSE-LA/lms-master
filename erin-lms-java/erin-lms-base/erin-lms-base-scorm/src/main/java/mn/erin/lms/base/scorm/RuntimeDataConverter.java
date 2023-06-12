package mn.erin.lms.base.scorm;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import mn.erin.lms.base.scorm.model.DataModel;

/**
 * @author Bat-Erdene Tsogoo.
 */
public final class RuntimeDataConverter
{
  private RuntimeDataConverter()
  {
  }

  public static Map<String, RuntimeDataInfo> convertRuntimeData(Map<DataModel, Serializable> data)
  {
    Map<String, RuntimeDataInfo> result = new HashMap<>();

    for (Map.Entry<DataModel, Serializable> entry : data.entrySet())
    {
      DataModel dataModel = entry.getKey();
      if (dataModel != null)
      {
        result.put(dataModel.getName(),
            RuntimeDataInfo.of(String.valueOf(entry.getValue()), dataModel.getConstraint()));
      }
    }

    return result;
  }
}

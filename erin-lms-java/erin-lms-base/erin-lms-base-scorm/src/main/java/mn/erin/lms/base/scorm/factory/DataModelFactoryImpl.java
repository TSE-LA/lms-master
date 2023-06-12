/*
 * (C)opyright, 2019, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.lms.base.scorm.factory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.lms.base.scorm.constants.DataModelConstants;
import mn.erin.lms.base.scorm.model.DataModel;
import mn.erin.lms.base.scorm.model.DataModelConstraint;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class DataModelFactoryImpl implements DataModelFactory
{
  private static final Logger LOGGER = LoggerFactory.getLogger(DataModelFactoryImpl.class);

  private static final String DATA_MODEL_CSV = "data-model.csv";
  private static final String DELIMITER = ",";

  private static Map<String, DataModel> dataModelMap = new HashMap<>();

  public DataModelFactoryImpl()
  {
    if (dataModelMap == null || dataModelMap.isEmpty())
    {
      readDataModelCSV();
    }
  }

  @Override
  public DataModel getInstance(String dataModel)
  {
    if (!dataModelMap.containsKey(dataModel))
    {
      throw new UnsupportedDataModelException(dataModel);
    }

    return dataModelMap.get(dataModel);
  }

  @Override
  public Set<DataModel> getAllInstances()
  {
    Set<DataModel> allDataModels = new HashSet<>();

    for (Map.Entry<String, DataModel> dataModel : dataModelMap.entrySet())
    {
      allDataModels.add(dataModel.getValue());
    }

    return allDataModels;
  }

  private void readDataModelCSV()
  {
    InputStream is = getClass().getClassLoader().getResourceAsStream(DATA_MODEL_CSV);

    if (is == null)
    {
      LOGGER.error("{} file not found!", DATA_MODEL_CSV );
      return;
    }

    try (BufferedReader reader = new BufferedReader(new InputStreamReader(is)))
    {
      String line;
      while ((line = reader.readLine()) != null)
      {
        String[] data = line.split(DELIMITER);
        String dataModelName = data[0];
        DataModelConstraint constraint = DataModelConstraint.valueOf(data[1]);

        DataModel dataModel = new DataModel(dataModelName, constraint);

        String defaultValue = "unknown".equals(data[2]) && DataModelConstants.CMI_COMMENT_1.equals(dataModelName) ? "" : data[2];
        dataModel.setDefaultValue(defaultValue);

        dataModelMap.put(dataModelName, dataModel);
      }
    }
    catch (IOException e)
    {
      LOGGER.error(e.getMessage(), e);
    }
  }
}
